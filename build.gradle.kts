plugins {
    application
    java
}

group = "com.passwordGenerator"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.24.1")
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")

    val javafxVersion = "21.0.4"

    implementation("org.openjfx:javafx-controls:$javafxVersion")
    implementation("org.openjfx:javafx-graphics:$javafxVersion")
    implementation("org.openjfx:javafx-base:$javafxVersion")
    implementation("org.openjfx:javafx-fxml:$javafxVersion")

    implementation("org.openjfx:javafx-controls:$javafxVersion:win")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:win")
    implementation("org.openjfx:javafx-base:$javafxVersion:win")
    implementation("org.openjfx:javafx-fxml:$javafxVersion:win")

    implementation("org.openjfx:javafx-controls:$javafxVersion:mac")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:mac")
    implementation("org.openjfx:javafx-base:$javafxVersion:mac")
    implementation("org.openjfx:javafx-fxml:$javafxVersion:mac")

    implementation("org.openjfx:javafx-controls:$javafxVersion:mac-aarch64")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:mac-aarch64")
    implementation("org.openjfx:javafx-base:$javafxVersion:mac-aarch64")
    implementation("org.openjfx:javafx-fxml:$javafxVersion:mac-aarch64")

    implementation("org.openjfx:javafx-controls:$javafxVersion:linux")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:linux")
    implementation("org.openjfx:javafx-base:$javafxVersion:linux")
    implementation("org.openjfx:javafx-fxml:$javafxVersion:linux")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-params:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("com.passwordGenerator.PasswordGeneratorApplication")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
}

tasks.jar { enabled = false }
tasks.distZip { enabled = false }
tasks.distTar { enabled = false }
tasks.startScripts { enabled = false }

tasks.register<Jar>("fatJar") {
    archiveBaseName.set("PasswordGenerator")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Main-Class" to "com.passwordGenerator.PasswordGeneratorApplication",
            "Implementation-Title" to "PasswordGenerator"
        )
    }

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

tasks.build {
    dependsOn("fatJar")
}

val osName = System.getProperty("os.name").lowercase()
val isMac = osName.contains("mac")

tasks.run.get().apply {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.passwordGenerator.PasswordGeneratorApplication")

    standardInput = System.`in`

    if (!project.hasProperty("args")) {
        args = listOf("--ui=console")
    }

    if (isMac) {
        jvmArgs = listOf(
            "-XstartOnFirstThread",
            "-Dapple.laf.useScreenMenuBar=true",
            "-Dcom.apple.mrj.application.apple.menu.about.name=PasswordGenerator"
        )
    }
}
