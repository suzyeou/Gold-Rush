plugins {
    id("java")
}

group = "edu.io.net"
version = "1.2.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":game_common"))
    implementation(project(":game_connector_lib"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}