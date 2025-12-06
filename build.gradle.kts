buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    }
}

allprojects {
    apply(plugin = "eclipse")
    apply(plugin = "idea")

    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven { url = uri("https://jitpack.io") }
    }
}

configure(subprojects) {
    version = "1.0.0"
    extra["appName"] = "Asteroid Redux"
    extra["gdxVersion"] = "1.13.0" // Using a stable version known to work well, or 1.14.0 if preferred
}
