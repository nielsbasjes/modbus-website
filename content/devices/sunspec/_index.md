+++
title = 'Sunspec'
weight = 10
+++

## What is SunSpec?
SunSpec is a standard for getting data from devices that are related to solar, battery and other electricity related systems. Go to the [SunSpec Alliance](https://sunspec.org/) website if you really want the details.

At the technical side SunSpec is a meta schema for Modbus that is used by many different devices from many vendors.

SunSpec is essentially collection of partial modbus mappings (they call Models) and each Model describes the meaningful values for a specific capability.
There are models for many capabilities: being a solar inverter, being a battery, having IPv4 network support, ...

Each actual SunSpec device has a different set of models so it depends on the actual device which of these models are present in the system AND at which address they live. Reality is that a software update by a vendor of a device can change this list of models.

The list of all possible models has been published by the [SunSpec Alliance](https://sunspec.org/) on github: https://github.com/sunspec/models

## How to get the Modbus Schema for a SunSpec device
To read the data from a SunSpec device you first need to ask which models it has and at which address they live.

The SunSpec library I created does just that: 
- Interrogate the device which models are present and at what address 
- Use that list to convert the SunSpec models into a Modbus Schema for the device at that point in time.

{{< tabs >}}
{{% tab title="Maven" %}}
```xml
<dependency>
  <groupId>nl.basjes.sunspec</groupId>
  <artifactId>sunspec-device</artifactId>
  <version>{{%sunspec-device-version%}}</version>
</dependency>
```
{{% /tab %}}
{{< /tabs >}}

{{< tabs >}}
{{% tab title="Kotlin" %}}
```kotlin
// Read the schema by interrogating the device.
// In general this will result in many blocks and hundreds of Fields
// (My Solar inverter has 19 Blocks and 602 Fields).
val device = SunspecDevice.generate(
    modbusDevice, // Interrogate this device
    "Demo",       // Give it a name (Optional)
    true,         // true = Skip generating a fake Schema Block for a unknown models
) ?: throw ModbusException("Unable to generate SunSpec Schema")
```
{{% /tab %}}
{{< /tabs >}}

> [!WARNING] Persisting a SunSpec schema has risks!
> Persisting the schema of a SunSpec device to a Yaml file or to generated code is a risky operation.
> **You cannot assume that this code is usable for other devices also.** You cannot even assume it is usable on the SAME device after a software update.
> If you want to do this then it is recommended to include checks in the code if all the Fields in "Model 1" (the identification of the device including brand, type, serial number and software version) are the values that match the device for which it was generated.

## Lists of ...

Some SunSpec models define repeating groups of Fields.

Original SunSpec models like have a fixed block of Fields and then some of them have 1 or more instances of a repeated group of Fields.

In those cases you will see that I have named those Fields with essentially an array index (starting at 0 !!) in the name of the field. 

> [!INFO]
> These indexes are separated by '_' so it is possible to parse it if you really need to.

Take for example model 160 in which you may find fields like  `Module_0_ID` and `Module_5_ID`

## Lists of Lists of ...

In the newer models (like the 7xx series) the SunSpec goes a level deeper. Here you can find multiple nesting levels.

In for example model 710 ("DER high frequency trip model.").
- In addition to the base fields there are multiple "Curves".
- Each Curve has a "ReadOnly" indicator Field and 3 sub parts ("MustTrip", "MayTrip" and "MomCess"). 
  - Note that these do NOT have an array index because they are fixed parts!
- Each subpart has an array of points consisting of a frequency (Hz) and a time (Tms).

Some of the fields you get from this (with the values from the test case I have):

    'Ena':                       [ 'ENABLED' ]
    'AdptCrvReq':                [ '0' ]
    'AdptCrvRslt':               [ 'IN_PROGRESS' ]
    
    'Crv_0_ReadOnly':            [ 'R' ]
    'Crv_0_MustTrip_ActPt':      [ '5' ]
    'Crv_0_MustTrip_Pt_0_Hz':    [ '63.000' ]
    'Crv_0_MustTrip_Pt_0_Tms':   [ '0.160' ]
    'Crv_0_MustTrip_Pt_1_Hz':    [ '62.000' ]
    'Crv_0_MustTrip_Pt_1_Tms':   [ '0.160' ]
    'Crv_0_MustTrip_Pt_2_Hz':    [ '62.000' ]
    'Crv_0_MustTrip_Pt_2_Tms':   [ '300.000' ]
    'Crv_0_MustTrip_Pt_3_Hz':    [ '61.200' ]
    'Crv_0_MustTrip_Pt_3_Tms':   [ '300.000' ]
    'Crv_0_MustTrip_Pt_4_Hz':    [ '61.200' ]
    'Crv_0_MustTrip_Pt_4_Tms':   [ '400.000' ]
    
    'Crv_0_MayTrip_ActPt':       []
    'Crv_0_MayTrip_Pt_0_Hz':     []
    'Crv_0_MayTrip_Pt_0_Tms':    []
    'Crv_0_MayTrip_Pt_1_Hz':     []
    'Crv_0_MayTrip_Pt_1_Tms':    []
    'Crv_0_MayTrip_Pt_2_Hz':     []
    'Crv_0_MayTrip_Pt_2_Tms':    []
    'Crv_0_MayTrip_Pt_3_Hz':     []
    'Crv_0_MayTrip_Pt_3_Tms':    []
    'Crv_0_MayTrip_Pt_4_Hz':     []
    'Crv_0_MayTrip_Pt_4_Tms':    []
    
    'Crv_0_MomCess_ActPt':       []
    'Crv_0_MomCess_Pt_0_Hz':     []
    'Crv_0_MomCess_Pt_0_Tms':    []
    'Crv_0_MomCess_Pt_1_Hz':     []
    'Crv_0_MomCess_Pt_1_Tms':    []
    'Crv_0_MomCess_Pt_2_Hz':     []
    'Crv_0_MomCess_Pt_2_Tms':    []
    'Crv_0_MomCess_Pt_3_Hz':     []
    'Crv_0_MomCess_Pt_3_Tms':    []
    'Crv_0_MomCess_Pt_4_Hz':     []
    'Crv_0_MomCess_Pt_4_Tms':    []
    
    'Crv_1_ReadOnly':            [ 'RW' ]
    'Crv_1_MustTrip_ActPt':      [ '5' ]
    'Crv_1_MustTrip_Pt_0_Hz':    [ '63.000' ]
    'Crv_1_MustTrip_Pt_0_Tms':   [ '0.160' ]
    'Crv_1_MustTrip_Pt_1_Hz':    [ '62.000' ]
    'Crv_1_MustTrip_Pt_1_Tms':   [ '0.160' ]
    'Crv_1_MustTrip_Pt_2_Hz':    [ '62.000' ]
    'Crv_1_MustTrip_Pt_2_Tms':   [ '300.000' ]
    'Crv_1_MustTrip_Pt_3_Hz':    [ '61.200' ]
    'Crv_1_MustTrip_Pt_3_Tms':   [ '300.000' ]
    'Crv_1_MustTrip_Pt_4_Hz':    [ '61.200' ]
    'Crv_1_MustTrip_Pt_4_Tms':   [ '400.000' ]
    
    'Crv_1_MayTrip_ActPt':       []
    'Crv_1_MayTrip_Pt_0_Hz':     []
    'Crv_1_MayTrip_Pt_0_Tms':    []
    'Crv_1_MayTrip_Pt_1_Hz':     []
    'Crv_1_MayTrip_Pt_1_Tms':    []
    'Crv_1_MayTrip_Pt_2_Hz':     []
    'Crv_1_MayTrip_Pt_2_Tms':    []
    'Crv_1_MayTrip_Pt_3_Hz':     []
    'Crv_1_MayTrip_Pt_3_Tms':    []
    'Crv_1_MayTrip_Pt_4_Hz':     []
    'Crv_1_MayTrip_Pt_4_Tms':    []
    
    'Crv_1_MomCess_ActPt':       []
    'Crv_1_MomCess_Pt_0_Hz':     []
    'Crv_1_MomCess_Pt_0_Tms':    []
    'Crv_1_MomCess_Pt_1_Hz':     []
    'Crv_1_MomCess_Pt_1_Tms':    []
    'Crv_1_MomCess_Pt_2_Hz':     []
    'Crv_1_MomCess_Pt_2_Tms':    []
    'Crv_1_MomCess_Pt_3_Hz':     []
    'Crv_1_MomCess_Pt_3_Tms':    []
    'Crv_1_MomCess_Pt_4_Hz':     []
    'Crv_1_MomCess_Pt_4_Tms':    []


## Example
An example on how this can be used in a Kotlin Script 

{{< notice style="green" icon="screwdriver-wrench" title="GetSunSpec.main.kts" expanded="false" >}}
{{< code language="kotlin" source="code/GetSunSpec.main.kts" >}}
{{< /notice >}}

To get a sense of what this results in: One of the tables printed in the loop of this code looked like this (my solar panels were making only 190 Watt at that moment):
```
|-----------+----+--------------+------+----------------------------------------------------------------------------------+----------------------------------------------------------|
| Block     | ID | Value        | Unit | Block Description                                                                | Field Description                                        |
|-----------+----+--------------+------+----------------------------------------------------------------------------------+----------------------------------------------------------|
| Model 1   | Mn | SMA          |      | Common: All SunSpec compliant devices must include this as the first model       | Well known value registered with SunSpec for compliance. |
| Model 1   | Md | SB3.6-1AV-41 |      | Common: All SunSpec compliant devices must include this as the first model       | Manufacturer specific value (32 chars).                  |
| Model 1   | Vr | 4.01.15.R    |      | Common: All SunSpec compliant devices must include this as the first model       | Manufacturer specific value (16 chars).                  |
| Model 1   | SN | 3005067415   |      | Common: All SunSpec compliant devices must include this as the first model       | Manufacturer specific value (32 chars).                  |
| Model 101 | W  | 190.0        | W    | Inverter (Single Phase): Include this model for single phase inverter monitoring | AC Power.                                                |
|-----------+----+--------------+------+----------------------------------------------------------------------------------+----------------------------------------------------------|
```
