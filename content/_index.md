+++
title = "The Modbus Schema Toolkit"
type = "home"
+++

## Summary

This is a Kotlin/Java toolkit that combines the ability to make a generic modbus mapping with an advanced modbus query
optimizer.
The goal of this set of libraries and tools to make getting meaningful data from Modbus devices like Electricity Meters,
Solar Inverters and Heat pumps very easy and very efficient.

This toolkit mainly consist of the following components:

- A generic definition language for creating a modbus mapping (a Modbus Schema).
    - i.e. translate binary values to meaningful values
- A Kotlin/Java 17 (JVM only) library that uses this mapping definition to optimize the retrieval of the underlying data
  and provide the desired meaningful values.
- A collection ready to run schemas (all the devices I have...):
    - Eastron SDM 630 Electricity Meter
    - Thermia Genesis Heatpump
    - SunSpec compliant devices.
- A generic (i.e. works with all schemas with almost no effort) GraphQL wrapper that enables access to any Modbus device over GraphQL.

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
    - automatically skips the Fields that have been retrieved and do not need to be retrieved again (immutable/read
      error)
- A maven plugin to convert a Modbus Schema into code.
    - The intent is for it to be possible into any language, Kotlin and Java have been included.

## Known limitations/Problems/Bugs

Things I will **NOT** change/fix:

- **It is READ ONLY. So NO writing.**
    - I will not change that because I consider that too much of a risk.

## Overall status

Works on my machines. Usable for experiments.

## Overview

![Overview](/ModbusSchemaToolkitOverview.png?width=1000px)

## Github projects

I have split this into 4 projects:

- [Modbus Schema](https://github.com/nielsbasjes/modbus-schema):
    - The main toolkit and schema definition
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline&lightbox=false)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-schema/build.yml?branch=main&label=main%20branch&classes=inline&lightbox=false)](https://github.com/nielsbasjes/modbus-schema/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.modbus/modbus-schema-parent.svg?label=Maven%20Central&classes=inline&lightbox=false)](https://central.sonatype.com/namespace/nl.basjes.modbus)
      [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/modbus-schema-parent/badge.json&classes=inline&lightbox=false)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/modbus-schema-parent/README.md)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-schema?label=GitHub%20stars&classes=inline&lightbox=false)](https://github.com/nielsbasjes/modbus-schema/stargazers)
- [Modbus Devices](https://github.com/nielsbasjes/modbus-devices):
    - The actual schemas of a few devices.
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline&lightbox=false)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-devices/build.yml?branch=main&label=main%20branch&classes=inline&lightbox=false)](https://github.com/nielsbasjes/modbus-devices/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.modbus.devices/modbus-devices-parent.svg?label=Maven%20Central&classes=inline&lightbox=false)](https://central.sonatype.com/namespace/nl.basjes.modbus.devices)
      [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/devices/modbus-devices-parent/badge.json&classes=inline&lightbox=false)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/devices/modbus-devices-parent/README.md)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-devices?label=GitHub%20stars&classes=inline&lightbox=false)](https://github.com/nielsbasjes/modbus-devices/stargazers)
- [SunSpec Device](https://github.com/nielsbasjes/sunspec-device):
    - Generate the Modbus Schema for the specific SunSpec you have
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline&lightbox=false)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/sunspec-device/build.yml?branch=main&label=main%20branch&classes=inline&lightbox=false)](https://github.com/nielsbasjes/sunspec-device/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.sunspec/sunspec-device-parent.svg?label=Maven%20Central&classes=inline&lightbox=false)](https://central.sonatype.com/namespace/nl.basjes.sunspec)
      [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/sunspec/sunspec-device-parent/badge.json&classes=inline&lightbox=false)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/sunspec/sunspec-device-parent/README.md)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/sunspec-device?label=GitHub%20stars&classes=inline&lightbox=false)](https://github.com/nielsbasjes/sunspec-device/stargazers)
- [Modbus GraphQL](https://github.com/nielsbasjes/modbus-graphql):
    - Wrap any Modbus Schema and serve a device over GraphQL
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline&lightbox=false)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-graphql/build.yml?branch=main&label=main%20branch&classes=inline&lightbox=false)](https://github.com/nielsbasjes/modbus-graphql/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.modbus.graphql/modbus-graphql-parent.svg?label=Maven%20Central&classes=inline&lightbox=false)](https://central.sonatype.com/namespace/nl.basjes.sunspec)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-graphql?label=GitHub%20stars&classes=inline&lightbox=false)](https://github.com/nielsbasjes/modbus-graphql/stargazers)
- [Modbus Website](https://github.com/nielsbasjes/modbus-website):
    - The source code of this website

All of this was created by [Niels Basjes](https://niels.basjes.nl/).

## License

I'm publishing this under the Apache 2.0 license because I believe this can be part of making this planet a bit more in control of the energy consumption.
I also believe that making this open for all to use is the best way to achieve this.

But do not underestimate how much work went into this. From my first attempts to releasing the first version took me about 5 years of spending my spare time.

So what I want to see in return is a little bit of gratitude from the people who use this.
If you are a home user/hobbyist/small business then a simple star on the projects you use is enough for me. Seeing that people use and like the things I create is what I'm doing this for.
What also really helps are bug reports, dumps from real devices I do not have and discussions on things you think can be done better.

Despite there not being any obligation (because of the Apache 2.0 license); If you are a big corporation where my code really adds value to the products you make/sell then I would really appreciate it if you could do a small sponsor thing. Buy me lunch (€10), Buy me a game (€100) or what ever you think is the right way to say thank you for the work I have done.

[![If this project has business value for you then don't hesitate to support me with a small donation.](https://img.shields.io/badge/Sponsor%20me-via%20Github-darkgreen.svg?lightbox=false)](https://github.com/sponsors/nielsbasjes)

    Modbus Schema Toolkit
    Copyright (C) 2019-2025 Niels Basjes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
