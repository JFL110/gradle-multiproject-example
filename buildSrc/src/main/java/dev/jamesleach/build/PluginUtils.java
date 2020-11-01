package dev.jamesleach.build;

import com.google.common.base.Joiner;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.process.ExecResult;

import java.io.ByteArrayOutputStream;

class PluginUtils {

    private final Project project;
    private static final String COMMON_CONFIG_NAME = "jamesBuild";

    PluginUtils(Project project) {
        this.project = project;
        // Init config
        project.getExtensions().create(COMMON_CONFIG_NAME, CommonPluginConfiguration.class);
    }

    Project project() {
        return project;
    }

    /**
     * Create a task and register it with the project
     */
    <T extends Task> TaskProvider<T> registerTask(String taskName, Class<T> taskType, Action<? super T> action) {
        return project.getTasks().register(taskName, taskType, action);
    }

    /**
     * Get common configuration for the plugin
     */
    CommonPluginConfiguration getCommonConfig() {
        return (CommonPluginConfiguration) project.getExtensions().getByName(COMMON_CONFIG_NAME);
    }


    /**
     * Execute a command line script in the root dir
     */
    String execRootDir(String command, Object... args) {
        ExecResult result;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            result = project().exec(execSpec -> execSpec
                    .commandLine(command)
                    .args(args)
                    .setErrorOutput(System.err)
                    .setStandardOutput(output)
                    .workingDir(project.getRootDir()));

            if (result.getExitValue() != 0) {
                throw new IllegalStateException("Non zero exit value should have thrown exception");
            }
            String outputStr = output.toString();
            return outputStr == null ? "" : outputStr.trim();
        } catch (Exception e) {
            throw new RuntimeException("Command failed '" + command + " " + Joiner.on(" ").join(args) + "'", e);
        }
    }
}
