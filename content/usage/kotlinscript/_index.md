+++
title = 'Kotlinscript'
weight = 20
+++

{{% notice style="orange" icon="hand-spock" title="Mostly complete documentation" %}}
Please help me find any inaccuracies or things that are not clear.
{{% /notice %}}

This toolkit works great on my Raspberry PI 3 using Kotlin script.

What you need:
- Java 17 (or newer)
  - For my PI3 I got Zulu 17 from the [Azul download site](https://www.azul.com/downloads/?version=java-17-lts&architecture=arm-32-bit-hf&package=jdk#zulu).
- Kotlin commandline compiler
  - https://kotlinlang.org/docs/command-line.html

From there you'll need a Schema for your device

{{< notice style="green" icon="screwdriver-wrench" title="minimal.yaml" >}}
{{< code language="yaml" source="/code/minimal/minimal.yaml" >}}
{{< /notice >}}

{{< notice style="green" icon="screwdriver-wrench" title="GetData.main.kts" >}}
{{< code language="kotlin" source="/code/minimal/GetMinimal.main.kts" >}}
{{< /notice >}}
