# Logback Config Translator Gradle Plugin

<!-- TOC -->
* [Logback Config Translator Gradle Plugin](#logback-config-translator-gradle-plugin)
  * [About](#about)
  * [Features](#features)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
  * [Usage](#usage)
  * [Contribution](#contribution)
<!-- TOC -->

## About

The aim of this project is to provide a convenient way
to create Logback configuration file in Yet Another Markup Language, that is seamlessy converted into an eXtensible Markup Language file.

This Gradle plugin is designed to convert Logback configuration files from YAML to XML format. Note that the goal of this plugin is strictly translation; it **does not validate** your Logback configuration,
and any errors in your YAML will be translated into your XML configuration as well.
Also, my intention was not to fully cover logback configuration domain, only a part of it that I frequently use.

## Features

- Translates YAML format Logback configurations into XML format
- Useful for those who prefer to edit YaML documents rather than messy XML files.

## Prerequisites

- [Gradle](https://gradle.org/install/) needs to be installed on your system.

## Installation

Add the plugin to your `build.gradle`:
```Groovy
plugins {
    id "io.github.zsz.logbackyaml" version "1.0"
}
```

## Usage
To use this plugin, follow these steps:
* Write your Logback configuration in YAML format in the appropriate file.
* In your build.gradle script, configure the plugin's task to point to your YAML file and designate an output file for the XML.

```Groovy
logbackYaml {
    retainOriginal = false
    inputFile = file('build/resources/main/logback.yml')
    outputFile = file('build/resources/main/logback.xml')
}
```
This will perform the conversion and delete the logback.yml that was copied over to build directory.
The task will be done automatically as the last step of `processResources`/`processTestResources` tasks.

**Warning:** do not change the value of `retainOriginal` if `inputFile` points to a _logback.yml_ that resides in your source directory.

The plugin configures tasks `logbackYaml` and `logbackTestYaml` as dependency for task types 
`JavaExec` and `Test` respectively.

## Contribution

Feel free to report any problems, suggest improvements, or add missing Logback configuration tag by creating an issue on the project's GitHub page or submitting a PR. 
Contributions are welcome!