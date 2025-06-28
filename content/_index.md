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
- A Kotlin/Java 11 (JVM only) library that uses this mapping definition to optimize the retrieval of the underlying data
  and provide the desired meaningful values.
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
    - automatically skips the Fields that have been retrieved and do not need to be retrieved again (immutable/read
      error)
- A maven plugin to convert a Modbus Schema into code.
    - The intent is for it to be possible into any language, Kotlin and Java have been included.

## Known limitations/Problems/Bugs

Things I will **NOT** change/fix:

- **It is READ ONLY. So NO writing.**
    - I will not change that because I consider that too much of a risk.

Things I intend to build/fix:

- Support for Coils and Discrete Inputs (Only Registers right now).

## Overall status

Works on my machines. Usable for experiments.

## Overview

```mermaid
---
config:
  flowchart:
    htmlLabels: false
---
flowchart TD
    subgraph "Modbus interfacing"
        pdev{{"Physical Modbus Device"}}
        apiimpl["`
            Modbus Implementation
            *Several implementations are supported*
        `"]
        api["`
            Modbus Device API
            *Abstraction to hide the implementation details*
        `"]
        apiimpl --> api
        pdev --> apiimpl
    end
    api --> fetch

    subgraph "Schema Runtime"
        direction TB
        fetch["Optimizing Fetcher"]
        cache[("`**Register cache**
            Raw values`")]
        schemaDev["`**Schema Device**
            Mapping Engine`"]
        cache --> schemaDev
        cache <--> fetch
    end

    subgraph "SunSpec"
        sunspecGen["`**SunSpec Schema Generator**`"]
        sunspec["SunSpec Model definitions"]
        sunspec --> sunspecGen
    end

    subgraph "Device definitions"
        thermiaGenesis["`**Thermia Genesis**`"]
        sdm630["`**Eastron SDM630**`"]
        yaml["`**Any Device Schema in Yaml format**`"]
        schema["`
          **Schema**
          Mapping Definition
        `"]
        schema --> schemaDev
        yaml --> schema
        sdm630 --> schema
        sunspecGen --> schema
        thermiaGenesis --> schema
    end

    api --> SunSpec

    subgraph Applications
        subgraph "Kotlin / Kotlin Script / Java"
            subgraph "`**Field name based API**`"
                baseApp["`
                Using the field name based Schema Device API you can use this on your own JVM allocations.
                This api requires retrieving the fields based on their name and asking the system for the actual return type.
            `"]
            end
            
            subgraph "`**Generated Code**`"
                codegenJvm["`
                A code generator has been provded that generates code (Kotlin & Java) that makes using the schema device a lot easier. For each Field a property is generated with a strongly typed return value.
            `"]
                
            end
        end
        subgraph "Any Language"
            subgraph "`**GraphQL Service (BETA)**`"
                graphQL["`
            **Currently in development**
            A GraphQL service has been created that is able to be used a facade in front of any modbus device.
        `"]
            end
        end
    end
    schemaDev --> Applications

```

## Github projects

I have split this into 4 projects:

- [Modbus Schema](https://github.com/nielsbasjes/modbus-schema):
    - The main toolkit and schema definition
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-schema/build.yml?branch=main&label=main%20branch&classes=inline)](https://github.com/nielsbasjes/modbus-schema/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.modbus/modbus-schema-parent.svg?label=Maven%20Central&classes=inline)](https://central.sonatype.com/namespace/nl.basjes.modbus)
      [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/modbus-schema-parent/badge.json&classes=inline)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/modbus-schema-parent/README.md)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-schema?label=GitHub%20stars&classes=inline)](https://github.com/nielsbasjes/modbus-schema/stargazers)
- [Modbus Devices](https://github.com/nielsbasjes/modbus-devices):
    - The actual schemas of a few devices.
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-devices/build.yml?branch=main&label=main%20branch&classes=inline)](https://github.com/nielsbasjes/modbus-devices/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.modbus.devices/modbus-devices-parent.svg?label=Maven%20Central&classes=inline)](https://central.sonatype.com/namespace/nl.basjes.modbus.devices)
      [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/devices/modbus-devices-parent/badge.json&classes=inline)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/devices/modbus-devices-parent/README.md)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-devices?label=GitHub%20stars&classes=inline)](https://github.com/nielsbasjes/modbus-devices/stargazers)
- [SunSpec Device](https://github.com/nielsbasjes/sunspec-device):
    - Generate the Modbus Schema for the specific SunSpec you have
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/sunspec-device/build.yml?branch=main&label=main%20branch&classes=inline)](https://github.com/nielsbasjes/sunspec-device/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.sunspec/sunspec-device-parent.svg?label=Maven%20Central&classes=inline)](https://central.sonatype.com/namespace/nl.basjes.sunspec)
      [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/sunspec/sunspec-device-parent/badge.json&classes=inline)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/sunspec/sunspec-device-parent/README.md)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/sunspec-device?label=GitHub%20stars&classes=inline)](https://github.com/nielsbasjes/sunspec-device/stargazers)
- [Modbus GraphQL](https://github.com/nielsbasjes/modbus-graphql):
    - Wrap any Modbus Schema and serve a device over GraphQL
    - [![License](https://img.shields.io/:license-apache-blue.svg?classes=inline)](https://www.apache.org/licenses/LICENSE-2.0.html)
      [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-graphql/build.yml?branch=main&label=main%20branch&classes=inline)](https://github.com/nielsbasjes/modbus-graphql/actions)
      [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.sunspec/modbus-graphql-parent.svg?label=Maven%20Central&classes=inline)](https://central.sonatype.com/namespace/nl.basjes.sunspec)
      [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-graphql?label=GitHub%20stars&classes=inline)](https://github.com/nielsbasjes/modbus-graphql/stargazers)
- [Modbus Website](https://github.com/nielsbasjes/modbus-website):
    - The source code of this website

All of this was created by [Niels Basjes](https://niels.basjes.nl/).
