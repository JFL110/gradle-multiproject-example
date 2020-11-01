package dev.jamesleach.build;

import com.google.common.io.Files;
import org.gradle.api.Task;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.api.tasks.bundling.ZipEntryCompression;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Plugin for an Elastic BeanStalk Project
 */
public class EbsPlugin extends BasePlugin {

    private static final String ZIP_TASK_NAME = "ebsZip";
    private static final String CREATE_DEFAULT_EBS_FILES = "createDefaultEbsFiles";
    private static final String ENHANCE_JAR = "enhanceJar";

    private static final String LIBS_DIR = "./build/libs/";
    private static final String PLATFORM_DIR = ".platform/";
    private static final String PROCFILE = "Procfile";
    private static final String DIST_GROUP = "distribution";


    @Override
    protected void apply() {

        // Plugins
        project().getPluginManager().apply(ApplicationPlugin.class);
        project().getPluginManager().apply(JavaPlugin.class);
        project().getPluginManager().apply(SpringBootPlugin.class);

        // Enhance the jar (with version information)
        TaskProvider<?> enhanceJar = enhanceJar(
                // ... depends on creation of default EBS files
                createDefaultEBSFiles(),
                // ... and bootjar
                project().getTasks().findByName("bootJar"));

        // EBS boot zip
        createBuildEbsZipTask(
                // ... depends on enhanceJar
                enhanceJar
        );

        // Docker
        new EbsDocker(utils()).
                addTasks(project().getBuildDir().toPath().resolve("libs/"),
                        enhancedJarOutputName() + ".jar",
                        // Depends on enhance jar
                        enhanceJar
                );
    }


    private TaskProvider<?> createDefaultEBSFiles() {
        return utils().registerTask(CREATE_DEFAULT_EBS_FILES, Task.class, task -> {
            // Meta
            task.setDescription("Create files for EBS if they don't exist");
            task.setGroup(DIST_GROUP);

            task.doLast(t -> {
                // Procfile
                File procfile = project().file("./" + PROCFILE);
                if (!procfile.exists()) {
                    try {
                        procfile.createNewFile();
                        Files.write(defaultProcfile().getBytes(), procfile);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create default Procfile", e);
                    }
                }

                // .platform dir
                File platformDir = project().file("./" + PLATFORM_DIR);
                if (!platformDir.exists()) {
                    platformDir.mkdir();
                }
            });
        });
    }


    /**
     * Create copy of bootJar output with version.properties file injected
     */
    private TaskProvider<?> enhanceJar(Object... dependencies) {
        return utils().registerTask(ENHANCE_JAR, Zip.class, task -> {
            // Meta
            task.dependsOn(dependencies);
            task.setDescription("Enhance jar");
            task.setGroup("distribution");

            // Create version info
            PopulateVersionInfo populateVersionInfo = new PopulateVersionInfo(
                    utils(),
                    () -> project().files(LIBS_DIR + bootJarOutputFile())
                            .plus(project().files(PROCFILE))
                            .plus(project().files(allPlatformFiles().toArray())));
            task.doFirst(populateVersionInfo);

            // Inputs
            task.from(project().zipTree(LIBS_DIR + bootJarOutputFile())).include("**");
            task.from(populateVersionInfo.getOutputDir()).include(populateVersionInfo.getOutputFile());

            // Output
            task.setEntryCompression(ZipEntryCompression.STORED);
            task.getArchiveExtension().set("jar");
            task.getArchiveBaseName().set(enhancedJarOutputName());
            task.getDestinationDirectory().set(project().file(LIBS_DIR));
        });
    }

    private TaskProvider<?> createBuildEbsZipTask(Object dependency) {
        return utils().registerTask(ZIP_TASK_NAME, Zip.class, task -> {
            // Meta
            task.dependsOn(dependency);
            task.setDescription("Build EBS zip");
            task.setGroup(DIST_GROUP);

            // Inputs
            task.from(project().file(LIBS_DIR)).include(bootJarOutputFile());
            task.from(project().file("./")).include(PLATFORM_DIR + "**").include(PROCFILE);

            // Output
            task.getArchiveBaseName().set(ebsJarName());
            task.getDestinationDirectory().set(project().file(LIBS_DIR));
        });
    }

    private List<File> allPlatformFiles() {
        try {
            return java.nio.file.Files.walk(project().file("./" + PLATFORM_DIR).toPath())
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Exception inspecting platform dir", e);
        }
    }


    private String enhancedJarOutputName() {
        return project().getName() + "-en";
    }

    private String bootJarOutputFile() {
        return project().getName() + ".jar";
    }


    private String ebsJarName() {
        return "ebs-" + project().getName();
    }


    private String defaultProcfile() {
        return "web: java -jar " + ebsJarName() + ".jar";
    }
}
