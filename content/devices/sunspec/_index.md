+++
title = 'Sunspec'
weight = 10
+++

SunSpec is special.

SunSpec is essentially collection of partial mappings (they call Models) and each Model is a set of field that describe a functionality an actual device CAN have.
There are models for being a solar inverter, for being a battery and for things like IPv4 network support.
It depends on the actual device which of these models are present in the system AND at which address they live.

Reality is that a software update by a vendor of a device can (and will) change the available Models.

So to read the data from a SunSpec device you first need to ask which models it has and at which address they live.

The SunSpec library I created does just that: Interrogate the device and generate on the fly a SchemaDevice which has Fields for everything this specific system supports.

https://github.com/nielsbasjes/sunspec-device

An example on how this can be used in a Kotlin Script 

{{< notice style="green" icon="screwdriver-wrench" title="GetSunSpec.main.kts" >}}
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
