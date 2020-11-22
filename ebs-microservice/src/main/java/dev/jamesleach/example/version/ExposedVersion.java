package dev.jamesleach.example.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * JSON Bean for version information
 */
@Data
@RequiredArgsConstructor(onConstructor=@__(@JsonCreator))
public class ExposedVersion {

    @JsonProperty("buildTimeMillis")
    private final Long buildTimeMillis;
    private final String buildTimeHuman;
    private final String filesHash;
    private final String gitBranchName;
    private final String gitCommitNumber;
    private final String gitCommitDescription;
    private final List<String> gitDiffFiles;

}
