+++
title = 'Design'
weight = 60
+++

{{% notice style="red" icon="info" title="Original sketches" %}}
My original design sketches from a few years ago (~2023). Looking back a lot has worked out already.
{{% /notice %}}

:


```mermaid
mindmap
  root((Usable Modbus))
    Goals
        Make modbus MUCH easier to use
            Schema standard
                Schema for device ONCE
                Runtimes
                    Simply "do" the provided schema
                    Code generator per language
        Ecosystem
            Vendors provide schema
    Registers
        Retrieval API<br/>with memory cache
            Yaml File for testing
            Real modbus
                J2MOD
                PLC4J/PLC4X
    Mapping
        From "Generic Retrieval API"<br/>to"usable value"
            Support 'all' common formats
                Little/BigEndian
                uint/int 16/32/64/128
                Float/Double
                UTF8
                Extensible with custom functions
            SunSpec
                List of Models of Blocks of Registers
                Standard set of models
                Each device has different subset of the models
    API
        Generic

        Specific
            Generated getters and setters
    Languages
        Java
            StringTemplate4
            Runtime
        Go
        C#
    Runtime
        Optimize reading
            Only read what is needed
            Only refresh what is needed
    Tools
        Maven
            Maven plugin
```

Java
===
* Read and parse the schema into memory structure
    * Usable by StringTemplate4 to generate Java code
    * Convertable into in memory runtime model

Layers
- Outside:
    - Schema specific Getters
        - Generated by StringTemplate4 based on the schema
    - GraphQL (subscriptions!) API
        - GraphQL Schema and code dynamically constructed
            - Based on the device schema
- Generic name based getters
    - Generated at startup using the device schema
    - Dynamically created expression evaluation structure
        - Uses other Generic name based getters
        - Uses Modbus Registers
- Retrieval layer
    - Cache of all registers
        - Current value
        - Last retrieval timestamp
    - Grouping of the registers
        - Which MUST be retrieved in a single request
        - Which must be updated
            - Needed?
            - Immutable?
                - Already have a value?
    - Retrieval
        - Optimizer
            - Of all registers that must be updated
                - Which are combined to reduce the number of modbus requests.
                - Maximum number of registers per request !!
            - Create retrieval sets of registers
        - Use existing tooling (like plc4j and j2mod) to actually GET the values

```mermaid
---
title: Normal flow
---
sequenceDiagram
    box LightYellow Application Software
    participant Application
    end
    box GreenYellow Logical Device
    participant Schema
    participant Modbus Register Cache
    participant Modbus Register Cache Updater
    end
    box LightBlue Hardware Abstraction
        participant Modbus API
    end
    box LightSkyBlue Hardware
    participant Physical Device
    end

    Note over Modbus Register Cache: Just a datastructure to cache <br>the current register values<br>from the device.

    Note over Modbus API: Abstraction layer that supports several<br/>Modbus implementations<br/>- j2mod<br/>- plc4j<br/>- file: Reading the (static) registers<br/> from a file for testing purposes.

    Application ->>+ Schema: Mark values as wanted
    Note over Schema: Determine which registers<br> are needed for <br>the requested values.
    Schema ->>+ Modbus Register Cache: Mark "wanted" registers
    Modbus Register Cache -->>- Schema : Done
    Schema -->>- Application: Done
    Application ->>+ Schema: Run update
    Schema ->>+ Modbus Register Cache Updater: Run update
    Modbus Register Cache Updater ->>+ Modbus Register Cache: Fetch registers to update
    Note over Modbus Register Cache: Sometimes registers must be retrieved<br> as a group in a single modbus request.<br>Even if not "wanted"!
    Note over Modbus Register Cache: Sometimes registers are immutable<br> and only need to be retrieved once.
    Modbus Register Cache -->>- Modbus Register Cache Updater: Return registers to update
    Note over Modbus Register Cache Updater: Only retrieve register groups that are "too old".
    Note over Modbus Register Cache Updater: Optimizing modbus requests<br> for actually needed registers.
    loop Get needed registers
        Note over Modbus Register Cache Updater: Many Modbus Register Caches have limits<br>on the number of requests per second/minute.
    Modbus Register Cache Updater ->>+ Physical Device: Fetch registers
        Physical Device -->>- Modbus Register Cache Updater: Register values
    Modbus Register Cache Updater ->> Modbus Register Cache: Update retrieved registers
    end
    Modbus Register Cache Updater -->>- Schema: Update finished
    Schema -->>- Application: Update finished

    loop Get Values
    Application ->>+ Schema : Get Value
    Schema ->>+ Modbus Register Cache: Get needed registers
    Modbus Register Cache -->>- Schema: Provide registers
    Note over Schema: Calculating the desired value<br> from the register values <br>and the configured mapping.
    Schema -->>- Application: Provide value
    end
```

```mermaid
---
title: Normal bootstrap
---
sequenceDiagram
    participant Application
    participant SchemaYaml
    participant Physical Device
    Application ->>+ SchemaYaml: Load Schema from Yaml file
    SchemaYaml -->>- Application: Schema instance
```

```mermaid
---
title: SunSpec bootstrap
---
sequenceDiagram
    participant Application
    participant SunSpecSchema
    Note over SunSpecSchema: A SunSpec specific Schema generator.

    participant Physical Device

    Application ->>+ SunSpecSchema: Create Schema For Device
    SunSpecSchema ->>+ Physical Device: Get SunSpec header
    Physical Device -->>- SunSpecSchema: SunSpec header
    Note over SunSpecSchema: Create empty <br>Schema instance

    loop Walk over Model list in device
        SunSpecSchema ->>+ Physical Device: Get next Model header
        Physical Device -->>- SunSpecSchema: Model header
        SunSpecSchema ->>+ Physical Device: Get Model specific repeater values
        Physical Device -->>- SunSpecSchema: values
        Note over SunSpecSchema: Add retrieved model to<br> Schema instance
    end
    SunSpecSchema -->>- Application: Return Schema instance
```


```mermaid
---
title: Java
---
classDiagram
class ModBusSchemaAPI{
    RegisterSet memoryCache
    RegisterSet getRegisters(int from, int to)
    abstract void refresh()
}
ModBusSchemaAPI <|-- ModBus_Schema_API_File
ModBusSchemaAPI <|-- ModBus_Schema_API_J2MOD
ModBusSchemaAPI <|-- ModBus_Schema_API_PLC4J

```