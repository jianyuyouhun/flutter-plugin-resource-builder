package com.jianyuyouhun.flutter.plugins.rb.generator;

import com.jianyuyouhun.flutter.plugins.rb.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 代码生成器基本逻辑处理
 */
public abstract class AbstractCodeGenerator implements CodeGenerator {

    public abstract void prepare();

    @NotNull
    public abstract File defineTargetFile();

    @NotNull
    public abstract String generateCode();


    @Override
    public void generate() {
        prepare();
        File file = defineTargetFile();
        String codeString = generateCode();
        FileUtils.putInFile(file, codeString);
    }
}
