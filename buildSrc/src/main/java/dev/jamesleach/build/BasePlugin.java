package dev.jamesleach.build;

import com.google.common.io.Files;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Base plugin common to all projects
 */
abstract class BasePlugin implements Plugin<Project> {

    private final Dependency lombok = new DefaultExternalModuleDependency("org.projectlombok", "lombok", "1.18.16");
    private PluginUtils utils;

    @Override
    public void apply(Project project) {
        this.utils = new PluginUtils(project);

        apply();

        // Common - copy Dynamo native debs if they exist on the classpath
        new CopyDynamoNativeDeps(utils).create();

        // Add lombok
        TaskProvider<?> modifyDeps = utils().registerTask("modifyDependencies", Task.class, task -> {
            // Meta
            task.setDescription("Modifiy dependencies");
            task.setGroup("build");

            task.doLast(t -> {
                // Add lombok to annotationProcessor dependencies if not exists
                project().getConfigurations().getByName("annotationProcessor", config -> {
                    if (config.getDependencies().stream()
                            .noneMatch(d -> Objects.equals(lombok.getGroup(), d.getGroup()) && lombok.getName().equals(d.getName()))) {
                        config.getDependencies().add(lombok);
                    }
                });

                // Add lombok.config to root of project
                File lombokConfigPath = project().file("./src/lombok.config");
                if (!lombokConfigPath.exists()) {
                    try {
                        lombokConfigPath.createNewFile();
                        Files.write(defaultLombokConfig().getBytes(), lombokConfigPath);
                        utils.project().getLogger().lifecycle("Created lombok config at " + lombokConfigPath);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not create default lombok config", e);
                    }
                }
            });
        });

        project().getTasks().getByName("compileJava").dependsOn(modifyDeps);
    }

    public PluginUtils utils() {
        return utils;
    }

    public Project project() {
        return utils.project();
    }

    abstract protected void apply();


    private String defaultLombokConfig() {
        return "lombok.anyConstructor.addConstructorProperties=true";
    }
}
