import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "top.e404"
version = "1.20.1"
val epluginVer = "1.4.0"

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
    compileOnly("me.clip:placeholderapi:2.11.6")
    // Bstats
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // mock bukkit
    testImplementation(kotlin("test", "2.1.21"))
    testImplementation("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.87.0")
    testImplementation("org.slf4j:slf4j-simple:2.0.13")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
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
