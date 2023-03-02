val logbackVersion = "1.2.3"
val ktorVersion = "2.2.2"

plugins {
    kotlin("multiplatform") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    application
}

group = "com.sveletor"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")

                implementation("io.ktor:ktor-server-cors:$ktorVersion")
                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
                implementation("io.ktor:ktor-server-conditional-headers:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

//                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
//                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
//                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

                implementation("ch.qos.logback:logback-classic:$logbackVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

application {
    mainClass.set("com.sveletor.application.ServerKt")
}

// Literally just a script that calls npm install then npm build. I'm new to gradle, go easy on me.
tasks.register<Exec>("compileSvelte") {
    doFirst { println("Compiling Svelte Project...") }
    commandLine("./npmBuild.cmd")
}

tasks.getByName("jvmProcessResources") {
    dependsOn("compileSvelte")
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}