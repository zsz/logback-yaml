package io.github.zsz.logbackyaml

import org.gradle.api.NonNullApi
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test

@NonNullApi
abstract class LogbackYamlPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    if (project.getPluginManager().hasPlugin("java")) {
      LogbackYamlExtension ext = project.extensions.create("logbackYaml", LogbackYamlExtension)
      LogbackYamlExtension testExt = project.extensions.create("logbackTestYaml", LogbackYamlExtension)

      project.tasks.register("logbackYaml", LogbackYamlTask) {
        group = "other"
        description = "Yaml to XML logback configuration converter."
        inputFile = ext.inputFile
        outputFile = ext.outputFile
        retainOriginal = ext.retainOriginal
      }

      project.tasks.register("logbackTestYaml", LogbackYamlTask).configure {
        group = "other"
        description = "Yaml to XML logback test configuration converter."
        inputFile = testExt.inputFile
        outputFile = testExt.outputFile
        retainOriginal = testExt.retainOriginal
      }

      project.afterEvaluate {
        project.tasks.withType(JavaExec).configureEach {
          dependsOn "logbackYaml"
          onlyIf {
            ext.inputFile.asFile.get().exists()
          }
        }
        project.tasks.withType(Test).configureEach {
          dependsOn "logbackTestYaml"
          onlyIf {
            testExt.inputFile.asFile.get().exists()
          }
        }
      }
    } else {
      project.logger.warn("Plugin applied without having plugin 'java' applied.")
    }
  }
}

