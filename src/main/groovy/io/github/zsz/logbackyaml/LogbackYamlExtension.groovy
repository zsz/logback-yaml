package io.github.zsz.logbackyaml


import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

abstract class LogbackYamlExtension {

  LogbackYamlExtension() {
    retainOriginal.convention(true)
  }

  /**
   * Whether to keep the original, that is, the YaML configuration file at the end of task execution.
   * <b>False value indicates your logback.yml will be deleted!</b>
   * @return {@code true} if the input YaML file will be kept; {@code false} if it will be deleted
   */
  abstract Property<Boolean> getRetainOriginal()

  /**
   * The Logback configuration to convert, written in YaML.
   * @return the incoming file
   */
  abstract RegularFileProperty getInputFile()

  /**
   * The Logback configuration that is converted from YaML, witten in XML.
   * @return the output file
   */
  abstract RegularFileProperty getOutputFile()

}