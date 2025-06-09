#!/usr/bin/env kotlin

@file:DependsOn("nl.basjes.modbus:modbus-api-plc4j:0.7.0")
@file:DependsOn("nl.basjes.modbus:modbus-schema-device:0.7.0")
@file:DependsOn("nl.basjes.modbus.devices:modbus-device-thermia-genesis:0.3.0")

import nl.basjes.modbus.device.api.MODBUS_STANDARD_TCP_PORT
import nl.basjes.modbus.device.plc4j.ModbusDevicePlc4j
import nl.basjes.modbus.thermia.ThermiaGenesis

val hostname = "thermia.iot.basjes.nl"
val port = MODBUS_STANDARD_TCP_PORT
val unitId = 1

val connectionString =
    "modbus-tcp:tcp://$hostname:$port?unit-identifier=$unitId"

print("Connecting...")
ModbusDevicePlc4j(connectionString).use { modbusDevice ->
    println(" done")

    val thermia = ThermiaGenesis()
    thermia.connect(modbusDevice)

    val outdoorTemp     = thermia.inputRegisters.outdoorTemperature
    val roomTemp        = thermia.inputRegisters.roomTemperature
    val roomTempSetting = thermia.holdingRegisters.comfortWheelSetting
    val tapWaterTemp    = thermia.inputRegisters.tapWaterWeightedTemperature

    outdoorTemp     .need()
    roomTemp        .need()
    tapWaterTemp    .need()
    roomTempSetting .need()

    thermia.update(1000)

    println("Outdoor Temperature            : ${outdoorTemp    .value} ${outdoorTemp    .unit} ")
    println("Room Temperature               : ${roomTemp       .value} ${roomTemp       .unit} ")
    println("Comfort Wheel Setting          : ${roomTempSetting.value} ${roomTempSetting.unit} ")
    println("Tap Water Weighted Temperature : ${tapWaterTemp   .value} ${tapWaterTemp   .unit} ")
}
