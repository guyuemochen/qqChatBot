plugins {
    val kotlinVersion = "1.5.31"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "mirai.guyuemochen.chatbot"
version = "0.2.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.reflections:reflections:0.10.2")
}
