plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val pluginName = "HuaweiIAP"
val pluginPackageName = "com.huawei.godot.iap"

android {
    namespace = pluginPackageName
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    libraryVariants.all {
        val variantName = name
        val aarTask = tasks.register("${variantName}CopyAAR") {
            doLast {
                val aarFile = layout.buildDirectory.file("outputs/aar/${project.name}-$variantName.aar")
                val outputDir = rootProject.layout.projectDirectory.dir("plugin/addons/$pluginName")
                copy {
                    from(aarFile)
                    into(outputDir)
                    rename(".*\\.aar", "$pluginName.aar")
                }
            }
        }
        tasks.named("assemble").configure {
            finalizedBy(aarTask)
        }
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://developer.huawei.com/repo/") }
}

dependencies {
    implementation("org.godotengine:godot:4.7.0.stable")
    implementation("com.huawei.hms:iap:6.16.6.305")
}
