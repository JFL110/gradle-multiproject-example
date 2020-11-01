package dev.jamesleach.build;

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.bundling.Zip;

import java.io.File;

/**
 * Plugin for a Lambda project
 */
public class LambdaPlugin extends BasePlugin {

    private static final String ZIP_TASK_NAME = "lambdaZip";

    @Override
    protected void apply() {
        // Plugins
        project().getPluginManager().apply(JavaPlugin.class);
        project().getPluginManager().apply(ShadowPlugin.class);

        // Lambda zip
        Task shadowJarTask = project().getTasks().findByName("shadowJar");
        createLambdaZip(shadowJarTask);

    }

    private void createLambdaZip(Task fatJarTask) {
        utils().registerTask(ZIP_TASK_NAME, Zip.class, task -> {

            // Task meta
            task.dependsOn(fatJarTask);
            task.setDescription("Build Lambda zip");
            task.setGroup("distribution");

            // Version
            PopulateVersionInfo populateVersionInfo = new PopulateVersionInfo(utils(), task::getSource);
            task.doFirst(populateVersionInfo);

            // Output
            task.getArchiveBaseName().set("lambda-" + project().getName());
            task.getDestinationDirectory().set(project().file("./build/libs/"));

            // Inputs
            File fatJar = project().file("./build/libs/" + project().getName() + "-all.jar");
            task.from(project().zipTree(fatJar)).include("**");
            task.from(populateVersionInfo.getOutputFile()).include("*");
        });
    }
}
