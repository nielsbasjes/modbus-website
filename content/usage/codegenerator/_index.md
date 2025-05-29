+++
title = 'Generated Code'
weight = 40
+++

{{% notice style="red" icon="bug" title="Incomplete documentation" %}}
Page is not yet finished. Is incomplete and will contain inaccuracies.
{{% /notice %}}

There is a customizable code generator.

It uses [Apache Freemarker](https://freemarker.apache.org/) as the template engine wrapped in a maven plugin with templates for Kotlin and Java included.

So if you have a schema

{{< notice style="green" icon="screwdriver-wrench" title="example.yaml" expanded="true" >}}
{{< code language="yaml" source="/code/example.yaml" >}}
{{< /notice >}}

You can generate native code during the build by putting this in the pom.xml:

{{< notice style="green" icon="screwdriver-wrench" title="pom.xml" expanded="true" >}}
{{< code language="kotlin" source="/code/pom.xml" >}}
{{< /notice >}}

And then doing this
```bash
mvn generate-sources generate-test-sources 
```
will generate code like this:
{{< notice style="blue" icon="screwdriver-wrench" title="/target/generated-sources/modbus-schema/kotlin/nl/example/Example.kt" expanded="false" >}}
{{< code language="kotlin" source="/code/target/generated-sources/modbus-schema/kotlin/nl/example/Example.kt" >}}
{{< /notice >}}

and tests that recreate all provided testcases as junit tests:
{{< notice style="blue" icon="screwdriver-wrench" title="/target/generated-test-sources/modbus-schema/kotlin/nl/example/TestExample.kt" expanded="false" >}}
{{< code language="kotlin" source="/code/target/generated-test-sources/modbus-schema/kotlin/nl/example/TestExample.kt" >}}
{{< /notice >}}
