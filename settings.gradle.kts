rootProject.name = "hddc-server-api"
include("hddc-api")
include("jooq-config")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
