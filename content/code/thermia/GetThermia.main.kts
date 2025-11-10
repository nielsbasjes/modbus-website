#!/usr/bin/env -S kotlin -howtorun .main.kts 

@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:2.2.0")
@file:DependsOn("nl.basjes.modbus:modbus-api-j2mod:0.14.0")
@file:DependsOn("nl.basjes.modbus:modbus-schema-device:0.14.0")
@file:DependsOn("nl.basjes.modbus.devices:modbus-device-thermia-genesis:0.6.2")
@file:DependsOn("org.json:json:20250517")
@file:DependsOn("de.kempmobil.ktor.mqtt:mqtt-core-jvm:0.6.2")
@file:DependsOn("de.kempmobil.ktor.mqtt:mqtt-client-jvm:0.6.2")
@file:DependsOn("org.apache.logging.log4j:log4j-to-slf4j:2.24.3")
@file:DependsOn("org.slf4j:slf4j-simple:2.0.17")

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import nl.basjes.modbus.thermia.ThermiaGenesis
import de.kempmobil.ktor.mqtt.MqttClient
import de.kempmobil.ktor.mqtt.PublishRequest
import de.kempmobil.ktor.mqtt.QoS
import de.kempmobil.ktor.mqtt.TimeoutException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import nl.basjes.modbus.device.j2mod.ModbusDeviceJ2Mod
import nl.basjes.modbus.schema.Field
import nl.basjes.modbus.schema.ReturnType.BOOLEAN
import nl.basjes.modbus.schema.ReturnType.DOUBLE
import nl.basjes.modbus.schema.ReturnType.LONG
import nl.basjes.modbus.schema.ReturnType.STRING
import nl.basjes.modbus.schema.ReturnType.STRINGLIST
import nl.basjes.modbus.schema.ReturnType.UNKNOWN
import nl.basjes.modbus.schema.SchemaDevice
import nl.basjes.modbus.schema.toTable
import org.json.JSONObject
import java.time.Instant
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.hours

val modbusHost      = "localhost" // "thermia.iot.basjes.nl"
val modbusPort      = 1502 // MODBUS_STANDARD_TCP_PORT
val modbusUnit      = 1

val mqttBrokerHost  :String? = "localhost"
val mqttBrokerPort  = 1883
val mqttTopic       :String? = "heat/thermia"

//val mqttUser            :String? = null TODO: If you need it you must change the code a bit.
//val mqttPassword        :String? = null TODO: If you need it you must change the code a bit.

// This is useful if you want to set a different hostname (shown in the HA Gauge label and such)
val homeAssistantDeviceName: String? = null

fun allTheFieldsIWant(thermia: ThermiaGenesis): List<Field> {
    // Use these fields as Measurements
    val allFields = mutableListOf<Field>()

    // The fields I want
    allFields.add(thermia.settings.comfortWheelSetting.field)
    allFields.add(thermia.sensors.roomTemperature.field)
    allFields.add(thermia.sensors.outdoorTemperature.field)
    allFields.add(thermia.sensors.tapWaterWeightedTemperature.field)
    allFields.add(thermia.sensors.tapWaterTopTemperature.field)
    allFields.add(thermia.sensors.tapWaterLowerTemperature.field)
    allFields.add(thermia.sensors.brineInTemperature.field)
    allFields.add(thermia.sensors.brineOutTemperature.field)
    allFields.add(thermia.sensors.currentlyRunning1stDemand.field)
    allFields.add(thermia.sensors.currentlyRunning2ndDemand.field)
    allFields.add(thermia.sensors.currentlyRunning3rdDemand.field)

    return allFields
}

// ===================================================================================================

print("Modbus: Connecting...")
val modbusMaster = ModbusTCPMaster(modbusHost, modbusPort)
modbusMaster.connect()
ModbusDeviceJ2Mod(modbusMaster, modbusUnit). use { modbusDevice ->
    println(" done")

    val thermia = ThermiaGenesis()
    thermia.connect(modbusDevice)

    // If no broker is specified the output is sent to the console (useful for testing)
    if (mqttBrokerHost == null || mqttTopic == null) {
        println("No database, outputting to console")
        runLoop(thermia, null, "console")
        return
    }

    // If we do have the broker and topic the data is sent to the MQTT broker
    println("Connecting to mqtt $mqttBrokerHost:$mqttBrokerPort")
    // TODO: Username + password ...
    val mqttClient = MqttClient(mqttBrokerHost, mqttBrokerPort) {}
    runBlocking {
        mqttClient.connect().onFailure { throw IOException("Connection failed: $it") }
    }
    runLoop(thermia, mqttClient, mqttTopic)
    runBlocking {
        mqttClient.disconnect()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun runLoop(device: ThermiaGenesis, mqttClient: MqttClient?, mqttTopic: String) {

    // Use these fields as Measurements
    val allFields = allTheFieldsIWant(device)

    // We need all the field we want.
    allFields.forEach { it.need() }

    println("Trying to get ${allFields.size} fields.")
    device.update()
    println("Found ${allFields.filter{ it.value != null }.size} fields to have a value.")
    println(device.schemaDevice.toTable(onlyUseFullFields = true))

    // ----------------------------------------------------------------------------------------

    // OPTIONAL: Generate the config for Home Assistant
    generateHomeAssistantConfig(device, allFields)

    // ----------------------------------------------------------------------------------------

    val interval = 1000L

    println("Starting read loop")

    while (true) {
        try {
            runBlocking {
                // Wait until the current time is a multiple of the configured interval
                val now = Instant.now().toEpochMilli()
                val sleepTime = (((now / interval) + 1) * interval) - now
                if (sleepTime > 0) delay(sleepTime)
            }
            // Update all fields
            val startUpdate = Instant.now()
            print("Doing update at: $startUpdate .. ")
            device.update()
            val finishUpdate = Instant.now()
            println("done in ${finishUpdate.toEpochMilli() -  startUpdate.toEpochMilli()} milliseconds.")

            val result = JSONObject()

            // We are rounding the timestamp to seconds to make the graphs in influxdb work a bit better
            val now = Instant.now()
            result.put("timestamp", now.toEpochMilli())
            result.put("timestampString", now)

            allFields.forEach {
                val jsonFieldName = it.jsonFieldName()
                when(it.returnType) {
                    DOUBLE     -> result.put(jsonFieldName, it.doubleValue     ?: 0.0)
                    LONG       -> result.put(jsonFieldName, it.longValue       ?: 0)
                    STRING     -> result.put(jsonFieldName, it.stringValue     ?: "")
                    STRINGLIST -> result.put(jsonFieldName, it.stringListValue ?: listOf<String>())
                    BOOLEAN    -> result.put(jsonFieldName, it.booleanValue    ?: "")
                    UNKNOWN    -> TODO()
                }
            }

            if (mqttClient == null) {
                println(result)
            } else {
                GlobalScope.launch {
                    print("Writing to MQTT: $now .. ")
                    mqttClient
                        .publish(PublishRequest(mqttTopic) {
                            desiredQoS = QoS.AT_LEAST_ONCE
                            messageExpiryInterval = 12.hours
                            payload(result.toString())
                        })
                        .onSuccess { println("COMPLETED") }
                        .onFailure { println("FAILED with $it") }
                }
            }

        } catch (e: TimeoutException) {
            System.err.println("Got a TimeoutException from MQTT (ignoring 1): $e --> ${e.message} ==> ${e.printStackTrace()}")
        } catch (e: java.util.concurrent.TimeoutException) {
            System.err.println("Got a java.util.concurrent.TimeoutException (ignoring 2): $e --> ${e.message} ==> ${e.printStackTrace()}")
        } catch (e: Exception) {
            System.err.println("Got an exception: $e --> ${e.message} ==> ${e.printStackTrace()}")
            println("Stopping")
            return
        }
    }
}

fun Field.jsonFieldName() = "${this.block.id} ${this.id}".replace(Regex("[^a-zA-Z0-9_]"), "_")

fun showAllFieldsWithUsableValues(schemaDevice: SchemaDevice) {
    schemaDevice.updateAll()
    println("All possible fields that provide a useful value:\n${schemaDevice.toTable(true)}")
    exitProcess(0)
}

fun generateHomeAssistantConfig(
    thermia: ThermiaGenesis,
    allFields: List<Field>,
) {
    // Generate the config for Home Assistant
    // We first fetch all fields that have been asked for
    // and then for all fields that actually have a value and are not marked as "system"
    // a config for Home Assistant is generated (must be copied to the Home Assistant setup manually)

    // These are always needed
    val manufacturer = "Thermia"
    val model        = "Calibra Cool 7"
    val softwareVersion      = thermia.sensors.softwareVersion.field
    val controlSoftwareVersion = thermia.sensors.controlSoftwareVersion.field

    val configFields = mutableListOf<Field>()
    configFields.add(softwareVersion)
    configFields.add(controlSoftwareVersion)
    configFields.addAll(allFields)
    configFields.distinct()

    configFields.forEach { it.need() }
    thermia.update(1000)
    configFields.forEach { it.unNeed() }

    val systemId = "$manufacturer-$model-home" // I do not have a serial number or something like that ... so 'home'

    println("""
# ----------------------------------------------------------------------------------------
# HomeAssistant definitions for all requested fields
mqtt:
  sensor:
    """.trimIndent())
    allFields
        .forEach {
            if (!it.isSystem && it.value != null) {
                val jsonFieldName = it.jsonFieldName()
                // Building a name that looks 'ok' in Home Assistant
                val name = it.block.id + " - " + it.description

                println(
                    """
    - name: "$name"
      unique_id: "$systemId-$jsonFieldName"
      state_topic: "$mqttTopic"
      value_template: "{{ value_json.$jsonFieldName ${if (it.returnType == DOUBLE) "| round(4, default=0)" else "" }}}"${if(it.unit.isNotBlank()) """
      unit_of_measurement: "${it.unit}"""" else ""}
      icon: mdi:solar-panel
      device:
        name: "${if(homeAssistantDeviceName.isNullOrBlank()) "$manufacturer $model" else homeAssistantDeviceName}"
        manufacturer: "$manufacturer"
        model: "$model"
        identifiers: "${softwareVersion.stringValue}"
        sw_version: "${controlSoftwareVersion.stringValue}"
      """
                )
            }
        }
    println("""
# ----------------------------------------------------------------------------------------
""")

//    exitProcess(0)
}
