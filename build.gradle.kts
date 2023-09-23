import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "InteractiveBooks")

group = "net.leonardo_dgs"
version = "1.6.6"
description = "Create cool interactive books!"

dependencies {
    // mewcraft libs
    compileOnly(project(":mewcore"))

    // libs in core
    compileOnly(libs.simplixstorage)

    // server api
    compileOnly(libs.server.paper)

    // plugin libraries
    compileOnly(libs.helper)
    compileOnly(libs.papi)

    // compile time stuff
    compileOnly("org.projectlombok", "lombok", "1.18.24")
    annotationProcessor("org.projectlombok", "lombok", "1.18.24")
}

paper {
    main = "net.leonardo_dgs.interactivebooks.InteractiveBooks"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    authors = listOf("Nailm", "Leonardo_DGS")

    serverDependencies {
        register("helper") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("MewCore") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("PlaceholderAPI") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}
