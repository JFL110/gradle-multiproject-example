package dev.jamesleach.build;

import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaTestFixturesPlugin;

/**
 * Plugin for a Java library project
 */
public class LibPlugin extends BasePlugin {
    @Override
    protected void apply() {
        // Plugins
        project().getPluginManager().apply(JavaLibraryPlugin.class);
        project().getPluginManager().apply(JavaTestFixturesPlugin.class);
    }
}
