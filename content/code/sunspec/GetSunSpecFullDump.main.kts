#!/usr/bin/env -S kotlin -howtorun .main.kts 

// Include the needed libraries
@file:DependsOn("nl.basjes.modbus:modbus-api-j2mod:0.14.0")
@file:DependsOn("nl.basjes.sunspec:sunspec-device:0.7.3")

// Regular Kotlin import statements
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import nl.basjes.modbus.device.api.MODBUS_STANDARD_TCP_PORT
import nl.basjes.modbus.device.exception.ModbusException
import nl.basjes.modbus.device.j2mod.ModbusDeviceJ2Mod
import nl.basjes.modbus.schema.toTable
import nl.basjes.sunspec.device.SunspecDevice

// The hostname to connect to
val modbusHost        = "sunspec.iot.basjes.nl"

// Use the standards for SunSpec to connect to the device
val modbusPort        = MODBUS_STANDARD_TCP_PORT
val modbusUnit        = 126 // SMA uses 126, other vendors can differ

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

    // Fetch the registers for all defined Fields
    device.updateAll()

    // Output the results as a table ( Where "----" is a not retrieved register, "xxxx" is a read error )
    // Because this is SunSpec you will see a table with hundreds of Fields and only a few have a value.
    println(device.toTable(onlyUseFullFields = false, includeRawDataAndMappings = true))
}
