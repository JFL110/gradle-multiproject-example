package dev.jamesleach.example;

import dev.jamesleach.example.inttest.IntTestRule;
import dev.jamesleach.example.version.ExposedVersion;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IntTestStatus {

    @ClassRule
    public static final IntTestRule intTestRule = new IntTestRule();

    private static final int MAX_JAR_AGE_MILLIS = 60 * 5 * 1000;

    @Test
    public void test() {
        // When
        ExposedVersion version = intTestRule.clientTarget("/").request().get(ExposedVersion.class);
        System.out.println(version);

        // Then
        assertNotNull(version.getBuildTimeMillis());

        // .. verify jar is not too old - should have been created just now
        long since = ZonedDateTime.now().toInstant().toEpochMilli() - version.getBuildTimeMillis();
        assertTrue(since < MAX_JAR_AGE_MILLIS);
    }
}
