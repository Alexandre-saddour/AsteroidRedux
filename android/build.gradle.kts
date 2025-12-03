plugins {
    id("com.android.application")
    kotlin("android")
}

val gdxVersion: String by project

android {
    namespace = "com.example.asteroidsredux"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.asteroidsredux"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
            assets.srcDirs("assets")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// Helper to define natives configuration
val natives = configurations.create("natives")

dependencies {
    implementation(project(":core"))
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("androidx.core:core-ktx:1.12.0")

//    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
//    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
//    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
//    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}

//tasks.register("copyAndroidNatives") {
//    doFirst {
//        file("libs/armeabi-v7a/").mkdirs()
//        file("libs/arm64-v8a/").mkdirs()
//        file("libs/x86_64/").mkdirs()
//        file("libs/x86/").mkdirs()
//
//        configurations["natives"].files.forEach { jar ->
//            var outputDir: File? = null
//            if (jar.name.endsWith("natives-armeabi-v7a.jar")) outputDir = file("libs/armeabi-v7a")
//            if (jar.name.endsWith("natives-arm64-v8a.jar")) outputDir = file("libs/arm64-v8a")
//            if (jar.name.endsWith("natives-x86_64.jar")) outputDir = file("libs/x86_64")
//            if (jar.name.endsWith("natives-x86.jar")) outputDir = file("libs/x86")
//
//            if (outputDir != null) {
//                copy {
//                    from(zipTree(jar))
//                    into(outputDir)
//                    include("*.so")
//                }
//            }
//        }
//    }
//}

//tasks.whenTaskAdded {
//    if (name.contains("package")) {
//        dependsOn("copyAndroidNatives")
//    }
//}
