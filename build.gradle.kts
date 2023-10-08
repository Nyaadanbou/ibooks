import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.deploy-conventions")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "InteractiveBooks")

group = "net.leonardo_dgs"
version = "1.6.6"
description = "Create cool interactive books!"

dependencies {
    // internal
    implementation(libs.simplixstorage)
    implementation(libs.bundles.cmds.paper)

    // server
    compileOnly(libs.server.paper)

    // standalone plugins
    compileOnly(libs.papi)
}

paper {
    main = "net.leonardo_dgs.interactivebooks.InteractiveBooks"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    authors = listOf("Leonardo_DGS")
    contributors = listOf("Nailm")

    serverDependencies {
        register("PlaceholderAPI") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}
