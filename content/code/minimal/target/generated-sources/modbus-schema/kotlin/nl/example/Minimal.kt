//
// Generated using the nl.basjes.modbus:modbus-schema-maven-plugin:0.14.0
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
 * A very simple demo schema
 */
open class Minimal {

    val schemaDevice = SchemaDevice()

    val tests = schemaDevice.tests

    fun connectBase(modbusDevice: ModbusDevice): Minimal {
        schemaDevice.connectBase(modbusDevice)
        return this
    }

    fun connect(modbusDevice: ModbusDevice): Minimal {
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
     * The first block
     */
    val block1 = Block1(schemaDevice);

    class Block1(schemaDevice: SchemaDevice) {
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
         * The name Field
         */
        public val name: Name
        public class Name(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("Name")
                 .description("The name Field")
                 .expression("utf8(hr:00000 # 12)")
                 .unit("")
                 .immutable(false)
                 .system(false)
                 .fetchGroup("<<Block 1 | Name>>")
                 .build()) {
            override val value get() = field.stringValue
        }

        // ==========================================
        /**
         * The flag Field
         */
        public val flag: Flag
        public class Flag(block: Block): DeviceField (
            Field.builder()
                 .block(block)
                 .id("Flag")
                 .description("The flag Field")
                 .expression("boolean(c:00000)")
                 .unit("")
                 .immutable(false)
                 .system(false)
                 .fetchGroup("<<Block 1 | Flag>>")
                 .build()) {
            override val value get() = field.booleanValue
        }

        init {
            this.block = Block.builder()
              .schemaDevice(schemaDevice)
              .id("Block 1")
              .description("The first block")
              .build()

            this.name = Name(block);
            this.flag = Flag(block);
        }

        override fun toString(): String {
            val table = StringTable()
            table.withHeaders("Block", "Field", "Value");
            toStringTable(table)
            return table.toString()
        }

        internal fun toStringTable(table: StringTable) {
            table
                .addRow("Block 1", "Name", "" + name.value)
                .addRow("Block 1", "Flag", "" + flag.value)
        }
    }

    override fun toString(): String {
        val table = StringTable();
        table.withHeaders("Block", "Field", "Value")
        block1  .toStringTable(table)
        return table.toString()
    }

    init {
        require(schemaDevice.initialize()) { "Unable to initialize schema device" }
    }

}
