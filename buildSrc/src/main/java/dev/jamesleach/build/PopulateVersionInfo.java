package dev.jamesleach.build;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Populate a version.properties file
 */
class PopulateVersionInfo implements Action<Task> {

    // Prop keys
    private static final String BUILD_TIME_MILLIS = "buildTimeMillis";
    private static final String GROUP_HASH = "groupHash";
    private static final String GIT_BRANCH = "gitBranch";
    private static final String GIT_COMMIT = "gitCommit";
    private static final String GIT_FILES_DIFF = "gitFilesDiff";
    private static final String GIT_COMMIT_DESCRIPTION = "gitCommitDescription";

    private static final int LOG_MAX_PROP_LENGTH_CHARS = 500;


    private final PluginUtils utils;
    private final Path outputDir;
    private final String outputFile;
    private final Supplier<FileCollection> hashFiles;

    PopulateVersionInfo(PluginUtils utils, Supplier<FileCollection> hashFiles) {
        this.utils = utils;
        this.hashFiles = hashFiles;
        this.outputDir = Paths.get(utils.project().getBuildDir().getPath()).resolve("version/");
        this.outputFile = "version.properties";
    }


    Path getOutputDir() {
        return outputDir;
    }

    String getOutputFile() {
        return outputFile;
    }

    @Override
    public void execute(Task task) {

        // File hashes
        Set<String> fileHashes = hashFiles.get().getFiles().stream()
                .filter(file -> !file.toPath().endsWith(getOutputFile()))
                .map(file -> {
                    try {
                        return file.toPath().toString() + ":" + Files.asByteSource(file).hash(Hashing.sha256()).toString();
                    } catch (IOException e) {
                        throw new RuntimeException("Exception generating file hash", e);
                    }
                })
                .collect(Collectors.toSet());

        String joinedFileHashes = Joiner.on(",").join(fileHashes);
        String groupHash = fileHashes.size() + ":" + Hashing.sha256().hashString(joinedFileHashes, Charsets.UTF_8).toString();

        // Git info
        String gitBranch = utils.execRootDir("git", "symbolic-ref", "--short", "HEAD");
        String gitCommit = utils.execRootDir("git", "rev-parse", "--short", "HEAD");
        String gitCommitDescription = utils.execRootDir("git", "log", "-1", "--oneline");
        String gitFilesDiff = utils.execRootDir("git", "diff", "--name-only");

        // Populate properties
        Properties propsOut = new Properties();
        propsOut.setProperty(GIT_FILES_DIFF, gitFilesDiff);
        propsOut.setProperty(BUILD_TIME_MILLIS, String.valueOf(ZonedDateTime.now().toInstant().toEpochMilli()));
        propsOut.setProperty(GROUP_HASH, groupHash);
        propsOut.setProperty(GIT_BRANCH, gitBranch);
        propsOut.setProperty(GIT_COMMIT, gitCommit);
        propsOut.setProperty(GIT_COMMIT_DESCRIPTION, gitCommitDescription);

        // Log
        String logLine = Joiner.on(" ").join(propsOut.entrySet().stream()
                .map(e -> e.getKey() + "[" + StringUtils.abbreviate(
                        e.getValue() == null ? "" : e.getValue().toString(), LOG_MAX_PROP_LENGTH_CHARS) + "]")
                .collect(Collectors.toList()));
        utils.project().getLogger().lifecycle(logLine);

        // Create the project file
        try {

            if (!outputDir.toFile().exists()) {
                if (!outputDir.toFile().mkdir()) {
                    throw new IOException("Could not create output directory");
                }
            }

            try (OutputStream output = new FileOutputStream(outputDir.resolve(outputFile).toFile())) {
                propsOut.store(output, null);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not write version.properties file", e);
        }
    }
}
