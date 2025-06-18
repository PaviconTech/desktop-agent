rootProject.name = "DesktopAgent"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()

    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://mobile.maven.couchbase.com/maven2/dev/")
        maven {
            url = uri("http://repo.e-iceblue.com/nexus/content/groups/public/")
            isAllowInsecureProtocol = true
        }
        maven("https://jitpack.io")
    }
}

include(":composeApp")