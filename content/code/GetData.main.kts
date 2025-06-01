#!/usr/bin/env kotlin

// Include the needed libraries
@file:DependsOn("nl.basjes.modbus:modbus-api-plc4j:0.6.0")
@file:DependsOn("nl.basjes.modbus:modbus-schema-device:0.6.0")

// Regular Kotlin import statements
import nl.basjes.modbus.device.api.MODBUS_STANDARD_TCP_PORT
import nl.basjes.modbus.device.plc4j.ModbusDevicePlc4j
import nl.basjes.modbus.schema.get
import nl.basjes.modbus.schema.toSchemaDevice
import nl.basjes.modbus.schema.toTable
import nl.basjes.modbus.schema.toYaml
import java.io.File

// The hostname to connect to
val modbusIp          = "sunspec.iot.basjes.nl"

// Use the standards for SunSpec to connect to the device
val modbusPort        = MODBUS_STANDARD_TCP_PORT
val modbusUnit        = 126  // This is the SunSpec specific Modbus Unit ID

print("Modbus: Connecting...")
// Connect to the real Modbus device over TCP using the Apache PLC4J library
ModbusDevicePlc4j("modbus-tcp:tcp://${modbusIp}:${modbusPort}?unit-identifier=${modbusUnit}")
    .use { modbusDevice ->
    println(" done")

    // Read the schema from a file
    val schema = File("example.yaml").readText(Charsets.UTF_8)

    // Convert that into a SchemaDevice with all the defined mappings (Blocks and Fields)
    val device = schema.toSchemaDevice()

    // Connect this Schema Device to the physical device (via Plc4J in this case))
    device.connect(modbusDevice)

    // Get a reference to some of the available fields
    val manufacturer = device["Model 1"]["Mn"] ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"Mn\" Field")
    val model        = device["Model 1"]["Md"] ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"Md\" Field")
    val version      = device["Model 1"]["Vr"] ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"Vr\" Field")
    val serialNumber = device["Model 1"]["SN"] ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"SN\" Field")

    // Real Modbus devices are so very slow that we need to indicate which need to be kept up to date
    manufacturer .need()
    model        .need()
    version      .need()
    serialNumber .need()

    // Now we tell the system all fields must be made up-to-date and we consider values less than 5 seconds old to be "new enough".
    device.update(5000) // << Here the modbus calls are done.

    // Note that we did not ask for the "Opt" field which is physically between the ones we did ask for.
    // The "Opt" field WILL be retrieved because the optimizing fetcher will have calculated that to be more efficient.

    // Output the results as a table ( Where "----" is a not retrieved register, "xxxx" is a read error )
    println(device.toTable(onlyUseFullFields = false, includeRawDataAndMappings = true))

    // Now create a NEW test scenario from the currently available values and add that to the schema definition
    // Because we have not retrieved ALL registers this test contains severall null values which were present in the original
    device.createTestsUsingCurrentRealData()

    // And print the entire thing as a reusable schema yaml (which now has 2 test scenarios!!)
    println("#-------------- BEGIN: MODBUS SCHEMA --------------")
    println(device.toYaml())
    println("#-------------- END: MODBUS SCHEMA ----------------")
}
