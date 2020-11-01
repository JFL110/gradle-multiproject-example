package dev.jamesleach.build;

import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Copy DynamoDB files required for running it in unit tests
 */
class CopyDynamoNativeDeps {

    static final String NAME = "copyDynamoNativeDeps";
    private static final String DEFAULT_CONFIG = "testRuntimeClasspath";
    private static final String TASK_GROUP = "build";
    private final PluginUtils utils;

    CopyDynamoNativeDeps(PluginUtils utils) {
        this.utils = utils;
    }

    public void create() {

        utils.project().getRepositories().maven(maven -> {
            maven.setName("mulesoft");
            maven.setUrl("https://repository.mulesoft.org/nexus/content/repositories/public");
        });

        TaskProvider<?> taskProvider = utils.registerTask(NAME, Copy.class, task -> {
            // Meta
            task.setGroup(TASK_GROUP);


            String configName = utils.getCommonConfig().getCopyNativeLibsConfiguration().getOrElse(DEFAULT_CONFIG);
            Path destination = Paths.get(utils.project().getBuildDir().getPath()).resolve("libs");
            task.from(utils.project().getConfigurations().getByName(configName))
                    .include("*.dll", "*.dylib", "*.so")
                    .into(destination);

            task.doFirst(t -> utils.project().getLogger().lifecycle("Copying DynamoDB native libs from '" + configName + "' into " + destination));
        });
        utils.project().getTasks().findByName("test").dependsOn(taskProvider);
    }
}
