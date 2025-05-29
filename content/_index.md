+++
title = "The Modbus Schema Toolkit"
type = "home"
+++

## Summary
This set of Kotlin/Java libraries and tools introduces a new way to making the mapping from the Modbus registers into usable values.


This toolkit mainly consist of the following components:
- A definition language for creating a modbus mapping (suitable for all devices I have) which I call a Modbus Schema.
  - Including some testing schemas to cover all capabilities (useful for anyone who wants to reimplement this)

{{% notice style="info" title="Intended as an open mapping standard" %}}
The way the mapping is defined is an open format and can be reimplemented into any programming language by anyone.
{{% /notice %}}

All the code is written in Kotlin but because of some specific things it only works on the JVM right now.
This does make it easily usable in Kotlin, Java and also Kotlin script applications.

- A library to use this mapping definition to actually do the mapping
  - A simple modbus interface to wrap any modbus implementation.
  - An engine that is able to use the provided expressions to determine which registers are needed and then do the actual mapping to usable values.
  - An optimizing fetcher that is able to reduce the number of modbus calls needed to get the registers.
  - A maven plugin that is capable of generating code from the provided schema
    - Kotlin and Java code generation are included, others can be added.
  
- A collection ready to run schemas have been included (all the devices I have...):
  - Eastron SDM 630
  - Thermia Genisys Heatpumps
  - My SMA Solar inverter (using SunSpec)

- A library to handle SunSpec devices.
  - A wrapper to make the official SunSpec definition usable from code.
    - Which also makes a few tweaks to fix problems in the official SunSpec
  - A library that inspects a SunSpec device and constructs the Modbus Schema for this device on the fly using the official SunSpec definitions.

{{% notice style="orange" icon="bolt" title="This is not a modbus implementation!" %}}
This toolkit relies on existing libraries to do the actually Modbus data transfer.
Connectors to multiple modbus implementations have been included.
{{% /notice %}}

## The problem
I have multiple devices at home that are capable of exposing metrics using the modbus protocol.
My solar inverter reports how much power it is delivering, my heatpump reports the temperature of the hot water and an electricity meter I have exposes how much power is being used.

The key problem is that Modbus is a binary protocol and the modbus itself does not give any meaning to the bits you can retrieve.

To add meaning to these bits the historical reality is that you would get a PDF from the manufacturer and there you could read in a table how the bits need to be interpreted.

So for every device everyone who want to read the data needs to reinvent the wheel over and over again.

I've spent too much time debugging nasty problems regarding reading Modbus and the fact that the most common notation is a 1-off compared to the read address on the wire.

## Goals of this toolkit
This is intended to make it much easier and more reliable and more reusable to get data from Modbus devices.

The overall goals
1. creating a generic way of defining the extraction of data from a modbus device that maps the raw registers into meaning full values.
2. doing it in such a way that a code generator for any (common) programming language can be created that generates the code needed to read the data from the device and actually execute the transformation into the meaning full fields.
3. actually do this for the devices I have at home and generate the Kotlin/Java code needed to really do this.

So essentially the intent of this project is to create a machine-readable standard schema/format specification for modbus devices that can be used to generate code.

Intended effects:
- The schema for a specific device only needs to be written ONCE.
  - Preferably by the company that created the device.
- The code generator + runtime for a specific language only needs to be written ONCE.
  - I have already written Kotlin and Java.
- For all devices it is now trivial to generate code that maps the registers into usable values.
- A new device is immediately available in all programming languages.
- A new language immediately can provide tooling for all defined devices.
- Other people and the companies who actually create the devices start publishing their Modbus registers in the format provided by this library. From there everyone can start using the data from these devices much more reliably in any context they want.

## Current features
- A schema definition
    - which allows very flexible expressions (Byte ordering, Integers, Floats, Strings, Enums, Bitmaps, PEMDAS Math, String concat, etc.).
    - has the option to include tests
- A generic API towards the actual Modbus implementation (I implemented Apache Plc4J and J2Mod)
- A retrieval system that optimizes the retrieval of the needed registers to reduce the number of Modbus calls.
- Code generation of Kotlin and Java

## Known limitations/Problems/Bugs

Things I will **NOT** change/fix:
- It is READ ONLY. So NO writing. I will not change that because I consider that too much of a risk.

Things I intend to build/fix:
- Better website & more documentation.
- Support for Coils and Discrete Inputs (Only Registers right now).
- I want to have an API gateway that speaks GraphQL and can connect to any Modbus device you have the schema for.

## Overall status
Works on my machine. Usable for experiments.

## Github projects
I have split this into 4 projects:
- [Modbus Schema](https://github.com/nielsbasjes/modbus-schema):
  - A toolkit and schema definition
  - [![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
    [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-schema/build.yml?branch=main&label=main%20branch)](https://github.com/nielsbasjes/modbus-schema/actions)
    [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.modbus/modbus-schema-parent.svg?label=Maven%20Central)](https://central.sonatype.com/namespace/nl.basjes.modbus)
    [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/modbus-schema-parent/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/modbus-schema-parent/README.md)
    [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-schema?label=GitHub%20stars)](https://github.com/nielsbasjes/modbus-schema/stargazers)

- [Modbus Devices](https://github.com/nielsbasjes/modbus-devices):
  - The actual schemas of a few devices.
  - [![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
    [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/modbus-devices/build.yml?branch=main&label=main%20branch)](https://github.com/nielsbasjes/modbus-devices/actions)
    [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.modbus.devices/modbus-devices-parent.svg?label=Maven%20Central)](https://central.sonatype.com/namespace/nl.basjes.modbus.devices)
    [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/modbus/devices/modbus-devices-parent/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/modbus/devices/modbus-devices-parent/README.md)
    [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/modbus-devices?label=GitHub%20stars)](https://github.com/nielsbasjes/modbus-devices/stargazers)

- [SunSpec Device](https://github.com/nielsbasjes/sunspec-device):
  - Generate the Modbus Schema for the specific SunSpec you have
  - [![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
    [![Github actions Build status](https://img.shields.io/github/actions/workflow/status/nielsbasjes/sunspec-device/build.yml?branch=main&label=main%20branch)](https://github.com/nielsbasjes/sunspec-device/actions)
    [![Maven Central](https://img.shields.io/maven-central/v/nl.basjes.sunspec/sunspec-device-parent.svg?label=Maven%20Central)](https://central.sonatype.com/namespace/nl.basjes.sunspec)
    [![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/nl/basjes/sunspec/sunspec-device-parent/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/nl/basjes/sunspec/sunspec-device-parent/README.md)
    [![GitHub stars](https://img.shields.io/github/stars/nielsbasjes/sunspec-device?label=GitHub%20stars)](https://github.com/nielsbasjes/sunspec-device/stargazers)

- [Modbus Website](https://github.com/nielsbasjes/modbus-website):
  - The source code of the documentation website

All of this was created by [Niels Basjes](https://niels.basjes.nl/).
