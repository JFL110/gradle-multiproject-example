package dev.jamesleach.example.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;

/**
 * JSON Bean for version information
 */
public class ExposedVersion {

    private final Long buildTimeMillis;
    private final String buildTimeHuman;
    private final String filesHash;
    private final String gitBranchName;
    private final String gitCommitNumber;
    private final String gitCommitDescription;
    private final List<String> gitDiffFiles;

    @JsonCreator
    ExposedVersion(@JsonProperty("buildTimeMillis") Long buildTimeMillis,
                   @JsonProperty("buildTimeHuman") String buildTimeHuman,
                   @JsonProperty("filesHash") String filesHash,
                   @JsonProperty("gitBranchName") String gitBranchName,
                   @JsonProperty("gitCommitNumber") String gitCommitNumber,
                   @JsonProperty("gitCommitDescription") String gitCommitDescription,
                   @JsonProperty("gitDiffFiles") List<String> gitDiffFiles) {
        this.buildTimeMillis = buildTimeMillis;
        this.buildTimeHuman = buildTimeHuman;
        this.filesHash = filesHash;
        this.gitBranchName = gitBranchName;
        this.gitCommitNumber = gitCommitNumber;
        this.gitCommitDescription = gitCommitDescription;
        this.gitDiffFiles = gitDiffFiles;
    }

    @JsonProperty("filesHash")
    public String getFilesHash() {
        return filesHash;
    }

    @JsonProperty("buildTimeMillis")
    public Long getBuildTimeMillis() {
        return buildTimeMillis;
    }


    @JsonProperty("buildTimeHuman")
    public String getBuildTimeHuman() {
        return buildTimeHuman;
    }

    @JsonProperty("gitCommitDescription")
    public String getGitCommitDescription() {
        return gitCommitDescription;
    }

    @JsonProperty("gitDiffFiles")
    public List<String> getGitDiffFiles() {
        return gitDiffFiles;
    }

    @JsonProperty("gitBranchName")
    public String getGitBranchName() {
        return gitBranchName;
    }

    @JsonProperty("gitCommitNumber")
    public String getGitCommitNumber() {
        return gitCommitNumber;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
