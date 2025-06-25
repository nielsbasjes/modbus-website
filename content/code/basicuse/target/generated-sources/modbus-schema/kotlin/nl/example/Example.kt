//
// Generated using the nl.basjes.modbus:modbus-schema-maven-plugin:0.10.0
// Using the builtin template to generate Kotlin MAIN code.
// https://modbus.basjes.nl
//

// ===========================================================
//               !!! THIS IS GENERATED CODE !!!
// -----------------------------------------------------------
//       EVERY TIME THE SOFTWARE IS BUILD THIS FILE IS
//        REGENERATED AND ALL MANUAL CHANGES ARE LOST
// ===========================================================
package nl.example

import nl.basjes.modbus.device.api.ModbusDevice
import nl.basjes.modbus.device.exception.ModbusException
import nl.basjes.modbus.schema.Field
import nl.basjes.modbus.schema.Block
import nl.basjes.modbus.schema.SchemaDevice
import nl.basjes.modbus.schema.toSchemaDevice
import nl.basjes.modbus.schema.utils.StringTable

/**
 * Demo based on a SunSpec device schema
 */
open class Example {

    val schemaDevice = SchemaDevice()

    val tests = schemaDevice.tests

    fun connectBase(modbusDevice: ModbusDevice): Example {
        schemaDevice.connectBase(modbusDevice)
        return this
    }

    fun connect(modbusDevice: ModbusDevice): Example {
        schemaDevice.connect(modbusDevice)
        return this
    }

    /**
     * Update all registers related to the needed fields to be updated with a maximum age of the provided milliseconds
     * @param maxAge maximum age of the fields in milliseconds
     * @return A list of all modbus queries that have been done (with duration and status)
     */
    @JvmOverloads
    fun update(maxAge: Long = 0) = schemaDevice.update(maxAge)

    /**
     * Update all registers related to the specified field
     * @param field the Field that must be updated
     * @return A list of all modbus queries that have been done (with duration and status)
     */
    fun update(field: Field) = schemaDevice.update(field)

    /**
     * Make sure all registers mentioned in all known fields are retrieved.
     * @return A (possibly empty) list of all modbus queries that have been done (with duration and status)
     */
    @JvmOverloads
    fun updateAll(maxAge: Long = 0) = schemaDevice.updateAll(maxAge)

    /**
     * @param field The field that must be kept up-to-date
     */
    fun need(field: Field) = schemaDevice.need(field)

    /**
     * @param field The field that no longer needs to be kept up-to-date
     */
    fun unNeed(field: Field) = schemaDevice.unNeed(field)

    /**
     * We want all fields to be kept up-to-date
     */
    fun needAll()  = schemaDevice.needAll()

    /**
     * We no longer want all fields to be kept up-to-date
     */
    fun unNeedAll()  = schemaDevice.unNeedAll()

    abstract class DeviceField(val field: Field) {
        /**
         * Retrieve the value of this field using the currently available device data.
         */
        abstract val value: Any?
        /**
         * We want this field to be kept up-to-date
         */
        fun need() = field.need()
        /**
         * We no longer want this field to be kept up-to-date
         */
        fun unNeed() = field.unNeed()
        /**
         * Directly update this field
         * @return A list of all modbus queries that have been done (with duration and status)
         */
        fun update() = field.update();
        /**
         * The unit of the returns value
         */
        val unit =  field.unit
        /**
         * The description of the Field
         */
        val description = field.description
        override fun toString(): String = if (value == null) { "null" } else { value.toString() }
    }

    // ==========================================
    /**
     * The header that starts the SunSpec model list
     */
    val sunSpecHeader = SunSpecHeader(schemaDevice);

    class SunSpecHeader(schemaDevice: SchemaDevice) {
        val block: Block;

        /**
         * Directly update all fields in this Block
         * @return A list of all modbus queries that have been done (with duration and status)
         */
        fun update() = block.update()

        /**
         * All fields in this Block must be kept up-to-date
         */
        fun need() = block.needAll()

        /**
         * All fields in this Block no longer need to be kept up-to-date
         */
        fun unNeed() = block.unNeedAll()


        // ==========================================
        /**
         * The SunS header
         */
        private val sunS: SunS
        private class SunS(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("SunS")
                 .description("The SunS header")
                 .expression("utf8(hr:40000 # 2)")
                 .unit("")
                 .immutable(true)
                 .system(true)
                 .fetchGroup("<<SunSpecHeader | SunS>>")
                 .build()) {
            override val value get() = field.stringValue
        }

        init {
            this.block = Block.builder()
              .schemaDevice(schemaDevice)
              .id("SunSpecHeader")
              .description("The header that starts the SunSpec model list")
              .build()

            this.sunS = SunS(block);
        }

        override fun toString(): String {
            val table = StringTable()
            table.withHeaders("Block", "Field", "Value");
            toStringTable(table)
            return table.toString()
        }

        internal fun toStringTable(table: StringTable) {
            // This block has no fields
        }
    }
    // ==========================================
    /**
     * Common: All SunSpec compliant devices must include this as the first model
     */
    val model1 = Model1(schemaDevice);

    class Model1(schemaDevice: SchemaDevice) {
        val block: Block;

        /**
         * Directly update all fields in this Block
         * @return A list of all modbus queries that have been done (with duration and status)
         */
        fun update() = block.update()

        /**
         * All fields in this Block must be kept up-to-date
         */
        fun need() = block.needAll()

        /**
         * All fields in this Block no longer need to be kept up-to-date
         */
        fun unNeed() = block.unNeedAll()


        // ==========================================
        /**
         * Model identifier.
         */
        private val iD: ID
        private class ID(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("ID")
                 .description("Model identifier.")
                 .expression("uint16(hr:40002 ; 0xFFFF ; 0x8000)")
                 .unit("")
                 .immutable(true)
                 .system(true)
                 .fetchGroup("<<Model 1 | ID>>")
                 .build()) {
            override val value get() = field.longValue
        }

        // ==========================================
        /**
         * Model length.
         */
        private val l: L
        private class L(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("L")
                 .description("Model length.")
                 .expression("uint16(hr:40003 ; 0xFFFF ; 0x8000)")
                 .unit("")
                 .immutable(true)
                 .system(true)
                 .fetchGroup("<<Model 1 | L>>")
                 .build()) {
            override val value get() = field.longValue
        }

        // ==========================================
        /**
         * Well known value registered with SunSpec for compliance.
         */
        public val mn: Mn
        public class Mn(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("Mn")
                 .description("Well known value registered with SunSpec for compliance.")
                 .expression("utf8(hr:40004 # 16)")
                 .unit("")
                 .immutable(true)
                 .system(false)
                 .fetchGroup("<<Model 1 | Mn>>")
                 .build()) {
            override val value get() = field.stringValue
        }

        // ==========================================
        /**
         * Manufacturer specific value (32 chars).
         */
        public val md: Md
        public class Md(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("Md")
                 .description("Manufacturer specific value (32 chars).")
                 .expression("utf8(hr:40020 # 16)")
                 .unit("")
                 .immutable(true)
                 .system(false)
                 .fetchGroup("<<Model 1 | Md>>")
                 .build()) {
            override val value get() = field.stringValue
        }

        // ==========================================
        /**
         * Manufacturer specific value (16 chars).
         */
        public val opt: Opt
        public class Opt(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("Opt")
                 .description("Manufacturer specific value (16 chars).")
                 .expression("utf8(hr:40036 # 8)")
                 .unit("")
                 .immutable(true)
                 .system(false)
                 .fetchGroup("<<Model 1 | Opt>>")
                 .build()) {
            override val value get() = field.stringValue
        }

        // ==========================================
        /**
         * Manufacturer specific value (16 chars).
         */
        public val vr: Vr
        public class Vr(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("Vr")
                 .description("Manufacturer specific value (16 chars).")
                 .expression("utf8(hr:40044 # 8)")
                 .unit("")
                 .immutable(true)
                 .system(false)
                 .fetchGroup("<<Model 1 | Vr>>")
                 .build()) {
            override val value get() = field.stringValue
        }

        // ==========================================
        /**
         * Manufacturer specific value (32 chars).
         */
        public val sN: SN
        public class SN(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("SN")
                 .description("Manufacturer specific value (32 chars).")
                 .expression("utf8(hr:40052 # 16)")
                 .unit("")
                 .immutable(true)
                 .system(false)
                 .fetchGroup("<<Model 1 | SN>>")
                 .build()) {
            override val value get() = field.stringValue
        }

        // ==========================================
        /**
         * Modbus device address.
         */
        public val dA: DA
        public class DA(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("DA")
                 .description("Modbus device address.")
                 .expression("uint16(hr:40068 ; 0xFFFF ; 0x8000)")
                 .unit("")
                 .immutable(false)
                 .system(false)
                 .fetchGroup("<<Model 1 | DA>>")
                 .build()) {
            override val value get() = field.longValue
        }

        // ==========================================
        /**
         * Force even alignment.
         */
        private val pad: Pad
        private class Pad(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("Pad")
                 .description("Force even alignment.")
                 .expression("int16(hr:40069 ; 0x8000)")
                 .unit("")
                 .immutable(true)
                 .system(true)
                 .fetchGroup("<<Model 1 | Pad>>")
                 .build()) {
            override val value get() = field.longValue
        }

        init {
            this.block = Block.builder()
              .schemaDevice(schemaDevice)
              .id("Model 1")
              .description("Common: All SunSpec compliant devices must include this as the first model")
              .build()

            this.iD  = ID(block);
            this.l   = L(block);
            this.mn  = Mn(block);
            this.md  = Md(block);
            this.opt = Opt(block);
            this.vr  = Vr(block);
            this.sN  = SN(block);
            this.dA  = DA(block);
            this.pad = Pad(block);
        }

        override fun toString(): String {
            val table = StringTable()
            table.withHeaders("Block", "Field", "Value");
            toStringTable(table)
            return table.toString()
        }

        internal fun toStringTable(table: StringTable) {
            table
                .addRow("Model 1", "Mn",  "" + mn.value)
                .addRow("Model 1", "Md",  "" + md.value)
                .addRow("Model 1", "Opt", "" + opt.value)
                .addRow("Model 1", "Vr",  "" + vr.value)
                .addRow("Model 1", "SN",  "" + sN.value)
                .addRow("Model 1", "DA",  "" + dA.value)
        }
    }

    override fun toString(): String {
        val table = StringTable();
        table.withHeaders("Block", "Field", "Value")
        sunSpecHeader .toStringTable(table)
        model1        .toStringTable(table)
        return table.toString()
    }

    init {
        require(schemaDevice.initialize()) { "Unable to initialize schema device" }
    }

}
