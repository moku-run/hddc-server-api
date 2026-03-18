object FlywayLibs {
    const val ID = "org.flywaydb.flyway"
    const val VERSION = "11.15.0"
    const val POSTGRESQL_VERSION = "42.7.4"

    const val PATH = "gradle/dep/flyway-dep.gradle"
    const val CONFIG_PATH = "gradle/config/flyway-config.gradle"

    const val CORE = "org.flywaydb:flyway-core:$VERSION"
    const val DB_POSTGRESQL = "org.flywaydb:flyway-database-postgresql:$VERSION"

    const val POSTGRESQL_CONNECTOR = "org.postgresql:postgresql:$POSTGRESQL_VERSION"
}
