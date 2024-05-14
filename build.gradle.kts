plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.3.1").apply(false)
    id("com.android.library").version("8.3.1").apply(false)
    id("com.squareup.sqldelight").version("1.5.5").apply(false)
    kotlin("android").version("1.9.22").apply(false)
    kotlin("multiplatform").version("1.9.22").apply(false)
    kotlin("plugin.serialization").version("1.9.22").apply(false)
    id("org.sonarqube") version "5.0.0.4638"
    id("com.apollographql.apollo3").version("3.8.2").apply(false)
    id("com.google.devtools.ksp").version("1.9.22-1.0.16").apply(false)
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    afterEvaluate {
        tasks.register("testClasses") {}
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

sonarqube {
    properties {
        property("sonar.projectKey", "sora:x-networking")
        property("sonar.projectName", "x-networking")
        property("sonar.exclusions", "**/*.txt,**/*.kts")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.projectDir}/build/reports/coverage/*.xml")
    }
}
