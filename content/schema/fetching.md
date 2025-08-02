+++
title = 'Fetching registers'
weight = 30
+++

The retrieval of the register/discrete values that are needed has a few special points to consider

Note: Here I mostly write 'registers'. Most of this works in the exact same way for the discretes. The only real difference is that fields can use a range of registers as required input and discretes can only be used in isolation (i.e. a single discrete).

## All field registers are in a single request
In one of my devices I have I found that if I retrieve a subset of the registers that are needed for a specific logical field I will either get a modbus error or I get nonsense values.

This means that it is essential that all registers for a specific field to be retrieved in the same modbus request.
To ensure this is always done correctly the concept of the `fetch group` was created.
This is simply a unique string that ensures that all register fetches are done correctly.

Normally the system generated value is good enough.
If however a set of fields need to be retrieved in a combination then it is needed to specify a manually defined fetch group.
Situations where this is needed are rare, currently I only know of the group type 'sync' which is currently only used in Model 704 of SunSpec.

## Some fields never change
In SunSpec some field have been documented as `static`; this means that the value does not change for this device at all.
This includes fields like the serial number and software version of the device.

In this toolkit a single field can be marked as `immutable` which makes it that once the field has been retrieved it will not be explicitly retrieved anymore. 
Note that the query optimizer MAY decide to include it anyway to reduce the number of modbus requests.

## Retrieved modbus values
All registers have a modbus value associated with it.
Such a modbus value can be one of these states:
- Not yet retrieved
- Has a valid value with a timestamp when it was retrieved
- Has been marked as a `soft error`. This means that retrieval as an optimization is avoided and explicit retrieval is still done.
- Has been marked as a `hard error`. This means that retrieval is not done at all anymore.

## Base retrieval model
The base model is that the consumer indicates that it needs a certain field from the schema by calling the `need()` function and call the `unNeed()` function if it is no longer needed. Each field has a counter (semaphore) that records how many still need it.

Then the `update()` function is called with an optional parameter indicating the allowed maximum age of the underlying register values.
The system will ensure that on return of this call all has been done to retrieve the values.

The consumer can check what the `ReturnType` of this `Field` is and based on that get the value from the appropriate getter. 

In common usecases the consumer can now do the `update()` and get the updated values of the fields.

## Getting the modbus values
The default non optimized query system does the following
- Get all _needed_ fields from the Schema Device
- Get all fetch groups for those fields
- For each fetch group create a ModbusQuery that represents the set of registers that must be retrieved.
- For each ModbusQuery do the call to the actual modbus device and store the returned values.
  - If a device returns error values then these are stored as `hard error`.

## Query Optimizer
By default, the optimized variant of the fetcher is used which does a few things extra.

The Modbus limit is that at most 125 registers (2000 discretes) can be retrieved in a single Modbus Request. 
Some devices have a lower limit which is respected by the optimizer.

The ModbusQueries created by the previous are the smallest allowed queries.
The optimizer gets those and combines them into a smaller number of MergedModbusQuery objects (respecting the specified query size limit).

The optimizer is allowed to fill in the holes between the needed registers.
The default setting is that it fills at most 100 registers (1600 discretes).
For those holes a HoleModbusQuery is created.
An important effect is that in a MergedModbusQuery all registers that are part of it are covered by a single underlying ModbusQuery. 
This is needed in error recovery situations.

The maximum hole size is configurable when connecting the modbus device to the schema device.
```kotlin
schemaDevice.connect(modbusDevice, 100)
```

Now the retrieval goes normal, only in case of errors things move differently
- For each ModbusQuery do the call to the actual modbus device.
  - If a good result store the returned values
  - If this returns an error then the system does error recovery depending on the type of query
    - ModbusQuery (i.e. single fetchgroup) 
      - error values are stored as `hard error` (i.e. is this bad an no need to retry).
    - HoleModbusQuery (i.e. an optimization artifact) 
      - error values are stored as `soft error` (i.e. retry may be useful if you explicitly need a field in this range).
    - MergedModbusQuery (i.e. multiple underlying queries)
      - The underlying queries are recombined into multiple smaller queries and ech is recursively retried.
