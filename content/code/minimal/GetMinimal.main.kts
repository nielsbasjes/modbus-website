#!/usr/bin/env -S kotlin -howtorun .main.kts 

// Include the needed libraries
@file:DependsOn("nl.basjes.modbus:modbus-api-j2mod:0.14.0")
@file:DependsOn("nl.basjes.modbus:modbus-schema-device:0.14.0")

// Regular Kotlin import statements
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import nl.basjes.modbus.device.api.MODBUS_STANDARD_TCP_PORT
import nl.basjes.modbus.device.j2mod.ModbusDeviceJ2Mod
import nl.basjes.modbus.schema.get
import nl.basjes.modbus.schema.toSchemaDevice
import nl.basjes.modbus.schema.toTable
import nl.basjes.modbus.schema.toYaml
import java.io.File

// The hostname to connect to
val modbusHost        = "modbus.iot.basjes.nl"
val modbusPort        = MODBUS_STANDARD_TCP_PORT
val modbusUnit        = 1

print("Modbus: Connecting...")
val modbusMaster = ModbusTCPMaster(modbusHost, modbusPort)
modbusMaster.connect()
ModbusDeviceJ2Mod(modbusMaster, modbusUnit). use { modbusDevice ->
    println(" done")

    // Read the schema from a file
    val schema = File("minimal.yaml").readText(Charsets.UTF_8)

    // Convert that into a SchemaDevice with all the defined mappings (Blocks and Fields)
    val device = schema.toSchemaDevice()

    // Connect this Schema Device to the physical device (via Plc4J in this case))
    device.connect(modbusDevice)

    // Get a reference to some of the available fields
    val name = device["Block 1"]["Name"]
        ?: throw IllegalArgumentException("Unable to get the \"Block 1\" -> \"Name\" Field")

    // Real Modbus devices are so very slow that we need to indicate which need to be kept up to date
    name .need()

    // Now we tell the system all fields must be made up-to-date and we consider values
    // less than 5 seconds old to be "new enough".
    device.update(5000) // << Here the modbus calls are done.

    // Output the results as a table ( "----" is a not retrieved register, "xxxx" is a read error )
    println(device.toTable(onlyUseFullFields = false, includeRawDataAndMappings = true))

    // Now create a NEW test scenario from the currently available values and add that to the
    // schema definition.
    // Because we have not retrieved ALL registers this test contains severall null values
    // which were present in the original
    device.createTestsUsingCurrentRealData()

    // And print the entire thing as a reusable schema yaml (which now has 2 test scenarios!!)
    println("#-------------- BEGIN: MODBUS SCHEMA IN YAML FORMAT --------------")
    println(device.toYaml())
    println("#-------------- END: MODBUS SCHEMA IN YAML FORMAT ----------------")
}
