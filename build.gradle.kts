import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "top.e404"
version = "1.17.1"
val epluginVer = "1.2.0"

fun eplugin(module: String, version: String = epluginVer) = "top.e404:eplugin-$module:$version"

repositories {
    mavenLocal()
    // papermc
    maven("https://repo.papermc.io/repository/maven-public/")
    // spigot
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    // sonatype
    maven("https://oss.sonatype.org/content/groups/public/")
    // placeholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    mavenCentral()
}

dependencies {
    // spigot
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    // eplugin
    implementation(eplugin("core"))
    implementation(eplugin("menu"))
    implementation(eplugin("serialization"))
    implementation(eplugin("hook-placeholderapi"))
    // placeholderAPI
    compileOnly("me.clip:placeholderapi:2.11.1")
    // Bstats
    implementation("org.bstats:bstats-bukkit:3.0.0")

    // mock bukkit
    testImplementation(kotlin("test", "1.8.21"))
    testImplementation("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.21.1")
    testImplementation("org.slf4j:slf4j-simple:2.0.7")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
}

tasks {
    build {
        finalizedBy(shadowJar)
    }

    shadowJar {
        val archiveName = "${project.name}-${project.version}.jar"
        archiveFileName.set(archiveName)

        relocate("org.bstats", "top.e404.eclean.relocate.bstats")
        relocate("kotlin", "top.e404.eclean.relocate.kotlin")
        relocate("top.e404.eplugin", "top.e404.eclean.relocate.eplugin")
        relocate("com.charleskorn.kaml", "top.e404.eclean.relocate.kaml")
        exclude("META-INF/**")

        doLast {
            val archiveFile = archiveFile.get().asFile
            println(archiveFile.parentFile.absolutePath)
            println(archiveFile.absolutePath)
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"

        dependsOn(clean)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    test {
        useJUnitPlatform()
        this.systemProperties["eclean.debug"] = true
    }
}
