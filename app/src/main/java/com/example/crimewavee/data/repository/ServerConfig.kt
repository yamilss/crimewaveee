package com.example.crimewavee.data.repository

import android.content.Context
import android.util.Log
import java.net.InetAddress

object ServerConfig {
    private const val TAG = "ServerConfig"

    // Configuración de servidores
    private const val LOCAL_EMULATOR = "http://10.0.2.2:8080/"
    private const val LOCAL_DEVICE = "http://192.168.1.100:8080/" // Cambiar por tu IP local
    private const val AWS_EC2 = "http://3.15.178.116:8080/" // ✅ IP DE EC2 CONFIGURADA

    // ⚠️ INSTRUCCIÓN: Reemplaza TU-IP-EC2 con la IP pública de tu instancia EC2
    // Ejemplo: "http://52.123.45.67:8080/"

    fun getBaseUrl(): String {
        // Intentar detectar automáticamente el mejor servidor
        return try {
            when {
                isEmulator() -> {
                    Log.d(TAG, "Detectado emulador Android - usando localhost")
                    LOCAL_EMULATOR
                }
                isServerReachable(extractHost(AWS_EC2)) -> {
                    Log.d(TAG, "Servidor AWS EC2 disponible")
                    AWS_EC2
                }
                isServerReachable(extractHost(LOCAL_DEVICE)) -> {
                    Log.d(TAG, "Servidor local disponible")
                    LOCAL_DEVICE
                }
                else -> {
                    Log.w(TAG, "No se pudo conectar a ningún servidor - usando emulador por defecto")
                    LOCAL_EMULATOR
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error detectando servidor: ${e.message}")
            LOCAL_EMULATOR
        }
    }

    private fun isEmulator(): Boolean {
        return android.os.Build.FINGERPRINT.startsWith("generic") ||
               android.os.Build.FINGERPRINT.startsWith("unknown") ||
               android.os.Build.MODEL.contains("google_sdk") ||
               android.os.Build.MODEL.contains("Emulator") ||
               android.os.Build.MODEL.contains("Android SDK built for x86") ||
               android.os.Build.MANUFACTURER.contains("Genymotion") ||
               (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic")) ||
               "google_sdk" == android.os.Build.PRODUCT
    }

    private fun extractHost(url: String): String {
        return url.replace("http://", "").replace("https://", "").split(":")[0]
    }

    private fun isServerReachable(host: String, timeout: Int = 3000): Boolean {
        return try {
            val address = InetAddress.getByName(host)
            address.isReachable(timeout)
        } catch (e: Exception) {
            false
        }
    }

    // Función para configuración manual
    fun setCustomServer(url: String): String {
        Log.i(TAG, "Usando servidor personalizado: $url")
        return url
    }
}
