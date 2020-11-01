package dev.jamesleach.example;

import dev.jamesleach.example.version.ExposedVersion;
import dev.jamesleach.example.version.VersionService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestStatusController {

    private final VersionService versionService = Mockito.mock(VersionService.class);
    private final StatusController controller = new StatusController(versionService);

    @Test
    public void testStatus(){
        ExposedVersion version = mock(ExposedVersion.class);
        when(versionService.version()).thenReturn(version);
        Assert.assertEquals(version, controller.index());
    }
}
