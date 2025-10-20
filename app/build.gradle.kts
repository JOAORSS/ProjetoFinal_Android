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
    implementation(libs.google.material)
    implementation(libs.androidx.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common.jvm)
    implementation(files("./libs/Autopeca360-dominio.jar"))
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.glide)
    implementation(libs.google.material)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.room.runtime)
    implementation(libs.room.guava)
    implementation(libs.room.runtime);
    annotationProcessor(libs.room.runtime);
}