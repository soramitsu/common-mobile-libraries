import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("native.cocoapods")
    id("maven-publish")
    id("com.squareup.sqldelight")
}

group = "jp.co.soramitsu"

version = "0.0.23"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "jp.co.soramitsu"
            artifactId = "common-networking"
            version = "0.0.23"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "scnRepo"
            url = uri(if (hasProperty("RELEASE_REPOSITORY_URL")) property("RELEASE_REPOSITORY_URL")!! else System.getenv()["RELEASE_REPOSITORY_URL"]!!)
            credentials {
                username = if (hasProperty("NEXUS_USERNAME")) (property("NEXUS_USERNAME") as String) else System.getenv()["NEXUS_USERNAME"]
                password = if (hasProperty("NEXUS_PASSWORD")) (property("NEXUS_PASSWORD") as String) else System.getenv()["NEXUS_PASSWORD"]
            }
        }
        maven {
            name = "scnRepoLocal"
            url = uri("${project.buildDir}/scnrepo")
        }
    }
}

val coroutineVersion = "1.6.1"
val ktorVersion = "2.0.0"

kotlin {
    val iosFrameworkName = "X-Networking"

    android()
    iosX64 { binaries.framework(iosFrameworkName) }
    iosArm64 { binaries.framework(iosFrameworkName) }
    iosSimulatorArm64 { binaries.framework(iosFrameworkName) }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "X-Networking"
        }
    }

    val sqlDelightVersion: String by project
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                //implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")

                //implementation("com.ionspin.kotlin:bignum:0.3.4")
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                api("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }

    tasks {
        register("universalFrameworkDebug",
            org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask::class) {
            baseName = iosFrameworkName
            from(
                iosArm64().binaries.getFramework(iosFrameworkName, "Debug"),
                iosX64().binaries.getFramework(iosFrameworkName, "Debug")
            )
            destinationDir = buildDir.resolve("bin/universal/debug")
            group = "Universal framework"
            description = "Builds a universal (fat) debug framework"
            dependsOn("link${iosFrameworkName}DebugFrameworkIosArm64")
            dependsOn("link${iosFrameworkName}DebugFrameworkIosX64")
        }
        register("universalFrameworkRelease",
            org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask::class) {
            baseName = iosFrameworkName
            from(
                iosArm64().binaries.getFramework(iosFrameworkName, "Release"),
                iosX64().binaries.getFramework(iosFrameworkName, "Release")
            )
            destinationDir = buildDir.resolve("bin/universal/release")
            group = "Universal framework"
            description = "Builds a universal (fat) release framework"
            dependsOn("link${iosFrameworkName}ReleaseFrameworkIosArm64")
            dependsOn("link${iosFrameworkName}ReleaseFrameworkIosX64")
        }
        register("universalFramework") {
            dependsOn("universalFrameworkDebug")
            dependsOn("universalFrameworkRelease")
        }
    }

    val xcf = XCFramework()

    ios {
        binaries.framework {
            baseName = iosFrameworkName
            xcf.add(this)
        }
    }

    android {
        publishAllLibraryVariants()
    }
}

sqldelight {
    database("SoraHistoryDatabase") {
        packageName = "jp.co.soramitsu.commonnetworking.db"
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}

tasks.register<Copy>("copyiOSTestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosX64/debugTest/resources")
}

tasks.findByName("iosX64Test")!!.dependsOn("copyiOSTestResources")
