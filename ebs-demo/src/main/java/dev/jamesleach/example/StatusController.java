package dev.jamesleach.example;

import dev.jamesleach.example.aop.ExampleAspectAnnotation;
import dev.jamesleach.example.version.ExposedVersion;
import dev.jamesleach.example.version.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/")
    @Operation(summary = "Gets the status of the application")
    @ExampleAspectAnnotation("some-value")
    public ExposedVersion index() {
        System.out.println("DONE");
        return versionService.version();
    }
}