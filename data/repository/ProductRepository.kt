    /**
     * Función de prueba de conectividad con el servidor
     */
    suspend fun testServerConnection(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ProductRepository", "🌐 Probando conectividad con servidor...")
                Log.d("ProductRepository", "🔗 URL base: $baseUrl")

                val response = apiService.getAllProducts()
                Log.d("ProductRepository", "✅ Servidor responde correctamente - ${response.size} productos")

                Result.success(true)
            } catch (e: Exception) {
                Log.e("ProductRepository", "❌ Error de conectividad: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // ...existing code...
