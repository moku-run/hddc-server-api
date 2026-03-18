plugins {
    `kotlin-dsl`
    groovy
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation("org.yaml:snakeyaml:2.2")
    implementation("org.jooq:jooq-codegen:3.20.8")
    implementation("org.jooq:jooq-meta:3.20.8")
}
