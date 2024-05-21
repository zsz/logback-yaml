package io.github.zsz.logbackyaml

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import io.github.zsz.logbackyaml.model.IgnoredPropsMixin
import io.github.zsz.logbackyaml.model.Wrapper
import org.gradle.api.DefaultTask
import org.gradle.api.NonNullApi
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

import java.nio.file.Files

/**
 * Task that takes a Logback {@link LogbackYamlTask#getInputFile() configuration written in YaML}
 * and converts it to an {@link LogbackYamlTask#getOutputFile() XML file} consumable by Logback.
 */
@NonNullApi
abstract class LogbackYamlTask extends DefaultTask {

  /**
   * @see LogbackYamlExtension#getRetainOriginal()
   */
  @Input
  @Optional
  abstract Property<Boolean> getRetainOriginal()

  /**
   * @see LogbackYamlExtension#getInputFile()
   */
  @InputFile
  @Optional
  abstract RegularFileProperty getInputFile()

  /**
   * @see LogbackYamlExtension#getOutputFile()
   */
  @OutputFile
  @Optional
  abstract RegularFileProperty getOutputFile()

  LogbackYamlTask() {
    retainOriginal.convention(true)
    inputFile.convention(project.layout.projectDirectory.file('resources/main/logback.yml'))
    outputFile.convention(project.layout.projectDirectory.file('resources/main/logback.xml'))
  }

  @TaskAction
  def performConversion() {
    File yml = inputFile.get().asFile
    if (!yml.exists()) {
      logger.warn("Input logback YML configuration `${yml}` does not exist")
      return
    }
    File xml = outputFile.get().asFile

    prepareOutput(xml)

    var yamlMapper = getYamlMapper()
    var xmlMapper = getXmlMapper()
    convertYmlToXml(yamlMapper, yml, xmlMapper, xml)

    cleanupIfNeeded(yml)

    logger.debug("Conversion succeeded converted `${yml}` to `${xml}`..")
  }

  def prepareOutput(File xml) {
    try {
      logger.debug("Preparing ${xml.getName()}..")
      Files.deleteIfExists(xml.toPath())
      Files.createFile(xml.toPath())
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to prepare output file `${xml}` due to ${e.getMessage()}")
    }

  }

  @Internal
  def getYamlMapper() {
    logger.debug("Creating Yaml mapper..")
    YAMLMapper yamlMapper = new YAMLMapper()
    yamlMapper.addMixIn(Object.class, IgnoredPropsMixin.class)
    return yamlMapper
  }

  @Internal
  def getXmlMapper() {
    logger.debug("Creating XML mapper..")
    XmlMapper xmlMapper = new XmlMapper()
    xmlMapper.addMixIn(Object.class, IgnoredPropsMixin.class)
    xmlMapper.configure(ToXmlGenerator.Feature.UNWRAP_ROOT_OBJECT_NODE, true)
    return xmlMapper
  }

  private void convertYmlToXml(YAMLMapper yamlMapper, File yml, XmlMapper xmlMapper, File xml) {
    try {
      logger.debug("Converting `${yml}` to `{$xml}`..")
      Wrapper wrapper = yamlMapper.readValue(yml, Wrapper.class)
      xmlMapper.writer(new DefaultXmlPrettyPrinter()).writeValue(xml, wrapper.configuration)
    } catch (IOException e) {
      logger.error("Converting `${yml}` to `${xml}` has failed due to `${e.getMessage()}`", e)
    }
  }

  private void cleanupIfNeeded(File yml) {

    if (!retainOriginal.get()) {
      logger.debug("Cleaning up `${yml}`..")
      if (!yml.delete()) {
        logger.warn("Could not delete original logback configuraiton `${yml}`")
      }
    }
  }
}