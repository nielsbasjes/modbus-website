+++
title = 'Schema'
weight = 30
+++

{{% notice style="red" icon="bug" title="Incomplete documentation" %}}
Page is not yet finished. Is incomplete and will contain inaccuracies.
{{% /notice %}}

# Schema concept:
There are modbus devices that (within a single function and set of registers) have defined blocks with registers that belong together.

In most modbus devices a schema is only a single block and any variable can be retrieved independently.

SunSpec is the best example where it works differently.

Sunspec is really a meta-schema where you retrieve the actual set of schema blocks that are present on the device.
So the offset of such a block on the actual device varies because each type of device has a different list of blocks.

In Sunspec these blocks are called models and each of those has groups that can have the requirement of being fetched in a single request or can repeat multiple times.

So in this schema standard we have

- **Schema**:
    - A set of blocks for a single device with the same base parameters (like same base offset and functioncode)
- **Block**:
    - A group of fields which are based on registers (with optionally its own offset from the schema)
- **Field**:
    - A single _logical value_ that is based on 1 or more registers within the block.
    - A field can have a 'fetch group' value which indicates that it can only be fetched if the entire 'fetch group' is fetched.
    - A field can have the immutable flag which means there is no need to read it again on a refresh of the data.
- **Register**: A modbus register that needs to be fetched. All registers with the same 'fetch group' value are always retrieved in a single modbus request.

Remarks about the 'fetch group':
- By default, a register does not have a fetch group.
- If a field needs a register then that register gets a fetch group from the field.
    - The normal 'fetch group' value from a field is an id based upon the Field itself.
    - If a Field has an explicit 'fetch group' then that is used.