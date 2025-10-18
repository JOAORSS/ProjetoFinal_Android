import java.net.Inet4Address
import java.net.NetworkInterface

plugins {
    alias(libs.plugins.android.application)
}

fun getHostLanIp(): String {
    try {
        val interfaces = NetworkInterface.getNetworkInterfaces().toList()
        for (iface in interfaces) {
            if (iface.isLoopback || !iface.isUp) continue

            for (addr in iface.inetAddresses.toList()) {
                if (addr is Inet4Address && addr.isSiteLocalAddress) {
                    return addr.hostAddress
                }
            }
        }
    } catch (e: Exception) {
        println("Erro ao detectar IP: ${e.message}")
    }
    return "10.0.2.2"
}

android {
    namespace = "com.example.app06_materialss"
    compileSdk = 36
    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.example.app06_materialss"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            val hostIp = getHostLanIp()

            println("=======================================================")
            println("==> IP do Servidor Host (Debug): $hostIp")
            println("=======================================================")

            buildConfigField("String", "SERVER_HOST_IP", "\"$hostIp\"")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SERVER_HOST_IP", "\"ip_do_servidor_de_producao.com\"")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common.jvm)
    implementation(files("./libs/Autopeca360-dominio.jar"))
    implementation(libs.core.splashscreen)
    implementation(libs.glide)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.room.runtime)
    implementation(libs.room.guava)

    val room_version = "2.8.2"
    implementation("androidx.room:room-runtime:$room_version");
    annotationProcessor("androidx.room:room-runtime:$room_version");
}