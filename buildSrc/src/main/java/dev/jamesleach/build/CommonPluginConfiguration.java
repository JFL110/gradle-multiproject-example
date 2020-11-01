package dev.jamesleach.build;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class CommonPluginConfiguration {

    @Input
    public abstract Property<String> getVersion();

    @Input
    public abstract Property<String> getCopyNativeLibsConfiguration();

}
