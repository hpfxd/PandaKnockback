plugins {
    id("java")
    id("maven-publish")
}

group = "com.hpfxd"
version = "1.0.0"
description = "A lightweight plugin for PandaSpigot 1.8.8 providing more customizable player knockback"

repositories {
    mavenCentral()
    maven("https://repo.hpfxd.com/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    maven("https://maven.enginehub.org/repo/") {
        content {
            includeGroup("com.sk89q.worldguard")
            includeGroup("com.sk89q.worldedit")
        }
    }
}

dependencies {
    compileOnly("com.hpfxd.pandaspigot:pandaspigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
            }

            pom {
                licenses {
                    license {
                        name = "GPL-v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.txt"
                    }
                }

                developers {
                    developer {
                        id = "hpfxd"
                        name = "hpfxd"
                        email = "me@hpfxd.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/hpfxd/PandaKnockback.git"
                    developerConnection = "scm:git:git://github.com/hpfxd/PandaKnockback.git"
                    url = "https://github.com/hpfxd/PandaKnockback"
                }
            }
        }
    }

    findProperty("repository.hpfxd.username")?.let { repoUsername ->
        repositories {
            maven {
                name = "hpfxd-repo"
                url = uri("https://repo.hpfxd.com/releases/")

                credentials {
                    username = repoUsername as String
                    password = findProperty("repository.hpfxd.password") as String
                }
            }
        }
    }
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
