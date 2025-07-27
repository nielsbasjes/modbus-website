+++
title = 'Thermia Genesis'
weight = 30
+++

Thermia Genesis is the platform used by several heat-pump models produced by Thermia.
I have a Thermia Calibra Cool 7 on which I have tested all of this.

![Thermia Heatpump](/ThermiaInverter.png?width=200px&lightbox=false)

Official website:
- https://thermia.com/products/ground-source-heat-pumps/thermia-calibra/

Used versions of the specification [can be found here](https://github.com/nielsbasjes/modbus-devices/tree/main/modbus-device-thermia-genesis/spec-official).

Official Modbus mappings:
- I have 3 versions of the Thermia Genesis modbus schema definitions.
  - Platform versions 10, 12 and 13
- There were some fields added and removed between those versions.
  - I have chosen to include all of them.
- There are many fields that only work 
  - on specific heat pump models
  - if specific modules have been installed (you'll see for example `(EM)` or `(EM3 only)` in the description of a Field).
  - if specific functionality is used (like swimming pool)

- Status: **All registers and discretes in the documentation have been put into the schema definition.**

- Schema as Yaml: [ThermiaGenesis101213.yaml](https://github.com/nielsbasjes/modbus-devices/blob/main/modbus-device-thermia-genesis/ThermiaGenesis101213.yaml)

I have also created for version 13 a schema where all Fields have been put into blocks based on the area of use (the "Reference to" column in the original PDFs)
- Schema as Yaml: [ThermiaGenesis13.yaml](https://github.com/nielsbasjes/modbus-devices/blob/main/modbus-device-thermia-genesis/ThermiaGenesis13.yaml)

The schema is also available as pre generated Kotlin code.
{{< tabs >}}
{{% tab title="Maven" %}}
```xml
<dependency>
  <groupId>nl.basjes.modbus.devices</groupId>
  <artifactId>modbus-device-thermia-genesis</artifactId>
  <version>{{%modbus-device-thermia-genesis-version%}}</version>
</dependency>
```
{{% /tab %}}
{{< /tabs >}}

With that the code to use the values can be as simple as this Kotlin Script:
{{< notice style="green" icon="screwdriver-wrench" title="GetThermia.main.kts" expanded="true" >}}
{{< code language="kotlin" source="/code/thermia/GetThermia.main.kts" >}}
{{< /notice >}}

This script produced this output:

    Outdoor Temperature            : 22.83 °C
    Room Temperature               : 22.5 °C
    Comfort Wheel Setting          : 20.5 °C
    Tap Water Weighted Temperature : 53.2 °C


Repo location of this Schema: https://github.com/nielsbasjes/modbus-devices/tree/main/modbus-device-thermia-genesis


