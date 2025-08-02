+++
title = 'Schema Structure'
weight = 10
+++

The Schema is the collection of everything needed to retrieve the data from the physical device and convert it into meaningful values.

## Base concept: Device, Block, Field, Register

- A **device** is a collection of blocks
- A **block** a collection of fields that make sense to keep together (similar to a name space as used in many programming languages).
  - Example: A heat-pump has a block for 'heating' and one for 'cooling', a SunSpec device has a block per SunSpec model.
- A **field** is a logical usable value which can be retrieved from the device.
  - Each field has a name that is unique within a block.
  - Some fields are marked as 'system' because they are intermediate values (like a scaling factor) or things like padding. These are actually not a "logical usable value".
- A field has an **expression** that determines where it's value comes from.
  - Most expressions consist of the interpretation of a set of modbus registers/discrete combined with a calculation. The expression can also be a constant, and it can also be calculated by combining values from multiple registers or even other fields.

## Block and Field IDs
It is important to note that the `id` of both a **block** and **field** will be used as a basis for code generation and expressions.

This library allows for a more free naming scheme while still allowing the use in expressions.

Simply said these ids must conform to: 
- Only ASCII letters, digits, the '_' and space are allowed.
- Must start with a letter
- May contain spaces which will be an exact part of the field name.

Technically speaking: 
```regexp
^[a-zA-Z]([a-zA-Z0-9_ ]*[a-zA-Z0-9_]+)?$
```

If this value contains spaces the code generation process will remove them and do (nice looking) magic on uppercase/lowercase of the various parts.
This is needed because most programming languages do not allow spaces in places like variable and class names.
