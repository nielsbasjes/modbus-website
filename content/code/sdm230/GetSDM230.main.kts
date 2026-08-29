#!/usr/bin/env -S kotlin -howtorun .main.kts 

@file:DependsOn("nl.basjes.modbus:modbus-api-j2mod:0.14.0")
@file:DependsOn("nl.basjes.modbus:modbus-schema-device:0.14.0")
//@file:DependsOn("nl.basjes.modbus.devices:modbus-device-sdm230:0.6.2")
@file:DependsOn("org.questdb:questdb-client:1.0.1")
@file:DependsOn("org.apache.logging.log4j:log4j-to-slf4j:2.24.3")
@file:DependsOn("org.slf4j:slf4j-simple:2.0.17")

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster
import com.ghgande.j2mod.modbus.util.SerialParameters
import io.questdb.client.Sender
import nl.basjes.modbus.device.j2mod.ModbusDeviceJ2Mod

import nl.basjes.modbus.schema.Field
import nl.basjes.modbus.schema.ReturnType.*
import nl.basjes.modbus.schema.SchemaDevice
import nl.basjes.modbus.schema.get
import nl.basjes.modbus.schema.toSchemaDevice
import java.io.File
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.system.exitProcess

val serialParameters = SerialParameters()

//serialParameters.portName       = "/dev/ttyUSB0"
serialParameters.portName       = "/dev/ttyACM0"
serialParameters.baudRate       = 38400
serialParameters.flowControlIn  = 0
serialParameters.flowControlOut = 0
serialParameters.databits       = 8
serialParameters.stopbits       = 1
serialParameters.parity         = 0
serialParameters.openDelay      = 0
serialParameters.encoding       = "rtu"

val modbusUnit           :Int    = 1
val databaseUrl          :String = "http::logger.manage.basjes.nl:9009"
val databaseTableName         :String = "weerdesteyn_boiler" // "some_table"

print("Connecting...")
ModbusDeviceJ2Mod(ModbusSerialMaster(serialParameters), modbusUnit).use { modbusDevice ->
    println(" done")

    // Read the schema from a file
    val schema = File("SDM230.yaml").readText(Charsets.UTF_8)

    val device = schema
        // Convert that into a SchemaDevice with all the defined mappings (Blocks and Fields)
        .toSchemaDevice()
        // Connect this Schema Device to the physical device
        .connect(modbusDevice)

//    if (
//        databaseUrl         == null ||
//        databaseTableName        == null
//    ) {
//        println("No database, outputting to console")
//        runLoop(device, null, "console")
//        return
//    } else {
        println("Connecting to database $databaseUrl")
        Sender
            .builder(Sender.Transport.TCP)
            .address(databaseUrl)
            .autoFlushRows(1)
            .build()
            .use { sender ->
                runLoop(device,sender, databaseTableName)
            }
//    }
}

fun runLoop(device: SchemaDevice, sender: Sender, databaseTableName: String) {
    // Use these fields as Measurements towards InfluxDB
    val allFields = mutableMapOf<String, Field>()

    fun storeField(name: String, field: Field?) {
        if (field == null) {
            println("The field with $name is null")
            exitProcess(1)
        }
        allFields[name] = field
    }
    
    storeField("Line_to_neutral_volts",                      device["measurements"]["Line to neutral volts"])
    storeField("Current",                                    device["measurements"]["Current"])
    storeField("Active_power",                               device["measurements"]["Active power"])
    storeField("Apparent_power",                             device["measurements"]["Apparent power"])
    storeField("Reactive_Power",                             device["measurements"]["Reactive Power"])
    storeField("Power_Factor",                               device["measurements"]["Power Factor"])
    storeField("Phase_Angle",                                device["measurements"]["Phase Angle"])
    storeField("Frequency",                                  device["measurements"]["Frequency"])
    storeField("Import_active_energy",                       device["measurements"]["Import active energy"])
    storeField("Export_active_energy",                       device["measurements"]["Export active energy"])
    storeField("Import_reactive_energy",                     device["measurements"]["Import reactive energy"])
    storeField("Export_reactive_energy",                     device["measurements"]["Export reactive energy"])
    storeField("Total_System_power_demand",                  device["measurements"]["Total System power demand"])
    storeField("Maximum_Total_System_power_demand",          device["measurements"]["Maximum Total System power demand"])
    storeField("Current_system_positive_power_demand",       device["measurements"]["Current system positive power demand"])
    storeField("Maximum_system_positive_power_demand",       device["measurements"]["Maximum system positive power demand"])
    storeField("Current_system_reverse_power_demand",        device["measurements"]["Current system reverse power demand"])
    storeField("Maximum_system_reverse_power_demand",        device["measurements"]["Maximum system reverse power demand"])
    storeField("Current_demand",                             device["measurements"]["Current demand"])
    storeField("Maximum_current_demand",                     device["measurements"]["Maximum current demand"])
    storeField("Total_active_energy",                        device["measurements"]["Total active energy"])
    storeField("Total_reactive_energy",                      device["measurements"]["Total reactive energy"])
    storeField("Current_resettable_total_active_energy",     device["measurements"]["Current resettable total active energy"])
    storeField("Current_resettable_total_reactive_energy",   device["measurements"]["Current resettable total reactive energy"])
    storeField("Serial_number",                              device["digital_meter_set_up"]["Serial number"])

    // Make sure we are going to fetch all the indicated fields.
    allFields.forEach { (_, field) -> field.need() }

    println("Starting read loop")

    val timer = Timer("Fetcher")
    val timerTask: AliveTimerTask =
        object : AliveTimerTask() {
            override fun run() {
                if (!isAlive) {
                    println("Doing nothing because it should not be running anymore.")
                }
                try {
                    // Update all fields
                    device.update()


                    val table = sender
                        .table(databaseTableName)
//                        .symbol("device_type", deviceTypes[random.nextInt(deviceTypes.length)])
//                        .longColumn("duration_ms", random.nextInt(4000))
//                        .doubleColumn("lat", random.nextDouble() * (max_lat - min_lat))
//                        .doubleColumn("lon", random.nextDouble() * (max_lon - min_lon))
//                        .longColumn("measure1", random.nextInt(Integer.MAX_VALUE))
//                        .longColumn("measure2", random.nextInt(Integer.MAX_VALUE))
//                        .longColumn("speed", random.nextInt(100))
//                        .atNow();
                    allFields.forEach {
                            (label, field) ->
                        when(field.returnType) {
                            DOUBLE ->        field.doubleValue                ?.let { value -> table.doubleColumn(label, value) }
                            LONG ->          field.longValue                  ?.let { value -> table.longColumn(label, value) }
                            STRING ->        field.stringValue                ?.let { value -> table.stringColumn(label, value) }
                            STRINGLIST ->    field.stringListValue?.toString()?.let { value -> table.stringColumn(label, value) }
                            BOOLEAN ->       field.booleanValue               ?.let { value -> table.boolColumn(label, value) }
                            UNKNOWN -> TODO()
                        }
                    }

                    table.atNow()
                    sender.flush()
                } catch (e: TimeoutException) {
                    System.err.println("Got a TimeoutException (ignoring): $e")
                } catch (e: Exception) {
                    System.err.println("Stopping because of exception: $e")
                    cancel()
                }
            }
        }

    timer.scheduleAtFixedRate(timerTask, 0L, 1000L)

    while (timerTask.isAlive) {
//        println("Still alive")
        Thread.sleep(1000) // Check every second
    }
    println("Stopping")
    timer.cancel()
    exitProcess(0)
}

abstract class AliveTimerTask : TimerTask() {
    var isAlive: Boolean = true

    override fun cancel(): Boolean {
        this.isAlive = false
        return super.cancel()
    }
}
