//
// Generated using the nl.basjes.modbus:modbus-schema-maven-plugin:0.7.0
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
 * Testing the Minimal class for: A very simple demo schema
 */
internal class TestMinimal {

    @Test
    fun ensureValidSchema() {
        val schemaDevice = Minimal().schemaDevice
        val results = schemaDevice.verifyProvidedTests()
        assertTrue(results.logResults(), "Unable to verify all tests defined in the schema definition" )
    }

    // ==========================================
    @Test
    // Just to demo the test capability ()
    fun verifyProvidedTest_JustToDemoTheTestCapability() {
        val modbusDevice = MockedModbusDevice.builder().build()
        val minimal = Minimal().connect(modbusDevice)
        modbusDevice.addRegisters(Address.of("hr:00000"), """
            4E69 656C 7320 4261 736A 6573 0000 0000 0000 0000
            0000 0000
            """.trimIndent());
        minimal.updateAll()
        assertEquals("Niels Basjes", minimal.block1.name.value)
    }
}
