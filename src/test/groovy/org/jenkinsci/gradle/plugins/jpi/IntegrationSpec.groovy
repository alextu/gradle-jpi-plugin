package org.jenkinsci.gradle.plugins.jpi

import groovy.transform.CompileStatic
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.experimental.categories.Category
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files

@CompileStatic
@Category(UsesGradleTestKit)
class IntegrationSpec extends Specification {
    @TempDir
    protected File projectDir

    protected GradleRunner gradleRunner() {
        def gradleProperties = inProjectDir('gradle.properties')
        if (!existsRelativeToProjectDir('gradle.properties')) {
            def props = new Properties()
            props.setProperty('org.gradle.warning.mode', 'fail')
            gradleProperties.withOutputStream {
                props.store(it, 'IntegrationSpec default generated values')
            }
        }
        def runner = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectDir)
        def gradleVersion = gradleVersionForTest
        if (gradleVersion != GradleVersion.current()) {
            return runner.withGradleVersion(gradleVersion.version)
        }
        runner
    }

    static GradleVersion getGradleVersionForTest() {
        System.getProperty('gradle.under.test')?.with { GradleVersion.version(delegate) } ?: GradleVersion.current()
    }

    static boolean isBeforeConfigurationCache() {
        gradleVersionForTest < GradleVersion.version('6.6')
    }

    static boolean isWindows() {
        System.getProperty('os.name').toLowerCase().contains('windows')
    }

    boolean existsRelativeToProjectDir(String path) {
        inProjectDir(path).exists()
    }

    File inProjectDir(String path) {
        new File(projectDir, path)
    }

    File mkDirInProjectDir(String path) {
        Files.createDirectories(projectDir.toPath().resolve(path)).toFile()
    }

    File touchInProjectDir(String path) {
        Files.createFile(projectDir.toPath().resolve(path)).toFile()
    }
}
