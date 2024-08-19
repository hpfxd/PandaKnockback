plugins {
    id("java")
}

group = "com.hpfxd"
version = "1.0.0"
description = "A lightweight plugin for PandaSpigot 1.8.8 providing more customizable player knockback"

repositories {
    mavenCentral()
    maven("https://repo.hpfxd.com/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("com.hpfxd.pandaspigot:pandaspigot-api:1.8.8-R0.1-SNAPSHOT")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()

        filesMatching("plugin.yml") {
            expand(
                "version" to project.version,
                "description" to project.description,
            )
        }
    }
}
