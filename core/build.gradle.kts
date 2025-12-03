plugins {
    kotlin("jvm")
}

val gdxVersion: String by project

dependencies {
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
