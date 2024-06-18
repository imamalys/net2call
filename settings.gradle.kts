pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://plugins.gradle.org/m2/")
        maven("https://www.jitpack.io")
        maven {
            name = "linphone.org maven repository"
            url = uri("https://linphone.org/maven_repository")
            content {
                includeGroup("org.linphone")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
        maven("https://www.jitpack.io")
        maven {
            name = "linphone.org maven repository"
            url = uri("https://linphone.org/maven_repository")
            content {
                includeGroup("org.linphone")
            }
        }
    }
}

rootProject.name = "Net2Call"
include(":sample")
include(":android-phone-sdk")
