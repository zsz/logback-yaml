package zsz.logbackyaml

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path

class LogbackYamlTest extends Specification {

  static final String GIVEN_YML_CONTENT =
      """configuration:
  scan: "true"
  scanPeriod: 60 seconds
  appenders:
    - name: Console
      class: ch.qos.logback.core.ConsoleAppender
      target: System.out
      encoder:
        pattern: "%d{HH:mm:ss.SSS} [%thread] %class{1}.%M\\\\(%file:%line\\\\)%n\\t%highlight(%-5level) - %msg%n%xEx"
    - name: RollingFile
      class: ch.qos.logback.core.rolling.RollingFileAppender
      file: myapp.log
      rollingPolicy:
        class: ch.qos.logback.core.rolling.TimeBasedRollingPolicy
        fileNamePattern: myapp-%d{yyyy-MM-dd}.log.gz
        maxHistory: 5
        totalSizeCap: 100MB
      encoder:
        pattern: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{1}.%M\\\\(%file:%line\\\\) - %msg%n"
  root:
    level: debug
    appenderRefs:
      - ref: Console
      - ref: RollingFile
  loggers:
    - name: "my.package"
      level: debug"""

  static final String EXPECTED_XML_CONTENT =
      """<configuration scan="true">
  <scanPeriod>60 seconds</scanPeriod>
  <appender class="ch.qos.logback.core.ConsoleAppender" name="Console">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %class{1}.%M\\(%file:%line\\)%n\t%highlight(%-5level) - %msg%n%xEx</pattern>
    </encoder>
    <target>System.out</target>
  </appender>
  <appender class="ch.qos.logback.core.rolling.RollingFileAppender" name="RollingFile">
    <file>myapp.log</file>
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{1}.%M\\(%file:%line\\) - %msg%n</pattern>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>myapp-%d{yyyy-MM-dd}.log.gz</fileNamePattern>
      <maxHistory>5</maxHistory>
      <totalSizeCap>100MB</totalSizeCap>
    </rollingPolicy>
  </appender>
  <root level="debug">
    <appender-ref ref="Console"/>
    <appender-ref ref="RollingFile"/>
  </root>
  <logger name="my.package" level="debug"/>
</configuration>""".replaceAll("\\n", System.lineSeparator())

  @TempDir()
  Path buildDirectory

  Path resourcesDirectory

  Path relativeResourcesDirectory = Path.of("resources/test")

  File buildFile

  File providedLogbackXmlFile
  File givenLogbackYmlFile


  def setup() {
    resourcesDirectory = buildDirectory.resolve(relativeResourcesDirectory)
    Files.createDirectories(resourcesDirectory)
    buildFile = buildDirectory.resolve('build.gradle').toFile()
    buildFile << """
        plugins {
            id 'groovy'
            id 'io.github.zsz.logback-yaml'
        }
    """

    providedLogbackXmlFile = resourcesDirectory.resolve("logback.xml").toFile()
    givenLogbackYmlFile = resourcesDirectory.resolve("logback.yml").toFile()
    givenLogbackYmlFile << GIVEN_YML_CONTENT
  }

  def withSlashes(Path path) {
    path.toString().replaceAll("\\\\", "/")
  }

  def "provided xml is valid logback configuration"() {
    given:
    buildFile << """
        logbackYaml {
            inputFile = file("${withSlashes(relativeResourcesDirectory.resolve("logback.yml"))}")
            outputFile = file("${withSlashes(relativeResourcesDirectory.resolve("logback.xml"))}")
        }
    """

    System.out.println(buildFile.text)


    when:
    def result = GradleRunner.create()
        .withProjectDir(buildDirectory.toFile())
        .withArguments('logbackYaml', '--stacktrace')
        .withPluginClasspath()
        .withDebug(true)
        .build()

    then:
    providedLogbackXmlFile.text.trim() == EXPECTED_XML_CONTENT.trim()
  }

}
