package dev.jamesleach.build;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import org.gradle.api.Task;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.testing.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EbsDocker {

    private static final String GROUP = "docker";
    private static final String DOCKER_KILL_LABEL = "gradle-src";

    private final PluginUtils utils;

    EbsDocker(PluginUtils utils) {
        this.utils = utils;
    }

    void addTasks(Path jarDir, String jarName, Object buildDependency) {

        String killLabel = utils.project().getName();
        String dockerTag = "gradle-ebs/" + utils.project().getName();
        String applicationPort = "8080";

        File dockerPath = utils.project().file("./build/docker/");
        File dockerFileFrom = utils.project().file("./build/libs/Dockerfile");

        // Create source set for int tests
        SourceSetContainer sourceSets = ((SourceSetContainer) utils.project().getExtensions().getByName("sourceSets"));
        SourceSet intTestSourceSet = sourceSets.create("intTest", srcSet -> {
            srcSet.java(j -> {
                j.srcDir("src/intTest/java");
            });
            srcSet.resources(r -> {
                r.srcDir("src/intTest/java");
            });
            srcSet.setCompileClasspath(
                    srcSet.getCompileClasspath()
                            .plus(sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).getCompileClasspath())
                            .plus(sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME).getCompileClasspath())
            );
        });

        TaskProvider<?> setupDockerDirectory = utils.registerTask("setupDockerDirectory", Task.class, task -> {
            task.setGroup(GROUP);
            task.dependsOn(buildDependency); // Not strictly true, could depend on a lower process

            task.doLast(t -> {
                // Create docker working directory if not exists
                if (!dockerPath.exists()) {
                    if (!dockerPath.mkdir()) {
                        throw new RuntimeException("Unable to create docker working directory");
                    }
                    utils.project().getLogger().info("Created docker directory at " + dockerPath);
                }

                // Create Docker file if not exists
                if (!dockerFileFrom.exists()) {
                    try {
                        dockerFileFrom.createNewFile();
                        Files.write(defaultDockerfile().getBytes(), dockerFileFrom);
                        utils.project().getLogger().info("Created Dockerfile at " + dockerFileFrom);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not create default Docker file", e);
                    }
                }
            });
        });

        TaskProvider<?> copyToDockerDirectory = utils.registerTask("copyToDockerDirectory", Copy.class, task -> {
            task.dependsOn(setupDockerDirectory, buildDependency);
            task.setGroup(GROUP);
            task.from(jarDir).include(jarName).into(dockerPath);
            task.from(utils.project().file("./build/libs/")).include("Dockerfile").into(dockerPath);
        });

        TaskProvider<?> buildDocker = utils.registerTask("buildDocker", Task.class, task -> {
            task.dependsOn(copyToDockerDirectory);
            task.setGroup(GROUP);

            task.doLast(t -> {
                utils.project().getLogger().lifecycle("Building docker image '" + dockerTag + "'");
                utils.execRootDir("docker", "build",
                        "--build-arg",
                        "JAR_FILE=" + jarName,
                        "-t", dockerTag, dockerPath.getPath());
            });
        });

        TaskProvider<?> killContainers = utils.registerTask("killContainers", Task.class, task -> {
            task.setGroup(GROUP);
            task.doLast(t -> killContainers(killLabel));
        });

        TaskProvider<?> killContainersAfterTest = utils.registerTask("killContainersAfterTest", Task.class, task -> {
            task.setGroup(GROUP);
            task.doLast(t -> killContainers(killLabel));
        });

        TaskProvider<?> startDocker = utils.registerTask("startDocker", Task.class, task -> {
            task.dependsOn(buildDocker, killContainers);
            task.setGroup(GROUP);

            task.doLast(t -> {
                utils.project().getLogger().lifecycle("Starting docker container '" + dockerTag + "'");

                String container = utils.execRootDir("docker", "run", "-p", applicationPort, "-d", "-l", DOCKER_KILL_LABEL + "=" + killLabel, dockerTag);
                String addr = utils.execRootDir("docker", "port", container, applicationPort + "/tcp");
                task.getExtensions().add("address", addr);

                utils.project().getLogger().lifecycle("Started container '" + container + "' on address " + addr);
            });
        });

        utils.registerTask("dockerIntTests", Test.class, task -> {
            task.dependsOn(startDocker);
            task.setGroup("verification");

            // Pass environment variales
            task.doFirst(t -> {
                String addr = startDocker.get().property("address").toString();
                ((Test) t).environment("APP_ADDRESS", addr);
            });

            task.setDescription("Run integration tests against docker");
            task.setTestClassesDirs(intTestSourceSet.getOutput().getClassesDirs());
            task.setClasspath(intTestSourceSet.getCompileClasspath().plus(intTestSourceSet.getRuntimeClasspath()));

            task.finalizedBy(killContainersAfterTest);
        });
    }


    private void killContainers(String killLabel) {
        utils.project().getLogger().lifecycle("Find all my docker containers with label '" + DOCKER_KILL_LABEL + "=" + killLabel + "'");
        List<String> containersToKill = Arrays.stream(utils.execRootDir("docker", "ps", "-q").split("\n"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        if (!containersToKill.isEmpty()) {
            utils.project().getLogger().lifecycle("Killing all my containers " + Joiner.on(" ").join(containersToKill));
            containersToKill.forEach(container -> utils.execRootDir("docker", "kill", container));
        } else {
            utils.project().getLogger().lifecycle("No running containers");
        }
    }


    private String defaultDockerfile() {
        return "FROM openjdk:8-jdk-alpine\n" +
                "ARG JAR_FILE=target/*.jar\n" +
                "COPY ${JAR_FILE} app.jar\n" +
                "ENTRYPOINT [\"java\",\"-jar\",\"/app.jar\"]";
    }
}
