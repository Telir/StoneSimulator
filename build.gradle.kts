plugins {
    id("java")
    kotlin("jvm") version "2.0.0"
}

group = "my.telir"
version = "1.0.0"
description = "Stone Simulator"


java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.2")

    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")

    //BuildTools
    compileOnly(files("lib/spigot-1.12.2-R0.1-SNAPSHOT.jar"))

}

tasks {
    jar {
        from(sourceSets["main"].resources)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(configurations["runtimeClasspath"].map { if (it.isDirectory) it else zipTree(it) })
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description
        )
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

val outputDir: String by project

tasks.register<Copy>("copyJar") {
    dependsOn("build")

    file(outputDir).listFiles { file -> file.name.startsWith(project.name) }?.forEach { it.delete() }

    from(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
    into(file(outputDir))
}