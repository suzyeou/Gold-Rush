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

tasks.jar {
    archiveFileName.set("game_client.jar")

    manifest {
        attributes["Main-Class"] = "edu.io.net.client.GameClient"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.exists() }.map { if (it.isDirectory) it else zipTree(it) }
    })
}