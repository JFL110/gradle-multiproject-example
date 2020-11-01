package dev.jamesleach.example.version;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Values read from the generated version.properties file
 */
@Configuration
@PropertySource(value = "classpath:version.properties", ignoreResourceNotFound = true)
class VersionProperties {

    @Value("${buildTimeMillis:#{null}}")
    private Long buildTimeMillis;

    @Value("${groupHash:#{null}}")
    private String groupHash;

    @Value("${gitBranch:#{null}}")
    private String gitBranch;

    @Value("${gitCommit:#{null}}")
    private String gitCommit;

    @Value("${gitCommitDescription:#{null}}")
    private String gitCommitDescription;

    @Value("${gitFilesDiff:#{null}}")
    private String gitFilesDiff;

    Long getBuildTimeMillis() {
        return buildTimeMillis;
    }

    String getGroupHash() {
        return groupHash;
    }

    String getGitBranch() {
        return gitBranch;
    }

    String getGitCommit() {
        return gitCommit;
    }

    String getGitCommitDescription() {
        return gitCommitDescription;
    }

    String getGitFilesDiff() {
        return gitFilesDiff;
    }
}
