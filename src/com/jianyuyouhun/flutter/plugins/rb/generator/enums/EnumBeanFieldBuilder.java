package com.jianyuyouhun.flutter.plugins.rb.generator.enums;

public class EnumBeanFieldBuilder {
    public String buildFieldCode(String enumName, String jsonVarName) {
        return "/// \n" +
                "///   @JSONField(name: '" + jsonVarName + "')\n" +
                "///   String? " + jsonVarName + "String;\n" +
                "/// \n" +
                "///   @JSONField(serialize: false, deserialize: false)\n" +
                "///   " + enumName + "? _" + jsonVarName + ";\n" +
                "/// \n" +
                "///   " + enumName + " get " + jsonVarName + " {\n" +
                "///     if (_" + jsonVarName + " == null) {\n" +
                "///       _" + jsonVarName + " = " + enumName + "Utils.build(" + jsonVarName + "String);\n" +
                "///     }\n" +
                "///     return _" + jsonVarName + "!;\n" +
                "///   }\n" +
                "/// \n" +
                "///   set " + jsonVarName + "(" + enumName + " type) {\n" +
                "///     _" + jsonVarName + " = type;\n" +
                "///     " + jsonVarName + "String = _" + jsonVarName + "!.value();\n" +
                "///   }\n\n";
    }
}
