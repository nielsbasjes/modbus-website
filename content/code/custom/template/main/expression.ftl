<#macro notImplemented expr><#compress>
  <#if expr.notImplemented??>
    <#if expr.notImplemented?has_content>
    |  NOT IMPLEMENTED:[ <#list expr.notImplemented as notImplementedHexList>[<@hexValues hexStrings=notImplementedHexList/>]<#sep>, </#sep></#list> ]
    </#if>
  </#if>
</#compress></#macro>

<#macro hexValues hexStrings><#compress>
    [ <#list hexStrings as hexString>0x${hexString}<#sep>, </#sep></#list> ]
</#compress></#macro>

<#macro expression expr><#compress>
<#if !expr?has_content>
NULL EXPRESSION
<#else>
<#if isExpressionType(expr, "ExpressionRegistersConstant")>   RegistersConstant( |PER REGISTER| <@hexValues hexStrings=expr.asRegisterHexStrings/> |OR PER BYTE| <@hexValues hexStrings=expr.asByteHexStrings/>                                     )</#if>
<#if isExpressionType(expr, "ExpressionGetModbus")>           GetModbus(         <#list expr.requiredRegisters as address>${address.toModiconX()}<#sep>, </#sep></#list>  )</#if>
<#if isExpressionType(expr, "ExpressionSwapBytes")>           SwapBytes(         <#list expr.requiredRegisters as address>${address.toModiconX()}<#sep>, </#sep></#list>  )</#if>
<#if isExpressionType(expr, "ExpressionSwapEndian")>          SwapEndian(        <#list expr.requiredRegisters as address>${address.toModiconX()}<#sep>, </#sep></#list>  )</#if>
<#if isExpressionType(expr, "ExpressionLongConstant")>        LongConstant(      ${expr.value?c}                                                                          )</#if>
<#if isExpressionType(expr, "ExpressionDoubleConstant")>      DoubleConstant(    ${expr.value?c}                                                                          )</#if>
<#if isExpressionType(expr, "ExpressionNumericalField")>      NumericalField(    ${expr.fieldName}                                                                        )</#if>
<#if isExpressionType(expr, "ExpressionAdd")>                 Add(               <@expression expr=expr.left/>     , <@expression expr=expr.right/>                       )</#if>
<#if isExpressionType(expr, "ExpressionSubtract")>            Subtract(          <@expression expr=expr.left/>     , <@expression expr=expr.right/>                       )</#if>
<#if isExpressionType(expr, "ExpressionMultiply")>            Multiply(          <@expression expr=expr.left/>     , <@expression expr=expr.right/>                       )</#if>
<#if isExpressionType(expr, "ExpressionDivide")>              Divide(            <@expression expr=expr.dividend/> , <@expression expr=expr.divisor/>                     )</#if>
<#if isExpressionType(expr, "ExpressionPower")>               Power(             <@expression expr=expr.base/>     , <@expression expr=expr.exponent/>                    )</#if>
<#if isExpressionType(expr, "ExpressionIEEE754Float32")>      IEEE754Float32(    <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIEEE754Float64")>      IEEE754Float64(    <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIntegerSigned16")>     IntegerSigned16(   <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIntegerSigned32")>     IntegerSigned32(   <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIntegerSigned64")>     IntegerSigned64(   <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIntegerUnsigned16")>   IntegerUnsigned16( <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIntegerUnsigned32")>   IntegerUnsigned32( <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIntegerUnsigned64")>   IntegerUnsigned64( <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionBitsetStringList")>    BitsetStringList(  <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list> BIT MAPPED TO <#list expr.mappings as bitNr, result>Bit   ${bitNr} -> ${result}<#sep> | </#sep></#list> <@notImplemented expr/>)</#if>
<#if isExpressionType(expr, "ExpressionStringConstant")>      StringConstant(    ${expr.value}                                                                            )</#if>
<#if isExpressionType(expr, "ExpressionEnumString")>          EnumString(        <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list> MAPPED TO     <#list expr.mappings as value, result>Value ${value} -> ${result}<#sep> | </#sep></#list> <@notImplemented expr/>)</#if>
<#if isExpressionType(expr, "ExpressionEui48String")>         Eui48String(       <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionHexString")>           HexString(         <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIPv4AddrString")>      IPv4AddrString(    <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionIPv6AddrString")>      IPv6AddrString(    <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionStringConcat")>        StringConcat(      <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>   <@notImplemented expr/>  )</#if>
<#if isExpressionType(expr, "ExpressionStringField")>         StringField(       ${expr.fieldName}                                                                        )</#if>
<#if isExpressionType(expr, "ExpressionStringFromNumber")>    StringFromNumber(  <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>       )</#if>
<#if isExpressionType(expr, "ExpressionUTF8String")>          UTF8String(        <#list expr.subExpressions as expr><@expression expr=expr/><#sep>, </#sep></#list>       )</#if>
</#if>
</#compress></#macro>
