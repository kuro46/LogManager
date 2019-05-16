import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    java
}

group = "com.github.kuro46"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compileOnly("org.bukkit", "bukkit", "1.12.2-R0.1-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    archiveFileName.set("LogManager.jar")
}

tasks.withType<ProcessResources> {
    filter { it.replace("\$version", version.toString()) }
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
}

tasks.withType<ShadowJar> {
    minimize()
    relocatePackage("kotlin")
    relocatePackage("org.intellij")
    relocatePackage("org.jetbrains")
}

fun ShadowJar.relocatePackage(target: String) {
    relocate(target, "com.github.kuro46.logmanager.libs.$target")
}

