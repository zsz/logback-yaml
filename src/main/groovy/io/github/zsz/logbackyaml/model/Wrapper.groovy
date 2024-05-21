package io.github.zsz.logbackyaml.model

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

class Wrapper {
  @JacksonXmlProperty(localName = "configuration")
  Configuration configuration
}

@JacksonXmlRootElement(localName = "configuration")
class Configuration implements IgnoredPropsMixin {

  @JacksonXmlProperty(isAttribute = true)
  Boolean scan

  @JacksonXmlProperty(isAttribute = true)
  Boolean debug

  @JacksonXmlProperty(localName = "import")
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  List<Import> imports

  @JacksonXmlProperty(localName = "scanPeriod")
  String scanPeriod

  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXISTING_PROPERTY,
      property = "name",
      visible = true
  )
  @JsonSubTypes([
      @Type(Console),
      @Type(RollingFile)
  ])
  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "appender")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  List<Appender> appenders

  @JacksonXmlProperty(localName = "root")
  Root root

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "logger")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  List<Logger> loggers

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "turboFilter")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  List<TurboFilter> turboFilters

  @JacksonXmlProperty(localName = "statusListener")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  List<StatusListener> statusListeners

}

class TurboFilter implements IgnoredPropsMixin, ClassAttributeMixin {
  String className
}

class StatusListener implements IgnoredPropsMixin, ClassAttributeMixin {
  String className
}

class Import implements IgnoredPropsMixin, ClassAttributeMixin {
  String className
}

class Root implements IgnoredPropsMixin {

  @JacksonXmlProperty(isAttribute = true)
  String level

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JacksonXmlElementWrapper(localName = "appender-ref", useWrapping = false)
  @JacksonXmlProperty(localName = "appender-ref")
  List<AppenderRef> appenderRefs

}

class Logger implements IgnoredPropsMixin {

  @JacksonXmlProperty(isAttribute = true)
  String name

  @JacksonXmlProperty(isAttribute = true)
  String level

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JacksonXmlElementWrapper(localName = "appender-ref", useWrapping = false)
  @JacksonXmlProperty(localName = "appender-ref")
  List<AppenderRef> appenderRefs
}


abstract class Appender implements IgnoredPropsMixin, ClassAttributeMixin {

  String className

  @JacksonXmlProperty(localName = "name", isAttribute = true)
  String name

  @JacksonXmlProperty(localName = "encoder")
  Encoder encoder

  @JsonProperty("filter")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JacksonXmlElementWrapper(useWrapping = false)
  List<Filter> filters

  @JsonCreator
  protected Appender() {

  }
}

@JsonTypeName("Console")
class Console extends Appender {

  @JsonCreator
  Console() {

  }
  @JacksonXmlProperty(localName = "target")
  String target


}

@JsonTypeName("RollingFile")
class RollingFile extends Appender {

  @JsonCreator
  RollingFile() {
  }

  @JacksonXmlProperty
  String file

  @JacksonXmlProperty(localName = "rollingPolicy")
  RollingPolicy rollingPolicy

}

class RollingPolicy implements IgnoredPropsMixin, ClassAttributeMixin {

  String className

  @JacksonXmlProperty
  String fileNamePattern

  @JacksonXmlProperty
  String maxHistory

  @JacksonXmlProperty
  String totalSizeCap
}

class Encoder implements IgnoredPropsMixin {

  @JacksonXmlProperty(localName = "pattern")
  String pattern

}

class Filter implements IgnoredPropsMixin, ClassAttributeMixin {
  String className

  @JacksonXmlProperty
  String level

  @JacksonXmlProperty
  String onMatch

  @JacksonXmlProperty
  String onMismatch
}

class AppenderRef implements IgnoredPropsMixin {

  @JacksonXmlProperty(isAttribute = true)
  String ref

}

interface ClassAttributeMixin {
  @JacksonXmlProperty(localName = "class", isAttribute = true)
  @JsonProperty("class")
  String getClassName()
}

interface IgnoredPropsMixin {
  @JsonIgnore
  default String[] getContentHash() {
    return new String[]{this.hashCode()}
  }

  @JsonIgnore
  default String[] getOriginalClassName() {
    return new String[]{this.getClass().getSimpleName()}
  }
}
