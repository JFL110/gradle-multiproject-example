package dev.jamesleach.example.version;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Configuration
public class VersionService {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private final VersionProperties versionProperties;

    @Autowired
    VersionService(VersionProperties versionProperties) {
        this.versionProperties = versionProperties;
    }

    public ExposedVersion version() {
        return new ExposedVersion(
                versionProperties.getBuildTimeMillis(),
                versionProperties.getBuildTimeMillis() == null ? "" :  ZonedDateTime.ofInstant(Instant.ofEpochMilli(versionProperties.getBuildTimeMillis()), UTC).toString(),
                versionProperties.getGroupHash(),
                versionProperties.getGitBranch(),
                versionProperties.getGitCommit(),
                versionProperties.getGitCommitDescription(),
                versionProperties.getGitFilesDiff() == null ? ImmutableList.of() : ImmutableList.copyOf(versionProperties.getGitFilesDiff().split("\n"))
        );
    }
}
