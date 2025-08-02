+++
title = 'Yaml format'
weight = 40
+++

You can create the schema for your device!

A simple example of the end result

{{< code language="yaml" source="/code/minimal/minimal.yaml" >}}

## Adding tests is really easy
It is really easy to add tests if you have a schema yaml and a real device to get real data from.
1. Define all the Blocks and Fields for your schema.
2. Now load that in the library to create a SchemaDevice.
3. Connect to a ModbusDevice instance with a real device behind it.
4. Get the register and discrete values for all the defined fields
```kotlin
schemaDevice.updateAll()
```
5. Based on all currently loaded register values generate the test
```kotlin
schemaDevice.createTestsUsingCurrentRealData()
```
6. Simply print the schema with all the tests included
```kotlin
println(schemaDevice.toYaml())
```

Or simply start with this kotlin script which does all that.
{{< notice style="green" icon="screwdriver-wrench" title="AddTestsToSchema.main.kts" expanded="false"  >}}
{{< code language="kotlin" source="/code/create/AddTestsToSchema.main.kts" >}}
{{< /notice >}}


## Yaml File Format explained

### Header
```yaml
# $schema: https://modbus.basjes.nl/v2/ModbusSchema.json
description: 'A very simple demo schema'
schemaFeatureLevel: 2
maxRegistersPerModbusRequest: 125
```
- `# $schema: https://modbus.basjes.nl/v2/ModbusSchema.json`
  - A reference to a JsonSchema that is used to validate and document the yaml file. Many editors support this to aid you in writing a valid yaml structure.
- `description: 'A very simple demo schema'`
  - A human readable description for what this is the schema
- `schemaFeatureLevel: 2`
  - The allowed functionality in this schema. Version 1 only allowed registers, Version 2 added booleans (coils and discrete inputs)
- `maxRegistersPerModbusRequest: 125`
  - [OPTIONAL] If the device does not allow getting the full 125 registers per request you can limit that here.

### Block

```yaml
blocks:
- id: 'Block 1'
  description: 'The first block'
  fields:
```
You can have multiple blocks. Each block must have a unique id and has its own set of Fields.
See it as a namespace which bundled logical values together.

NOTE: A field can only use other fields in their expression from the SAME block.

### Field
```yaml
    - id: 'Name'
      description: 'The name Field'
      # If a field NEVER changes value then set this to true
#        immutable: true
        # If a field is not a user level usable value set this to true (for example a scaling factor)
#        system: true
      expression: 'utf8(hr:0 # 12)'
      unit: 'W'
```

A block has 1 or more Fields.

- `id`
  - Each Field must have a unique id and an expression that determines the way the value for this Field is obtained.
- `description`
  - A human-readable explanation what this field is.
- `expression`
  - The content of the expression is explained on the other pages under schema on this site.
- `unit`
  - If the returned value has a unit (like Watt, Volt or Celsius) you can use this to add a human readable unit to the Field. This can be used downstream to show in a dashboard.
- `immutable`
  - If the value of a Field will NEVER change (during the runtime of the application) then set `immutable: true` because that will aid in optimizing the queries done to the actual device.
- `system`
  - If the value is something that should not be considered a meaningful value by itself then set it to `system: true`. The effect is that in many places (like code generation and GraphQL) the Field it will be hidden. This is used in the SunSpec scaling factors (which are intermediate fields needed to calculate a value) and padding fields (which are useless).
- `fetchGroup`
  - Don't use this because it will hurt you. Only needed in very rare cases. 
  - It is possible to force multiple Fields to always be part of the same Modbus Query. This requires the registers of those fields to be directly next to each other. 

### Tests
```yaml
tests:
- id: 'Just to demo the test capability'
```

Optionally a schema yaml can contain tests.
Each test validates the relation between specific input registers/discretes and the output values for specific fields.

- `id`
  - The ID of a test will be used to name the test code when generating code.

### Test Input
```yaml
  input:
    - firstAddress: 'hr:0'
      rawValues: |2-
      # --------------------------------------
      # The name is here
      4e69 656c 7320 4261 736a 6573 0000 0000 0000 0000
      0000 0000

    - firstAddress: 'c:0'
      rawValues: |2-
      # --------------------------------------
      # The flag is here
      0 0 1 1 0
```

The `input` section contains the input modbus values.
Multiple blocks of register values can be provided and these will all be merged together as input for the tests.

- `firstAddress`
  - The address of the first value in the list of provided values.
- `rawValues`
  - A space/newline separated list of raw modbus input values.
  - In case of Registers these will be groups of 4 hex letters each representing a single register. 
    - Values you may find:
      - `[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]`: Normal 4 digit hex values. 
      - `----`: No value available.
      - `xxxx` or `XXXX`: A Modbus Read error is returned when these are requested.
  - In case of Discretes these will be groups of 1 letter each representing a single discrete. Values:
    - Values you may find:
      - `0`: A 0 is returned.
      - `1`: A 1 is returned.
      - `-`: No value available.
      - `x` or `X`: A Modbus Read error is returned when these are requested.


NOTE: When the tests are generated from a read device the lowercase x (`xxxx` or `x`) means this was a `Soft read error` and when you see an upper case X (`XXXX` or `X`) it means this was a `Hard read error`. Check the page on [fetching](/schema/fetching) for more information about this.

### Test Expectations
```yaml
  blocks:
    - id:          'Block 1'
      expected:
        'Name':    [ 'Niels Basjes' ]
        'Flag':    [ 'false' ]
```

The last part is specifying the expectations that must be correct.
It is not required to have all blocks and/or fields specified.

For each Field which you want to verify you can add an expected value.

Note the expected value is an array !
- An empty array `[]` means no value should be returned.
- An array can have 
  - a number `[ 42 ]` or `[ 42.42 ]`
  - a boolean (as String) `[ 'true' ]` or `[ 'false' ]`
  - a list of Strings `[ 'one', 'two' ]`
