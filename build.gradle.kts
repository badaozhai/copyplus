plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.copyplus"
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.3")
    type.set("IC")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("243.*")
    }

    buildSearchableOptions {
        enabled = false
    }
}
