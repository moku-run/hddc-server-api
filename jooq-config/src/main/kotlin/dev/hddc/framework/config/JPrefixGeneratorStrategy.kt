package dev.hddc.framework.config

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy.Mode
import org.jooq.meta.Definition

class JPrefixGeneratorStrategy : DefaultGeneratorStrategy() {
    override fun getJavaClassName(definition: Definition, mode: Mode): String {
        return "J${super.getJavaClassName(definition, mode)}"
    }
}
