buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.postgresql:postgresql:42.7.4")
        classpath(Flyway.POSTGRESQL_PATH)
    }
}

plugins {
    java
    id(SpringBoot.ID) version SpringBoot.VERSION apply false
    id(SpringBoot.DEPENDENCY_MANAGEMENT) version SpringBoot.DEPENDENCY_MANAGEMENT_VERSION apply false
    id(Flyway.ID) version Flyway.VERSION apply false
    id(JOOQ.NS_STUDER_JOOQ) version JOOQ.VERSION apply false
    kotlin(Kotlin.WITH_JVM) version Kotlin.KOTLIN_VERSION apply false
    kotlin(Kotlin.KAPT) version Kotlin.KOTLIN_VERSION apply false
    kotlin(Kotlin.WITH_SPRING) version Kotlin.KOTLIN_VERSION apply false
    kotlin(Kotlin.WITH_JPA) version Kotlin.KOTLIN_VERSION apply false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(Versioning.JAVA)
    }
}

allprojects {
    group = Versioning.GROUP
    version = Versioning.VERSION
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
