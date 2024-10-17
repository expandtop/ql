pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven {
            setUrl("http://maven.metaapp.cn/repository/maven-releases/")
            isAllowInsecureProtocol = true
        }
        maven { setUrl("https://repo1.maven.org/maven2/")  }
        maven { setUrl("https://maven.aliyun.com/repository/public")  }
        maven { setUrl("https://maven.aliyun.com/repository/google")  }
        maven { setUrl("https://maven.aliyun.com/repository/jcenter")  }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/releases/")  }
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/")  }
        maven { setUrl("https://s01.oss.sonatype.org/content/groups/public")  }
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://raw.githubusercontent.com/DevedHu/maven-repo/main/repository") }
        maven {
            setUrl("http://maven.metaapp.cn/repository/maven-releases/")
            isAllowInsecureProtocol = true
        }
        maven { setUrl("https://repo1.maven.org/maven2/")  }
        maven { setUrl("https://maven.aliyun.com/repository/public")  }
        maven { setUrl("https://maven.aliyun.com/repository/google")  }
        maven { setUrl("https://maven.aliyun.com/repository/jcenter")  }
        maven { setUrl("https://maven.aliyun.com/nexus/content/repositories/releases/")  }
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/")  }
        maven { setUrl("https://s01.oss.sonatype.org/content/groups/public")  }
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            library("androidx-core", "androidx.core:core-ktx:1.9.0")
            library("androidx-appcompat", "androidx.appcompat:appcompat:1.6.1")
            library("androidx-activity", "androidx.activity:activity:1.7.0")
            library("androidx-activity-ktx", "androidx.activity:activity-ktx:1.7.0")
            library("androidx-fragment", "androidx.fragment:fragment:1.5.4")

            library("androidx-constraintlayout", "androidx.constraintlayout:constraintlayout:2.1.4")
            library("androidx-recyclerview", "androidx.recyclerview:recyclerview:1.3.0")

            val lifecycle = "2.6.1"
            library(
                "androidx-lifecycle-runtime",
                "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle"
            )
            library("androidx-lifecycle-common", "androidx.lifecycle:lifecycle-common:$lifecycle")
            library(
                "androidx-lifecycle-viewmodel",
                "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
            )
            library(
                "androidx-lifecycle-livedata",
                "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle"
            )
            library("androidx-lifecycle-process", "androidx.lifecycle:lifecycle-process:$lifecycle")
            library("androidx-lifecycle-service", "androidx.lifecycle:lifecycle-service:$lifecycle")

            val navigation = "2.5.3"
            library(
                "androidx-navigation-fragment",
                "androidx.navigation:navigation-fragment-ktx:$navigation"
            )
            library("androidx-navigation-ui", "androidx.navigation:navigation-ui-ktx:$navigation")

            library("material", "com.google.android.material:material:1.11.0")
            library("coil", "io.coil-kt:coil:2.2.2")
            library("mmkv", "com.tencent:mmkv:1.2.13")


            // okhttp BOM
            val okhttp = "4.12.0"
            library("okhttp3-core", "com.squareup.okhttp3:okhttp:$okhttp")
            library("okhttp3-logging", "com.squareup.okhttp3:logging-interceptor:$okhttp")

            //Retrofit
            val retrofit = "2.9.0"
            library("retrofit2-core", "com.squareup.retrofit2:retrofit:$retrofit")
            library("retrofit2-converter", "com.squareup.retrofit2:converter-gson:$retrofit")

            val coroutines = "1.6.4"
            library("coroutines-core", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
            library(
                "coroutines-android",
                "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines"
            )

            val room = "2.4.2"
            library("androidx-room-compiler", "androidx.room:room-compiler:$room")
            library("androidx-room-runtime", "androidx.room:room-runtime:$room")
            library("androidx-room-ktx", "androidx.room:room-ktx:$room")

            //gson
            library("gson", "com.google.code.gson:gson:2.8.5")

            //noties
            library("noties-core", "io.noties.markwon:core:4.6.2")
            library("noties-image", "io.noties.markwon:image-coil:4.6.2")

            //agentweb
            library("agentweb-core", "io.github.justson:agentweb-core:v5.1.1-androidx")
            library(
                "agentweb-filechooser",
                "io.github.justson:agentweb-filechooser:v5.1.1-androidx"
            )
            library("agentweb-downloader", "com.github.Justson:Downloader:v5.0.4-androidx")

            library("zxing", "com.google.zxing:core:3.5.1")

            //磁盘文件查看工具
            library("monitor-file", "com.github.yangchong211.YCAndroidTool:MonitorFileLib:1.2.8")
            //卡顿工具
            library("monitor-caton", "com.github.yangchong211.YCAndroidTool:MonitorCatonLib:1.2.8")
            //网络工具
            library("monitor-net", "com.github.yangchong211.YCAndroidTool:MonitorNetLib:1.2.8")
            //崩溃工具
            library("monitor-crash", "com.github.yangchong211.YCAndroidTool:MonitorCrashLib:1.2.8")
            //anr工具
            library("monitor-anr", "com.github.yangchong211.YCAndroidTool:MonitorAnrLib:1.2.8")
            //anr工具
            library("monitor-alive", "com.github.yangchong211.YCAndroidTool:MonitorAliveLib:1.2.8")


            library("walle", "com.meituan.android.walle:library:1.1.7")
            library("backgroundlibrary", "com.github.JavaNoober.BackgroundLibrary:libraryx:1.7.6")
        }
    }
}

rootProject.name = "Proxies"
include(":app")
 