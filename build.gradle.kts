import java.util.zip.ZipFile

plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    `maven-publish`
}

group = property("maven_group") as String
version = "${property("mod_version")}+fabric-mc${property("minecraft_version")}"

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
}

loom {
    mods {
        create("farmerscreate") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation("maven.modrinth:create-fly:${property("create_fabric_version")}")
    implementation("maven.modrinth:farmers-delight-refabricated:${property("farmers_delight_version")}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.processResources {
    val modMetadata = mapOf(
        "version" to project.version.toString(),
        "minecraft_dependency_version" to project.property("minecraft_dependency_version") as String,
        "fabric_loader_version" to project.property("fabric_loader_version") as String,
        "create_fabric_version_range" to project.property("create_fabric_version_range") as String,
        "farmers_delight_version_range" to project.property("farmers_delight_version_range") as String,
    )
    inputs.properties(modMetadata)
    filesMatching("fabric.mod.json") {
        expand(modMetadata)
    }
}

tasks.jar {
    from("LICENSE.txt")
    from("NOTICE")
}

tasks.named<Jar>("sourcesJar") {
    from("LICENSE.txt")
    from("NOTICE")
}

val allowedJarPrefixes = listOf(
    "de/chefexperte/farmersCreate/",
    "assets/farmerscreate/",
)

val allowedJarFiles = listOf(
    "fabric.mod.json",
    "farmers-create.mixins.json",
    "LICENSE.txt",
    "NOTICE",
)

fun checkNamespaces(archive: File): List<String> {
    val strays = mutableListOf<String>()
    val zip = ZipFile(archive)
    try {
        val entries = zip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.isDirectory) continue
            val name = entry.name
            if (name.startsWith("META-INF/")) continue
            if (name in allowedJarFiles) continue
            if (allowedJarPrefixes.any { name.startsWith(it) }) continue
            strays += name
        }
    } finally {
        zip.close()
    }
    return strays
}

val verifyNamespaces = tasks.register("verifyNamespaces") {
    dependsOn(tasks.jar, tasks.named("sourcesJar"))
    val jars = listOf(tasks.jar, tasks.named<Jar>("sourcesJar")).map { it.get().archiveFile }
    doLast {
        val problems = jars.flatMap { file ->
            val archive = file.get().asFile
            checkNamespaces(archive).map { "${archive.name}: $it" }
        }
        if (problems.isNotEmpty()) {
            throw GradleException("Foreign namespaces in the build output:\n" + problems.joinToString("\n"))
        }
    }
}

tasks.named("check") {
    dependsOn(verifyNamespaces)
}
