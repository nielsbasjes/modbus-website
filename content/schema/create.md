+++
title = 'Creating a schema'
weight = 50
+++

You can create the schema for your device!

In most cases you'll start with a PDF from the manufacturer which shows everything in tables.

Simply start by looking at [the existing Yaml schemas I have published](https://github.com/nielsbasjes/modbus-devices/), the documentation about the [Yaml file format](/schema/yaml) and [the possible expressions to define the mapping](/schema/expressions).

With that a simple script like this should get you going pretty well.

{{< notice style="green" icon="screwdriver-wrench" title="AddTestsToSchema.main.kts" >}}
{{< code language="kotlin" source="/code/create/AddTestsToSchema.main.kts" >}}
{{< /notice >}}

