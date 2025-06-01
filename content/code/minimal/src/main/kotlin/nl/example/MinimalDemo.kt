package nl.example

import nl.basjes.modbus.device.api.MODBUS_STANDARD_TCP_PORT
import nl.basjes.modbus.device.plc4j.ModbusDevicePlc4j

fun doMinimalExample() {
    // The hostname to connect to
    val modbusIp   = "modbus.iot.basjes.nl"
    val modbusPort = MODBUS_STANDARD_TCP_PORT
    val modbusUnit = 1

    print("Modbus: Connecting...")
    // Connect to the real Modbus device over TCP using the Apache PLC4J library
    ModbusDevicePlc4j("modbus-tcp:tcp://${modbusIp}:${modbusPort}?unit-identifier=${modbusUnit}")
        .use { modbusDevice ->
            println(" done")

            // Get the schema as the generated code class
            val device = Minimal()

            // Connect this Schema Device to the physical device (via Plc4J in this case))
            device.connect(modbusDevice)

            // No need to guess the names of the blocks and fields.
            device.block1.name.need()

            // Now we tell the system all fields must be made up-to-date and we consider values
            // less than 5 seconds old to be "new enough".
            device.update(5000) // << Here the modbus calls are done.

            // No need to search for the return type of the Field (strong typed generated code).
            println("Name: ${device.block1.name.value}")
        }
}
