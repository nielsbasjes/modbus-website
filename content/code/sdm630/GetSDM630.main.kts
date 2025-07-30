#!/usr/bin/env kotlin

@file:DependsOn("nl.basjes.modbus:modbus-api-j2mod:0.14.0")
@file:DependsOn("nl.basjes.modbus:modbus-schema-device:0.14.0")
@file:DependsOn("nl.basjes.modbus.devices:modbus-device-sdm630:0.6.2")
@file:DependsOn("com.influxdb:influxdb-client-java:7.3.0")
@file:DependsOn("org.apache.logging.log4j:log4j-to-slf4j:2.24.3")
@file:DependsOn("org.slf4j:slf4j-simple:2.0.17")

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster
import com.ghgande.j2mod.modbus.util.SerialParameters
import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.WriteApiBlocking
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import nl.basjes.modbus.device.j2mod.ModbusDeviceJ2Mod
import nl.basjes.modbus.eastron.SDM630
import nl.basjes.modbus.schema.Field
import nl.basjes.modbus.schema.ReturnType.*
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.system.exitProcess

val serialParameters = SerialParameters()

serialParameters.portName       = "/dev/ttyUSB0"
serialParameters.baudRate       = 38400
serialParameters.flowControlIn  = 0
serialParameters.flowControlOut = 0
serialParameters.databits       = 8
serialParameters.stopbits       = 1
serialParameters.parity         = 0
serialParameters.openDelay      = 0
serialParameters.encoding       = "rtu"

val modbusUnit           :Int    = 1

val databaseUrl          :String? = null // "http://127.0.0.1:8086"
val databaseName         :String? = "energy" // "Some bucket"
val databaseMeasurement  :String? = "electricity"

print("Connecting...")
ModbusDeviceJ2Mod(ModbusSerialMaster(serialParameters), modbusUnit).use { modbusDevice ->
    println(" done")

    val sdm630 = SDM630()
    sdm630.connect(modbusDevice)

    if (
        databaseUrl         == null ||
        databaseName        == null ||
        databaseMeasurement == null
    ) {
        println("No database, outputting to console")
        runLoop(sdm630, null, "console")
        return
    } else {
        println("Connecting to database $databaseUrl")
        InfluxDBClientFactory
            .createV1(
                databaseUrl,
                null, // username
                null, // password
                databaseName,
                "autogen"
            ).use { influxDBClient ->
                if (!influxDBClient.ping()) {
                    System.err.println("Error pinging server.")
                    return
                }
                runLoop(sdm630, influxDBClient.writeApiBlocking, databaseMeasurement)
            }
    }
}

fun runLoop(sdm630: SDM630, writeApi: WriteApiBlocking?, databaseMeasurement: String) {
    // Use these fields as Measurements towards InfluxDB
    val allFields = mutableMapOf<String, Field>()

    allFields.put("SDM630_Phase_1_line_to_neutral_volts_Volts",             sdm630.measurements.phase1LineToNeutralVolts        .field)
    allFields.put("SDM630_Phase_2_line_to_neutral_volts_Volts",             sdm630.measurements.phase2LineToNeutralVolts        .field)
    allFields.put("SDM630_Phase_3_line_to_neutral_volts_Volts",             sdm630.measurements.phase3LineToNeutralVolts        .field)
    allFields.put("SDM630_Phase_1_current_Amps",                            sdm630.measurements.phase1Current                   .field)
    allFields.put("SDM630_Phase_2_current_Amps",                            sdm630.measurements.phase2Current                   .field)
    allFields.put("SDM630_Phase_3_current_Amps",                            sdm630.measurements.phase3Current                   .field)
    allFields.put("SDM630_Phase_1_power_Watts",                             sdm630.measurements.phase1Power                     .field)
    allFields.put("SDM630_Phase_2_power_Watts",                             sdm630.measurements.phase2Power                     .field)
    allFields.put("SDM630_Phase_3_power_Watts",                             sdm630.measurements.phase3Power                     .field)
    allFields.put("SDM630_Phase_1_volt_amps_VA",                            sdm630.measurements.phase1VoltAmps                  .field)
    allFields.put("SDM630_Phase_2_volt_amps_VA",                            sdm630.measurements.phase2VoltAmps                  .field)
    allFields.put("SDM630_Phase_3_volt_amps_VA",                            sdm630.measurements.phase3VoltAmps                  .field)
    allFields.put("SDM630_Phase_1_volt_amps_reactive_VAr",                  sdm630.measurements.phase1ReactivePower             .field)
    allFields.put("SDM630_Phase_2_volt_amps_reactive_VAr",                  sdm630.measurements.phase2ReactivePower             .field)
    allFields.put("SDM630_Phase_3_volt_amps_reactive_VAr",                  sdm630.measurements.phase3ReactivePower             .field)
    allFields.put("SDM630_Phase_1_power_factor__1_",                        sdm630.measurements.phase1PowerFactor               .field)
    allFields.put("SDM630_Phase_2_power_factor__1_",                        sdm630.measurements.phase2PowerFactor               .field)
    allFields.put("SDM630_Phase_3_power_factor__1_",                        sdm630.measurements.phase3PowerFactor               .field)
    allFields.put("SDM630_Phase_1_phase_angle_Degrees",                     sdm630.measurements.phase1PhaseAngle                .field)
    allFields.put("SDM630_Phase_2_phase_angle_Degrees",                     sdm630.measurements.phase2PhaseAngle                .field)
    allFields.put("SDM630_Phase_3_phase_angle_Degrees",                     sdm630.measurements.phase3PhaseAngle                .field)
    allFields.put("SDM630_Average_line_to_neutral_volts_Volts",             sdm630.measurements.averageLineToNeutralVolts       .field)
    allFields.put("SDM630_Average_line_current_Amps",                       sdm630.measurements.averageLineCurrent              .field)
    allFields.put("SDM630_Sum_of_line_currents_Amps",                       sdm630.measurements.sumOfLineCurrents               .field)
    allFields.put("SDM630_Total_system_power_Watts",                        sdm630.measurements.totalSystemPower                .field)
    allFields.put("SDM630_Total_system_volt_amps_VA",                       sdm630.measurements.totalSystemVoltAmps             .field)
    allFields.put("SDM630_Total_system_VAr_VAr",                            sdm630.measurements.totalSystemVAr                  .field)
    allFields.put("SDM630_Total_system_power_factor__1_",                   sdm630.measurements.totalSystemPowerFactor          .field)
    allFields.put("SDM630_Total_system_phase_angle_Degrees",                sdm630.measurements.totalSystemPhaseAngle           .field)
    allFields.put("SDM630_Frequency_of_supply_voltages_Hz",                 sdm630.measurements.frequencyOfSupplyVoltages       .field)
    allFields.put("SDM630_Import_Wh_since_last_reset_2__kWh_MWh",           sdm630.measurements.totalImportKWh                  .field)
    allFields.put("SDM630_Export_Wh_since_last_reset_2__kWH_MWh",           sdm630.measurements.totalExportKWh                  .field)
    allFields.put("SDM630_Import_VArh_since_last_reset_2__kVArh_MVArh",     sdm630.measurements.totalImportKVArh                .field)
    allFields.put("SDM630_Export_VArh_since_last_reset_2__kVArh_MVArh",     sdm630.measurements.totalExportKVArh                .field)
    allFields.put("SDM630_VAh_since_last_reset__2__kVAh_MVAh",              sdm630.measurements.totalVAh                        .field)
    allFields.put("SDM630_Ah_since_last_reset__3__Ah_kAh",                  sdm630.measurements.ah                              .field)
    allFields.put("SDM630_Total_system_power_demand__4__W",                 sdm630.measurements.totalSystemPowerDemand          .field)
    allFields.put("SDM630_Maximum_total_system_power_demand_4__VA",         sdm630.measurements.maximumTotalSystemPowerDemand   .field)
    allFields.put("SDM630_Total_system_VA_demand_VA",                       sdm630.measurements.totalSystemVADemand             .field)
    allFields.put("SDM630_Maximum_total_VA_system_demand_VA",               sdm630.measurements.maximumTotalSystemPowerDemand   .field)
    allFields.put("SDM630_Neutral_current_demand_Amps",                     sdm630.measurements.neutralCurrentDemand            .field)
    allFields.put("SDM630_Maximum_neutral_current_demand_Amps",             sdm630.measurements.maximumNeutralCurrentDemand     .field)
    allFields.put("SDM630_Line_1_to_Line_2_volts_Volts",                    sdm630.measurements.line1ToLine2Volts               .field)
    allFields.put("SDM630_Line_2_to_Line_3_volts_Volts",                    sdm630.measurements.line2ToLine3Volts               .field)
    allFields.put("SDM630_Line_3_to_Line_1_volts_Volts",                    sdm630.measurements.line3ToLine1Volts               .field)
    allFields.put("SDM630_Average_line_to_line_volts_Volts",                sdm630.measurements.averageLineToLineVolts          .field)
    allFields.put("SDM630_Neutral_current_Amps",                            sdm630.measurements.neutralCurrent                  .field)
    allFields.put("SDM630_Phase_1_L_N_volts_THD_Pct",                       sdm630.measurements.phase1LNVoltsTHD                .field)
    allFields.put("SDM630_Phase_2_L_N_volts_THD_Pct",                       sdm630.measurements.phase2LNVoltsTHD                .field)
    allFields.put("SDM630_Phase_3_L_N_volts_THD_Pct",                       sdm630.measurements.phase3LNVoltsTHD                .field)
    allFields.put("SDM630_Phase_1_Current_THD_Pct",                         sdm630.measurements.phase1CurrentTHD                .field)
    allFields.put("SDM630_Phase_2_Current_THD_Pct",                         sdm630.measurements.phase2CurrentTHD                .field)
    allFields.put("SDM630_Phase_3_Current_THD_Pct",                         sdm630.measurements.phase3CurrentTHD                .field)
    allFields.put("SDM630_Average_line_to_neutral_volts_THD_Pct",           sdm630.measurements.averageLineToNeutralVoltsTHD    .field)
    allFields.put("SDM630_Average_line_current_THD_Pct",                    sdm630.measurements.averageLineCurrentTHD           .field)
    allFields.put("SDM630_Total_system_power_factor__5__Degrees",           sdm630.measurements.totalSystemPowerFactor          .field)
    allFields.put("SDM630_Phase_1_current_demand_Amps",                     sdm630.measurements.phase1CurrentDemand             .field)
    allFields.put("SDM630_Phase_2_current_demand_Amps",                     sdm630.measurements.phase2CurrentDemand             .field)
    allFields.put("SDM630_Phase_3_current_demand_Amps",                     sdm630.measurements.phase3CurrentDemand             .field)
    allFields.put("SDM630_Maximum_phase_1_current_demand_Amps",             sdm630.measurements.maximumPhase1CurrentDemand      .field)
    allFields.put("SDM630_Maximum_phase_2_current_demand_Amps",             sdm630.measurements.maximumPhase2CurrentDemand      .field)
    allFields.put("SDM630_Maximum_phase_3_current_demand_Amps",             sdm630.measurements.maximumPhase3CurrentDemand      .field)
    allFields.put("SDM630_Line_1_to_line_2_volts_THD_Pct",                  sdm630.measurements.line1ToLine2VoltsTHD            .field)
    allFields.put("SDM630_Line_2_to_line_3_volts_THD_Pct",                  sdm630.measurements.line2ToLine3VoltsTHD            .field)
    allFields.put("SDM630_Line_3_to_line_1_volts_THD_Pct",                  sdm630.measurements.line3ToLine1VoltsTHD            .field)
    allFields.put("SDM630_Average_line_to_line_volts_THD_Pct",              sdm630.measurements.averageLineToLineVoltsTHD       .field)
    allFields.put("SDM630_Total_kWh_kWh",                                   sdm630.measurements.totalKWh                        .field)
    allFields.put("SDM630_Total_kVArh_kVArh",                               sdm630.measurements.totalKVArh                      .field)
    allFields.put("SDM630_L1_import_kWh_kWh",                               sdm630.measurements.l1ImportKWh                     .field)
    allFields.put("SDM630_L2_import_kWh_kWh",                               sdm630.measurements.l2ImportKWh                     .field)
    allFields.put("SDM630_L3_import_kWh_kWh",                               sdm630.measurements.l3ImportKWh                     .field)
    allFields.put("SDM630_L1_export_kWh_kWh",                               sdm630.measurements.l1ExportKWh                     .field)
    allFields.put("SDM630_L2_export_kWh_kWh",                               sdm630.measurements.l2ExportKWh                     .field)
    allFields.put("SDM630_L3_export_kWh_kWh",                               sdm630.measurements.l3ExportKWh                     .field)
    allFields.put("SDM630_L1_total_kWh_kWh",                                sdm630.measurements.l1TotalKWh                      .field)
    allFields.put("SDM630_L2_total_kWh_kWh",                                sdm630.measurements.l2TotalKWh                      .field)
    allFields.put("SDM630_L3_total_kWh_kWh",                                sdm630.measurements.l3TotalKWh                      .field)
    allFields.put("SDM630_L1_import_kVArh_kVArh",                           sdm630.measurements.l1ImportKVArh                   .field)
    allFields.put("SDM630_L2_import_kVArh_kVArh",                           sdm630.measurements.l2ImportKVArh                   .field)
    allFields.put("SDM630_L3_import_kVArh_kVArh",                           sdm630.measurements.l3ImportKVArh                   .field)
    allFields.put("SDM630_L1_export_kVArh_kVArh",                           sdm630.measurements.l1ExportKVArh                   .field)
    allFields.put("SDM630_L2_export_kVArh_kVArh",                           sdm630.measurements.l2ExportKVArh                   .field)
    allFields.put("SDM630_L3_export_kVArh_kVArh",                           sdm630.measurements.l3ExportKVArh                   .field)
    allFields.put("SDM630_L1_total_kVArh_kVArh",                            sdm630.measurements.l1TotalKVArh                    .field)
    allFields.put("SDM630_L2_total_kVArh_kVArh",                            sdm630.measurements.l2TotalKVArh                    .field)
    allFields.put("SDM630_L3_total_kVArh_kVArh",                            sdm630.measurements.l3TotalKVArh                    .field)

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
                    sdm630.update()
                    val point: Point =
                        Point
                            .measurement(databaseMeasurement)
                            // We are rounding the timestamp to seconds to make the graphs in influxdb work a bit better
                            .time(Instant.ofEpochSecond(Instant.now().epochSecond), WritePrecision.S)
                            .addTag("equipmentId", "SDM630")
                    allFields.forEach {
                            (label, field) ->
                        when(field.returnType) {
                            DOUBLE ->        field.doubleValue                ?.let { value -> point.addField(label, value) }
                            LONG ->          field.longValue                  ?.let { value -> point.addField(label, value) }
                            STRING ->        field.stringValue                ?.let { value -> point.addField(label, value) }
                            STRINGLIST ->    field.stringListValue?.toString()?.let { value -> point.addField(label, value) }
                            BOOLEAN ->       field.booleanValue               ?.let { value -> point.addField(label, value) }
                            UNKNOWN -> TODO()
                        }
                    }

                    if (writeApi == null) {
                        println(point.toLineProtocol())
                    } else {
                        println("Writing to influxDB: ${point.time}")
                        writeApi.writePoint(point)
                    }
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
