+++
title = "The Modbus Schema Toolkit"
type = "home"
+++

## Summary
This set of Kotlin/Java libraries and tools to read data from Modbus devices like Electricity Meters, Solar Inverters and Heat pumps. 

This toolkit mainly consist of the following components:
- A generic definition language for creating a modbus mapping (a Modbus Schema).
  - i.e. translate binary values to meaningful values
- A Kotlin/Java 11 (JVM only) library that uses this mapping definition to optimize the retrieval of the underlying data and provide the desired meaningful values.
- A collection ready to run schemas (all the devices I have...):
  - Eastron SDM 630 Electricity Meter
  - Thermia Genesis Heatpump
  - SunSpec compliant devices.

{{% notice style="info" title="Intended as an open mapping standard" %}}
The way the mapping is defined is an open format and can be reimplemented into any programming language by anyone.
{{% /notice %}}

{{% notice style="orange" icon="bolt" title="This is not a modbus implementation!" %}}
This toolkit relies on existing libraries to do the actually Modbus data transfer.
Connectors to multiple modbus implementations have been included.
{{% /notice %}}

{{% notice style="green" icon="wand-magic-sparkles" title="Runs on a Raspberry PI 3 !" %}}
I have this running in a very simple [Kotlin Script](/usage/kotlinscript) on a Raspberry PI 3 (yes, that old). 
It fetches the data, converts it into useful values and puts these into a time series database every second.
{{% /notice %}}

## Main differences
The main features that the existing Modbus libraries (I have found) not have:
- A generic way of mapping registers to values which allows for the kind of expressions you see in SunSpec.
  - Most libraries only return the binary values, some allow basic single value mapping (like Apache Plc4x).
- Full support for SunSpec using the official SunSpec definitions.
- An optimizer that
  - automatically calculates the minimal number of modbus requests for the collection of Fields (= meaningful value) 
  - automatically optimizes around read errors
  - automatically skips the Fields that have been retrieved and do not need to be retrieved again (immutable/read error)
- A maven plugin to convert a Modbus Schema into code. 
  - The intent is for it to be possible into any language, Kotlin and Java have been included.

## Known limitations/Problems/Bugs
Things I will **NOT** change/fix:
- **It is READ ONLY. So NO writing.** 
  - I will not change that because I consider that too much of a risk.

Things I intend to build/fix:
- Support for Coils and Discrete Inputs (Only Registers right now).
- I want to have an API gateway that speaks GraphQL and can connect to any Modbus device you have the schema for.

## Overall status
Works on my machines. Usable for experiments.

## Github projects

I have split this into 4 projects:

- [Modbus Schema](https://github.com/nielsbasjes/modbus-schema):
  - The main toolkit and schema definition

[//]: # ()
[//]: # ([![License]&#40;https://img.shields.io/:license-apache-blue.svg&#41;]&#40;https://www.apache.org/licenses/LICENSE-2.0.html&#41;)
[//]: # ([![Github actions Build status]&#40;https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-schema/build.yml?branch=main&label=main%20branch&#41;]&#40;https://github.com/nielsbasjes/modbus-schema/actions&#41;)
[//]: # ([![Maven Central]&#40;https://img.shields.io/maven-central/v/nl.basjes.modbus/modbus-schema-parent.svg?label=Maven%20Central&#41;]&#40;https://central.sonatype.com/namespace/nl.basjes.modbus&#41;)
[//]: # ([![Reproducible Builds]&#40;https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/modbus-schema-parent/badge.json&#41;]&#40;https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/modbus-schema-parent/README.md&#41;)
[//]: # ([![GitHub stars]&#40;https://img.shields.io/github/stars/nielsbasjes/modbus-schema?label=GitHub%20stars&#41;]&#40;https://github.com/nielsbasjes/modbus-schema/stargazers&#41;)
- [Modbus Devices](https://github.com/nielsbasjes/modbus-devices):
  - The actual schemas of a few devices.

[//]: # (  - [![License]&#40;https://img.shields.io/:license-apache-blue.svg&#41;]&#40;https://www.apache.org/licenses/LICENSE-2.0.html&#41;)
[//]: # (    [![Github actions Build status]&#40;https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-devices/build.yml?branch=main&label=main%20branch&#41;]&#40;https://github.com/nielsbasjes/modbus-devices/actions&#41;)
[//]: # (    [![Maven Central]&#40;https://img.shields.io/maven-central/v/nl.basjes.modbus.devices/modbus-devices-parent.svg?label=Maven%20Central&#41;]&#40;https://central.sonatype.com/namespace/nl.basjes.modbus.devices&#41;)

[//]: # (    [![Reproducible Builds]&#40;https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/devices/modbus-devices-parent/badge.json&#41;]&#40;https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/devices/modbus-devices-parent/README.md&#41;)

[//]: # (    [![GitHub stars]&#40;https://img.shields.io/github/stars/nielsbasjes/modbus-devices?label=GitHub%20stars&#41;]&#40;https://github.com/nielsbasjes/modbus-devices/stargazers&#41;)
- [SunSpec Device](https://github.com/nielsbasjes/sunspec-device):
  - Generate the Modbus Schema for the specific SunSpec you have

[//]: # (  - [![License]&#40;https://img.shields.io/:license-apache-blue.svg&#41;]&#40;https://www.apache.org/licenses/LICENSE-2.0.html&#41;)
[//]: # (    [![Github actions Build status]&#40;https://img.shields.io/github/actions/workflow/status/nielsbasjes/sunspec-device/build.yml?branch=main&label=main%20branch&#41;]&#40;https://github.com/nielsbasjes/sunspec-device/actions&#41;)
[//]: # (    [![Maven Central]&#40;https://img.shields.io/maven-central/v/nl.basjes.sunspec/sunspec-device-parent.svg?label=Maven%20Central&#41;]&#40;https://central.sonatype.com/namespace/nl.basjes.sunspec&#41;)
[//]: # (    [![Reproducible Builds]&#40;https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/sunspec/sunspec-device-parent/badge.json&#41;]&#40;https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/sunspec/sunspec-device-parent/README.md&#41;)
[//]: # (    [![GitHub stars]&#40;https://img.shields.io/github/stars/nielsbasjes/sunspec-device?label=GitHub%20stars&#41;]&#40;https://github.com/nielsbasjes/sunspec-device/stargazers&#41;)
- [Modbus Website](https://github.com/nielsbasjes/modbus-website):
  - The source code of the documentation website

All of this was created by [Niels Basjes](https://niels.basjes.nl/).
