//
// Generated using the nl.basjes.modbus:modbus-schema-maven-plugin:0.6.0
// Using the builtin template to generate Kotlin TEST code.
// https://modbus.basjes.nl
//

// ===========================================================
//               !!! THIS IS GENERATED CODE !!!
// -----------------------------------------------------------
//       EVERY TIME THE SOFTWARE IS BUILD THIS FILE IS
//        REGENERATED AND ALL MANUAL CHANGES ARE LOST
// ===========================================================
package nl.example

import nl.basjes.modbus.device.api.Address
import nl.basjes.modbus.device.memory.MockedModbusDevice
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.Test

/**
 * Testing the Example class for: Demo based on a SunSpec device schema
 */
internal class TestExample {

    @Test
    fun ensureValidSchema() {
        val schemaDevice = Example().schemaDevice
        val results = schemaDevice.verifyProvidedTests()
        assertTrue(results.logResults(), "Unable to verify all tests defined in the schema definition" )
    }

    // ==========================================
    @Test
    // A demonstration test scenario (Test generated from device data at 2025-05-29T14:15:22.906Z)
    fun verifyProvidedTest_ADemonstrationTestScenario() {
        val modbusDevice = MockedModbusDevice.builder().build()
        val example = Example().connect(modbusDevice)
        modbusDevice.addRegisters(Address.of("hr:40000"), """
            5375 6E53 0001 0042 534D 4100 0000 0000 0000 0000
            0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
            5342 332E 362D 3141 562D 3431 0000 0000 0000 0000
            0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
            0000 0000 0000 0000 342E 3031 2E31 352E 5200 0000
            0000 0000 3330 3035 3036 3734 3135 0000 0000 0000
            0000 0000 0000 0000 0000 0000 0000 0000 FFFF 8000
            """.trimIndent());
        example.updateAll()
        assertEquals("SMA", example.model1.mn.value)
        assertEquals("SB3.6-1AV-41", example.model1.md.value)
        assertEquals("", example.model1.opt.value)
        assertEquals("4.01.15.R", example.model1.vr.value)
        assertEquals("3005067415", example.model1.sN.value)
    }
}
