import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "top.e404"
version = "1.0.8"

repositories {
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    // Bstats
    implementation("org.bstats:bstats-bukkit:3.0.0")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    relocate("org.bstats", "top.e404.clean.bstats")
    exclude("META-INF/*")
    doFirst {
        for (file in File("jar").listFiles() ?: arrayOf()) {
            println("正在删除`${file.name}`")
            file.delete()
        }
    }

    doLast {
        File("jar").mkdirs()
        for (file in File("build/libs").listFiles() ?: arrayOf()) {
            println("正在复制`${file.name}`")
            file.copyTo(File("jar/${file.name}"), true)
        }
    }
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
}