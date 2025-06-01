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

{{< notice style="green" icon="screwdriver-wrench" title="minimal.yaml" expanded="true" >}}
{{< code language="yaml" source="/code/minimal/minimal.yaml" >}}
{{< /notice >}}

You can generate native code during the build by putting this in the pom.xml:

{{< notice style="green" icon="screwdriver-wrench" title="pom.xml" expanded="true" >}}
{{< code language="xml" source="/code/plugin-fragment.pom.xml" >}}
{{< /notice >}}

And then doing this
```bash
mvn generate-sources generate-test-sources 
```
will generate code like this:
{{< notice style="blue" icon="screwdriver-wrench" title="/target/generated-sources/modbus-schema/kotlin/nl/example/Minimal.kt" expanded="false" >}}
{{< code language="kotlin" source="/code/minimal/target/generated-sources/modbus-schema/kotlin/nl/example/Minimal.kt" >}}
{{< /notice >}}

and tests that recreate all provided testcases as junit tests:
{{< notice style="blue" icon="screwdriver-wrench" title="/target/generated-test-sources/modbus-schema/kotlin/nl/example/TestMinimal.kt" expanded="false" >}}
{{< code language="kotlin" source="/code/minimal/target/generated-test-sources/modbus-schema/kotlin/nl/example/TestMinimal.kt" >}}
{{< /notice >}}

And because a lot of the null checks and returntype related things have been generated into the code this all makes using the schema a lot easier:


{{< notice style="green" icon="screwdriver-wrench" title="/src/main/kotlin/nl/example/MinimalDemo.kt" expanded="true" >}}
{{< code language="kotlin" source="/code/minimal/src/main/kotlin/nl/example/MinimalDemo.kt" >}}
{{< /notice >}}

