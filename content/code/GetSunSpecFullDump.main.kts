#!/usr/bin/env kotlin

// Include the needed libraries
@file:DependsOn("nl.basjes.modbus:modbus-api-plc4j:0.6.0")
@file:DependsOn("nl.basjes.sunspec:sunspec-device:0.4.1")

// Regular Kotlin import statements
import nl.basjes.modbus.device.api.MODBUS_STANDARD_TCP_PORT
import nl.basjes.modbus.device.exception.ModbusException
import nl.basjes.modbus.device.plc4j.ModbusDevicePlc4j
import nl.basjes.modbus.schema.toTable
import nl.basjes.sunspec.SUNSPEC_STANDARD_UNITID
import nl.basjes.sunspec.device.SunspecDevice

// The hostname to connect to
val modbusIp          = "sunspec.iot.basjes.nl"

// Use the standards for SunSpec to connect to the device
val modbusPort        = MODBUS_STANDARD_TCP_PORT
val modbusUnit        = SUNSPEC_STANDARD_UNITID

print("Modbus: Connecting...")
// Connect to the real Modbus device over TCP using the Apache PLC4J library
ModbusDevicePlc4j("modbus-tcp:tcp://${modbusIp}:${modbusPort}?unit-identifier=${modbusUnit}")
    .use { modbusDevice ->
    println(" done")

    // Read the schema by interrogating the device (so no Yaml file).
    // In general this will result in many blocks and hundreds of Fields (My Solar inverter has 19 Blocks and 602 Fields).
    val device = SunspecDevice.generate(
        modbusDevice, // Interrogate this device
        "Demo",       // Give it a name (Optional)
        true,         // true = Skip generating a fake Schema Block for a unknown models
    ) ?: throw ModbusException("Unable to generate SunSpec Schema")

    println("The SchemaDevice for this specific SunSpec device has ${device.blocks.size} Blocks with a total of ${device.fields.size} fields.")

    // Connect this Schema Device to the physical device (via Plc4J in this case)
    device.connect(modbusDevice)

    // Fetch the registers for all defined Fields
    device.updateAll()

    // Output the results as a table ( Where "----" is a not retrieved register, "xxxx" is a read error )
    // Because this is SunSpec you will see a table with hundreds of Fields and only a few have a value.
    println(device.toTable(onlyUseFullFields = false, includeRawDataAndMappings = true))
}
