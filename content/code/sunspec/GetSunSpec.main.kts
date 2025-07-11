#!/usr/bin/env kotlin

// Include the needed libraries
@file:DependsOn("nl.basjes.modbus:modbus-api-j2mod:0.11.0")
@file:DependsOn("nl.basjes.sunspec:sunspec-device:0.6.0")

// Regular Kotlin import statements
import nl.basjes.modbus.device.api.MODBUS_STANDARD_TCP_PORT
import nl.basjes.modbus.device.exception.ModbusException
import nl.basjes.modbus.device.plc4j.ModbusDevicePlc4j
import nl.basjes.modbus.schema.ReturnType
import nl.basjes.modbus.schema.get
import nl.basjes.modbus.schema.toTable
import nl.basjes.modbus.schema.utils.StringTable
import nl.basjes.sunspec.SUNSPEC_STANDARD_UNITID
import nl.basjes.sunspec.device.SunspecDevice
import java.lang.Thread.sleep

// The hostname to connect to
val modbusIp          = "sunspec.iot.basjes.nl"

// Use the standards for SunSpec to connect to the device
val modbusPort        = MODBUS_STANDARD_TCP_PORT
val modbusUnit        = SUNSPEC_STANDARD_UNITID

print("Modbus: Connecting...")
val modbusMaster = ModbusTCPMaster(modbusHost, modbusPort)
modbusMaster.connect()
ModbusDeviceJ2Mod(modbusMaster, modbusUnit). use { modbusDevice ->
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

    // Get a reference to some of the available fields
    val manufacturer = device["Model 1"]["Manufacturer"]  ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"Manufacturer\" Field")
    val model        = device["Model 1"]["Model"]         ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"Model\" Field")
    val version      = device["Model 1"]["Version"]       ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"Version\" Field")
    val serialNumber = device["Model 1"]["Serial Number"] ?: throw IllegalArgumentException("Unable to get the \"Model 1\" -> \"Serial Number\" Field")

    val singlePhaseInverterACPower = device["Model 101"]["W"] ?: throw IllegalArgumentException("Unable to get the \"Model 101\" -> \"W\" Field")

    val wantedFields = listOf(manufacturer, model, version, serialNumber, singlePhaseInverterACPower)

    // Real Modbus devices are so very slow that we need to indicate which need to be kept up to date
    wantedFields.forEach { it.need() }

    // For this demo we just do 10 fetches with a ~1 second delay between them.
    for ( i in 0 .. 10) {
        sleep(1000)
        println("== FETCH $i ======================================\n")

        // Now we tell the system all fields must be made up-to-date and we want everything to be updated.
        device.update() // << Here the modbus calls are done.

        // Note that:
        // - We did not ask for the "Opt" field. It WILL be retrieved because the optimizing fetcher calculated that to be more efficient.
        // - On the second loop only the singlePhaseInverterACPower will be updated. The others are marked as immutable and have not changed.


        // For this demo we are putting the values in a table that is printed on the screen.
        // Normally you would push it into a processing and/or storage system (like InfluxDb or Apache IoTDB)
        val table = StringTable()
        table.withHeaders("Block", "ID", "Value", "Unit", "Block Description", "Field Description")

        for (field in wantedFields) {
            table.addRow(
                field.block.id,
                field.id,
                // The produced value will be made available in a strongly typed property of the Field.
                // This allows for a clean use of the data in further calculations.
                when (field.returnType) {
                    ReturnType.LONG       -> field.longValue.toString()
                    ReturnType.DOUBLE     -> field.doubleValue.toString()
                    ReturnType.STRING     -> field.stringValue.toString()
                    ReturnType.STRINGLIST -> field.stringListValue.toString()
                    ReturnType.BOOLEAN    -> TODO("Support for Booleans is not there yet")
                    ReturnType.UNKNOWN    -> "<< D'oh! >>"
                },
                field.unit,
                field.block.description ?: "",
                field.description,
            )
        }
        println(table)
    }
    println("========================================\n")

    // Output the results as a table ( Where "----" is a not retrieved register, "xxxx" is a read error )
    // Because this is SunSpec you will see a table with hundreds of Fields and only a few have a value.
    println(device.toTable(onlyUseFullFields = false, includeRawDataAndMappings = true))
}
