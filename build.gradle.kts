plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("net.kyori.indra") version "2.1.1"
    id("net.kyori.indra.git") version "2.1.1"
}

group = "net.leonardo_dgs"
version = "1.6.3"
description = "Create cool interactive books!"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        content {
            includeGroup("io.papermc.paper")
            includeGroup("net.md-5")
        }
    }
    maven("https://repo.codemc.org/repository/maven-public/") {
        content {
            includeGroup("de.tr7zw")
        }
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi") {
        content {
            includeGroup("me.clip")
        }
    }
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.Simplix-Softworks")
        }
    }
}

dependencies {
    // API
    compileOnly("io.papermc.paper", "paper-api", "1.19.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "23.0.0")
    compileOnly("org.projectlombok", "lombok", "1.18.24")
    annotationProcessor("org.projectlombok", "lombok", "1.18.24")

    // Plugin libraries
    compileOnly("me.lucko", "helper", "5.6.10")
    compileOnly("me.clip", "placeholderapi", "2.11.2")

    // Libraries that needs to be shaded
    implementation("de.tr7zw", "item-nbt-api", "2.10.1-SNAPSHOT")
    implementation("com.github.Simplix-Softworks", "SimplixStorage", "3.2.5")
    implementation("org.bstats", "bstats-bukkit", "3.0.0")
    val cloudVersion = "1.7.1"
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion) {
        exclude("net.kyori")
    }
}

indra {
    javaVersions().target(17)
}

bukkit {
    main = "net.leonardo_dgs.interactivebooks.InteractiveBooks"
    name = project.name
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    depend = listOf("helper")
    softDepend = listOf("PlaceholderAPI")
    authors = listOf("Leonardo_DGS")
    website = "https://www.spigotmc.org/resources/45604/"
}

tasks {
    jar {
        archiveClassifier.set("noshade")
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        minimize()
        archiveFileName.set("${project.name}-${project.version}.jar")
        archiveClassifier.set("")
        sequenceOf(
            "de.tr7zw",
            "org.bstats",
            "de.leonhard.storage",
            "cloud.commandframework",
            "io.leangen.geantyref"
        ).forEach {
            relocate(it, "net.leonardo_dgs.interactivebooks.lib.$it")
        }
    }
//    processResources {
//        val tokens = mapOf(
//            "project.version" to project.version
//        )
//        inputs.properties(tokens)
//    }
    task("deploy") {
        dependsOn(build)
        doLast {
            exec {
                commandLine("rsync", "${shadowJar.get().archiveFile.get()}", "dev:data/dev/plugins")
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7) ?: error("Could not determine commit hash")
fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this