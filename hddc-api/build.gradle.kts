plugins {
    id(SpringBoot.ID)
    id(SpringBoot.DEPENDENCY_MANAGEMENT)
    id(Flyway.ID)
    id(JOOQ.NS_STUDER_JOOQ)
    kotlin(Kotlin.WITH_JVM)
    kotlin(Kotlin.KAPT)
    kotlin(Kotlin.WITH_SPRING)
    kotlin(Kotlin.WITH_JPA)
}

val root = rootProject.projectDir

apply {
    from("$root/${SpringBootLibs.PATH}")
    from("$root/${DatabaseLibs.PATH}")
    from("$root/${SpringSecurityLibs.PATH}")
    from("$root/${KotlinLibs.PATH}")
    from("$root/${TestLibs.PATH}")
    from("$root/${ApiDocsLibs.PATH}")
    from("$root/${JooqLibs.PATH}")
    from("$root/${JooqLibs.CONFIG_PATH}")
    from("$root/${FlywayLibs.PATH}")
    from("$root/${FlywayLibs.CONFIG_PATH}")
    from("$root/${MessagingLibs.PATH}")
    from("$root/${CacheLibs.PATH}")
    from("$root/${LogLibs.PATH}")
}

tasks.named("generateJooq") {
    dependsOn("flywayMigrate")
}

tasks.named("build") {
    dependsOn("generateJooq")
}

tasks.named("bootJar") {
    dependsOn("test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
