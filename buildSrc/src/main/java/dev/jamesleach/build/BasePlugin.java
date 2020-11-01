package dev.jamesleach.build;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Base plugin common to all projects
 */
abstract class BasePlugin implements Plugin<Project> {

    private PluginUtils utils;

    @Override
    public void apply(Project project) {
        this.utils = new PluginUtils(project);

        apply();

        // Common - copy Dynamo native debs if they exist on the classpath
        new CopyDynamoNativeDeps(utils).create();
    }

    public PluginUtils utils() {
        return utils;
    }

    public Project project() {
        return utils.project();
    }

    abstract protected void apply();
}
