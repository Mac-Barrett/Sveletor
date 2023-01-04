val logbackVersion = "1.2.3"
val ktorVersion = "2.0.1"

plugins {
    kotlin("multiplatform") version "1.7.10"
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
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")

                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")

//                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
//                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
//                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
//
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

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