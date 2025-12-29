import com.badlogic.gdx.tools.texturepacker.TexturePacker

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
        classpath("com.badlogicgames.gdx:gdx-tools:1.14.0")
    }
}

allprojects {
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
    extra["gdxVersion"] = "1.14.0"
}


// === this is for downloading TexturePacker ===
val tools by configurations.creating
val gdxVersion = extra["gdxVersion"].toString()
dependencies {
    add("tools", "com.badlogicgames.gdx:gdx-tools:$gdxVersion")
}

tasks.register<Copy>("fetchTexturePacker") {
    from(tools)
    into(layout.buildDirectory.dir("tools"))
}

tasks.register("runTexturePacker") {
    doLast {
        val s = TexturePacker.Settings().apply {
            maxWidth = 4096
            maxHeight = 4096
            paddingX = 2
            paddingY = 2
            edgePadding = true
            duplicatePadding = true
        }
        TexturePacker.process(s, "android/assets/images", "android/assets/sprites", "atlas")
    }
}
