+++
title = 'Field Expressions'
weight = 20
+++

All fields have exactly 1 expression that defines where the value comes from.

In an expression you can have 
- constant values 
- a single value which is an interpretation of a sequence of modbus registers or a discrete
- references to other fields
which can all combined into expressions to produce a single final value.

## Return type
Each expression will have a return type that depends on the actual expression.
Supported return types:
- Boolean
  - A simple true/false value
- Long
  - A 64-bit integer value
- Double
  - A 64-bit floating point value
- String
  - A textual value
- StringList
  - A collection of String values.

There is no way to specify the return type, it is automatically determined from the expression.

## Register and Discrete addresses

In the modbus world the `defacto` way of specifying a modbus address is the `Modicon notation`.

This combines the type of register with a register number in the 1 to 9999 range.
The physical address of such a register is 1 lower (i.e. 0-9998). This means that not all registers that are possible (0-65535) can be used.

{{% notice style="red" icon="bug" title="Confusing address notations" %}}
Depending on the vendor either the 'old style' notation is used (where all registers are +1 of their actual address) or the actual address is used. This is a serious point of attention when converting an existing mapping so it can be used here.
{{% /notice %}}

This specification allows for the following notations for a single register (All these examples show the same address):

- **Modicon notation (5 digits).**
  - Is 1 more than the actual wire address
  - Only allows for a small subset of the possible registers: 1-9999
  - Example: **14322**
- **Modicon notation (6 digits).**
  - Is 1 more than the actual wire address.
  - Allows for all registers to be used (1-65536)
  - Example: **104322**
- **The 'x' separated notation.**
  - The type of register and the address are separated by an 'x'
  - Is 1 more than the actual wire address.
  - Allows for all registers to be used (1-65536)
  - Example: **1x4322**  or  **1x000004322**
- The named and exact notation
  - A named notation that uses the exact wire address (no '+1' anymore), uses a ':' as separator and allows for all registers to be used (1-65536)
  - Supported names (and the allowed abbreviations). All are case-INsensitive.
    - **coil** or **c**
    - **discrete-input** or **di**
    - **input-register** or **ir**
    - **holding-register** or **hr**
  - Example: **discrete-input:4321**   or  **di:000004321**   or   **di:4321**

## A set of registers

In many places 2 or more consecutive registers are needed.

Assume we need to specify the list of these 4 registers:  hr:2 hr:3 hr:4 hr:5

There are 3 supported notations for multiple registers:
- address#count
  - A sequence of a total of 4 registers and hr:2 is the first one.
  - **hr:2#4**
- address .. address
  - A range where both the first and last mentioned are included in the list.
  - **hr:2..hr:5**
- address, address, address, address, address
  - A comma separated list of registers.
  - **hr:2, hr:3, hr:4, hr:5**

For defining ranges any of the allowed register notations can be used and MIXED (don't do it, but the spec allows it). So something as horrific as this actually works `int64(coil:123, c:124, 0x00126, 00127)`, because the latter two are 'off by 1' the seemingly missing `125` is actually not missing at all.

The main reason for supporting all of these formats is to make the transition from an existing mapping as simple as possible because the existing notation can be used. 

All registers must be a single incremental unique list without any gaps!
But they MAY be used out of order as a parameter for a conversion function.

## Operations

### Notes
Some notes about the operations.
- Some operations return a set of registers that can then be passed to other functions.
- Some operations can be made to recognize special values that mean the feature of the device at hand is not supported or disabled. In such operations you can specify a sequence of register values that indicate this fact. If you see `<notImplemented>` in the operations below this is an optional ';' separated list of HEX register values that are to be interpreted as such.
- Any operation that cannot provide a value must effectively indicate this to the caller. Any function that expects a value and receives such an indication must pass this to their caller.

- `<discrete>` is a single address of a Coil of Discrete Input.
- `<registers>` is single range of registers addresses or register values.

### Bit manipulations
All functions work with the input being little endian.
In case the real data is not that then these two functions and the fact that you can explicitly specify the registers in the correct order are enough to fix this.

- `swapendian (<registers>)` --> Register(s)
  - Reverse 16 bits: 0xABCD ( 10101011 11001101 ) into 0xB3D5 ( 10110011 11010101 )
- `swapbytes (<registers>)`  --> Register(s)
  - Reverse 2 bytes: 0xABCD into 0xCDAB

### Strings

- `'something'` --> String
  - A constant string value
- `utf8(<registers>)` --> String
  - Interpret the provided bytes as UTF8 and convert them to a String
- `hexstring(<registers>)` --> String
  - Convert the provided bytes into a HEX string (i.e. "0xAB 0xCD")
- `concat(<string> [, <string>]* )` --> String
  - Concatenate the comma separated list of Strings into 1 final string. The provided string values can also be field names, numerical values or boolean values which are converted into a string before concatenation.

### Numbers
- `ieee754_32(<2 registers>)` --> Floating point number
  - Interpret the 2 provided registers as an ieee754 32 floating point number.
- `ieee754_64(<4 registers>)` --> Floating point number
  - Interpret the 4 provided registers as an ieee754 64 floating point number.
- `int16(<registers> ; <notImplemented>)` --> Signed Integer number
  - Interpret the 1 provided register as a signed 16 bit integer number.
- `int32(<registers> ; <notImplemented>)` --> Signed Integer number
  - Interpret the 2 provided registers as a signed 32 bit integer number.
- `int64(<registers> ; <notImplemented>)` --> Signed Integer number
  - Interpret the 4 provided registers as a signed 64 bit integer number.
- `uint16(<registers> ; <notImplemented>)` --> Unsigned Integer number
  - Interpret the 1 provided register as an unsigned 16 bit integer number.
- `uint32(<registers> ; <notImplemented>)` --> Unsigned Integer number
  - Interpret the 2 provided registers as an unsigned 32 bit integer number.
- `uint64(<registers> ; <notImplemented>)` --> Unsigned Integer number
  - Interpret the 4 provided registers as an unsigned 64 bit integer number.
  - NOTE: Many programming languages (like JVM based) do NOT HAVE an unsigned 64 bit integer type. This will trigger a failure if such a too large is found.

All numbers can be combined into calculations that follow PEMDAS to determine the final result that is desired.

### Sets

- `enum(<registers> ; <notImplemented> ; <mapping>)` --> String
  - Interpret the registers as an unsigned integer and use the provided mapping to convert this value into a single String.
  - Example: `enum( ir:55 ;1->'Manual operation'; 2-> 'Defrost'; 3-> 'Hot water'; 4-> 'Heat'; 5-> 'Cool'; 6-> 'Pool'; 7-> 'Anti legionella'; 8-> 'Passive Cooling'; 98-> 'Standby' ;99-> 'No demand' ;100-> 'OFF')`
- `bitset(<registers> ; <notImplemented> ; <mapping>)` --> Set of Strings
  - Interpret the bits in the registers as booleans and for each SET bit the provided String is output as part of the resulting list of values.
  - Example: The register value "0x0005" when passed through `bitset(hr:0 ; 0xDEAD ; 0-> 'Zero'; 1-> 'One'; 2-> 'Two')` will result in a set with the values "Zero" and "Two"

### Booleans
- `true` or `false`     --> Boolean
- `boolean(<discrete>)` --> Boolean
  - This simply returns a boolean (0=false, 1=true) based upon the indicated discrete address (coil or discrete input).
- `boolean(<discrete>; <0 string> ; <1 string>)` --> String
  - This converts the discrete value into a String using the provided mapping.
- `boolean(<boolean>; <false string> ; <true string>)` --> String
  - This converts the boolean value into a String using the provided mapping.
- `bitsetbit(<registers> ; <notImplemented> ; <bitNr>)`  --> Boolean
  - Similar to `bitset` but this returns a boolean (0=false, 1=true) based upon the indicated bit.

### Specials
- `eui48(<3 or 4 registers> ; <notImplemented>)` --> String
  - Interpret the provided 3 or 4 registers as a MAC address (as used in SunSpec)
  - `0102 0304 0506` --> `01:02:03:04:05:06`
- `ipv4addr(<registers> ; <notImplemented>)` --> String
  - Interpret the provided 4 registers as a binary IPv4 network address and convert is into a '.' string
  - `0102 0304` --> `1.2.3.4`
- `ipv6addr(<registers> ; <notImplemented>)` --> String
  - Interpret the provided 4 registers as a binary IPv4 network address and convert is into a '.' string
  - `0001 0203 0405 0607 0809 0A0B 0C0D 0E0F` --> `0001:0203:0405:0607:0809:0A0B:0C0D:0E0F`


<!--
  Modbus Schema Toolkit
  Copyright (C) 2019-2025 Niels Basjes

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
