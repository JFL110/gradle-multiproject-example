package dev.jamesleach.example;

import dev.jamesleach.example.version.ExposedVersion;
import dev.jamesleach.example.version.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint to return the status of the app.
 *
 * @author jim
 *
 */
@RestController
public class StatusController {

    private final VersionService versionService;

    @Autowired
    StatusController(VersionService versionService) {
        this.versionService = versionService;
    }

    @RequestMapping("/")
    public ExposedVersion index() {
        return versionService.version();
    }
}