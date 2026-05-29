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
        // 不设上限：插件只用稳定的 AnAction / Editor / CopyPasteManager API，
        // 避免每次 IDE 大版本升级都因为 untilBuild 限制装不上。
        untilBuild.set(provider { null })
    }

    buildSearchableOptions {
        enabled = false
    }
}
