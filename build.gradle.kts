plugins {
    val kotlinVersion = "1.5.31"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "mirai.guyuemochen.chatbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
