pluginManagement {
    repositories {
        // 阿里云镜像 - Google
        maven("https://maven.aliyun.com/repository/google") {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // 阿里云镜像 - Central
        maven("https://maven.aliyun.com/repository/central")
        // 阿里云镜像 - Gradle Plugin Portal
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        // 腾讯云镜像作为备用
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        // 官方仓库作为最后备选
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 阿里云镜像 - Google
        maven("https://maven.aliyun.com/repository/google")
        // 阿里云镜像 - Central
        maven("https://maven.aliyun.com/repository/central")
        // 阿里云镜像 - JCenter
        maven("https://maven.aliyun.com/repository/jcenter")
        // 腾讯云镜像作为备用
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        // 华为云镜像作为备用
        maven("https://repo.huaweicloud.com/repository/maven/")
        // 官方仓库作为最后备选
        google()
        mavenCentral()
    }
}

rootProject.name = "CoreCodeLibrary"

include(":app")
include(":corecode")
include(":common")