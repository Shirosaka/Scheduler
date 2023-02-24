plugins {
    id("java")
    id("application")
}

group = "dev.shirosaka.scheduler"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("dev.shirosaka.scheduler.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}