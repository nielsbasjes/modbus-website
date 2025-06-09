<#import "expression.ftl" as expr>
//
// Generated using the nl.basjes.modbus:modbus-schema-maven-plugin:${pluginVersion}
// Using the CUSTOM template to generate Custom code.
// https://modbus.basjes.nl
//

// ===========================================================
//               !!! THIS IS GENERATED CODE !!!
// -----------------------------------------------------------
//       EVERY TIME THE SOFTWARE IS BUILD THIS FILE IS
//        REGENERATED AND ALL MANUAL CHANGES ARE LOST
// ===========================================================
PACKAGE: ${packageName};
CLASS: ${asClassName(className)}

<#list schemaDevice.blocks as block>
// ==========================================
BLOCK: ${asClassName(block.id)} -> id = "${block.id}"<#if block.description??> ; description = "${block.description}"</#if>
// FIELDS
<#list block.fields as field>
    // ==========================================
    FIELD: ${asClassName(field.id)} ; id = "${field.id}"; description = "${field.description}"; unit = "${field.unit}"; immutable = ${field.immutable?string('true', 'false')}; system = ${field.system?string('true', 'false')}; fetchGroup = "${field.fetchGroup}"
           GETTER     = field.get${asClassName(valueGetter(field.returnType))}();
           EXPRESSION = ${field.parsedExpression}
           REWRITE    = <@expr.expression expr=field.parsedExpression/>
</#list>
</#list>
