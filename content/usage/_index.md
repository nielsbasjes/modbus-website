+++
title = 'Usage'
weight = 20
+++

{{% notice style="red" icon="bug" title="Incomplete documentation" %}}
Page is not yet finished. Is incomplete and will contain inaccuracies.
{{% /notice %}}

## Basic workflow
The basic problem with real devices providing data over Modbus is that they are very very slow.

I have found that reading a single registers from my heatpump takes about 500ms and that reading a block of 125 registers in a single request also takes about 500ms.

So the base workflow of this library has been chosen als follows:

- Obtain an instance of `SchemaDevice`
  - This has one or more `Block`s and each has `Field`s
  - Each `Field` has an expression.
  - The expression dictates the `ReturnType` of the specific Field.

- Connect to the actual Modbus Device using one of the [supported Modbus implementations](/modbus).

- Link these two together
  - `schemaDevice.connect(modbusDevice)`

- Indicate to the SchemaDevice instance which of the fields you `need`.


And then as often as you like (and your hardware supports!):
- Tell it to do `update`
  - Now the library will retrieve the needed modbus register values and remember them. This also includes when these were retrieved !
- For each field you can now get the actual value

## Language specifics
{{% children %}}
