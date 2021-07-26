import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "art.ryanstew"
version = "1.2.0"

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.onarandombox.com/content/groups/public/")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
    javaParameters = true
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.10.10")
    {
        isTransitive = false
    }

    compileOnly("com.github.SaberLLC:Saber-Factions:2.9.1-RC")
    {
        isTransitive = false
    }

    compileOnly("com.github.Armarr:Autorank-2:4.5.1")
    {
        isTransitive = false
    }

    compileOnly("net.luckperms:api:5.3")
    {
        isTransitive = false
    }

    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.3.0")
    {
        isTransitive = false
    }

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    {
        isTransitive = false
    }

    compileOnly(fileTree("libs"))
}


tasks {
    named<ShadowJar>("shadowJar") {
        relocate("co.aikar.commands", "art.ryanstew.otbmisc")
        relocate("co.aikar.locales", "art.ryanstew.otbmisc")
    }

    build {
        dependsOn(shadowJar)
    }
}

tasks.register<Copy>("copyToTestServer")
{
    from(tasks["shadowJar"])
    into("C:\\Users\\ryans\\Desktop\\Plugin Server\\plugins")
}

tasks.processResources {
    expand("version" to project.version)
}
