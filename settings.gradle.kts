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

// 组件数据类定义
data class Component(
    val index: Int,
    val module: String,
    val localEnable: Boolean = true,
    val group: String = "org.jxdx.xjh",
    val version: String = "1.0.0",
    val snapshot: Boolean = false,
    val localPath: String? = null,
    val dependencies: List<Int> = emptyList(),
    val apiDependencies: List<Int> = emptyList(),
    val includes: List<Int>? = null
)

// 组件配置
val components = listOf(
    Component(
        index = 0,
        module = "corecode",
        localEnable = true
    ),
    Component(
        index = 1,
        module = "common",
        localEnable = true,
        apiDependencies = listOf(0)
    ),
    Component(
        index = 2,
        module = "app",
        localEnable = true,
        dependencies = listOf(1),
        // 主模块标识，包含需要include的子模块
        includes = listOf(0, 1)
    )
)

// 依赖模式枚举
enum class DependencyMode(val value: Int) {
    IMPLEMENTATION(0),
    API(1)
}

/**
 * 查找组件并处理其依赖关系
 * @param moduleName 模块名
 * @param action 处理每个依赖的回调函数
 */
fun findComponent(moduleName: String, action: (Component, DependencyMode) -> Unit): Component? {
    val component = components.find { it.module == moduleName }

    component?.let { comp ->
        if (comp.dependencies.isNotEmpty() || comp.apiDependencies.isNotEmpty()) {
            val depStr = if (comp.dependencies.isNotEmpty()) "normal{${comp.dependencies}}" else ""
            val apiDepStr = if (comp.apiDependencies.isNotEmpty()) "api{${comp.apiDependencies}}" else ""
            println("║ [${comp.module}]---has dependencies:$depStr $apiDepStr")

            // 处理implementation依赖
            comp.dependencies.forEach { index ->
                val dependency = components[index]
                action(dependency, DependencyMode.IMPLEMENTATION)
            }

            // 处理api依赖
            comp.apiDependencies.forEach { index ->
                val dependency = components[index]
                action(dependency, DependencyMode.API)
            }
        }
    }

    return component
}

/**
 * 为指定模块导入依赖
 * @param moduleName 模块名
 * @param dependencyHandler 依赖处理器
 */
fun importDependencies(moduleName: String, dependencyHandler: DependencyHandler) {
    println("╔════════════════Build Script 【$moduleName】 Start════════════════╗")

    findComponent(moduleName) { dependency, mode ->
        val (path, description) = if (dependency.localEnable) {
            val localPath = ":${dependency.module}"
            val desc = "is localed, is imported ${dependency.localPath ?: "in project"}"
            localPath to desc
        } else {
            val remote = "${dependency.version}${if (dependency.snapshot) "-SNAPSHOT" else ""}"
            val remotePath = "${dependency.group}:${dependency.module}:$remote"
            val desc = "is remoted, version:$remote"
            remotePath to desc
        }

        println("║  └————component:[${dependency.module}] $description")

        when (mode) {
            DependencyMode.API -> dependencyHandler.add("api", path)
            DependencyMode.IMPLEMENTATION -> dependencyHandler.add("implementation", path)
        }
    }

    println("╚════════════════Build Script 【$moduleName】   End════════════════╝")
}

// 执行包含组件的逻辑
val rootModule = components.find { it.includes != null }

if (rootModule != null) {
    println("╔════════════════Setting Script 【${rootModule.module}】 Start════════════════")

    // 包含主模块
    include(":${rootModule.module}")

    // 包含所有子模块
    rootModule.includes?.forEach { index ->
        val dependency = components[index]
        if (dependency.localEnable) {
            dependency.localPath?.let { localPath ->
                println("║  └————enable local dependency:[${dependency.module}], path:$localPath")
                include(":${dependency.module}")
                project(":${dependency.module}").projectDir =
                    File(rootProject.projectDir.parentFile, localPath)
            } ?: run {
                println("║  └————enable local dependency:[${dependency.module}], path:in project")
                include(":${dependency.module}")
            }
        }
    }

    println("╚════════════════Setting Script 【${rootModule.module}】   End════════════════")
} else {
    println("can not find any root Module with 'includes' tag")
}
