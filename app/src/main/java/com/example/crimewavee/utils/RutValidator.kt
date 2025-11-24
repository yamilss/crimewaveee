package com.example.crimewavee.utils

/**
 * Validador de RUT chileno
 * Implementa el algoritmo oficial para validar RUTs chilenos
 */
object RutValidator {

    /**
     * Valida un RUT chileno completo
     * @param rut RUT en formato "12345678-9" o "12.345.678-9"
     * @return true si el RUT es válido, false en caso contrario
     */
    fun isValidRut(rut: String): Boolean {
        if (rut.isBlank()) return false

        val cleanRut = rut.replace(".", "").replace("-", "").trim()

        if (cleanRut.length < 2) return false

        val number = cleanRut.substring(0, cleanRut.length - 1)
        val dv = cleanRut.last().toString().uppercase()

        // Verificar que el número contenga solo dígitos
        if (!number.all { it.isDigit() }) return false

        // Verificar que el dígito verificador sea válido
        if (!isValidDv(dv)) return false

        // Calcular el dígito verificador esperado
        val expectedDv = calculateDv(number)

        return dv == expectedDv
    }

    /**
     * Calcula el dígito verificador de un RUT
     * @param rutNumber Número del RUT sin dígito verificador
     * @return Dígito verificador calculado
     */
    fun calculateDv(rutNumber: String): String {
        if (rutNumber.isBlank() || !rutNumber.all { it.isDigit() }) {
            return ""
        }

        var sum = 0
        var multiplier = 2

        // Recorrer el número de derecha a izquierda
        for (i in rutNumber.length - 1 downTo 0) {
            sum += rutNumber[i].digitToInt() * multiplier
            multiplier = if (multiplier == 7) 2 else multiplier + 1
        }

        val remainder = sum % 11
        val dv = 11 - remainder

        return when (dv) {
            10 -> "K"
            11 -> "0"
            else -> dv.toString()
        }
    }

    /**
     * Verifica si un dígito verificador es válido
     * @param dv Dígito verificador a validar
     * @return true si es válido (0-9 o K), false en caso contrario
     */
    private fun isValidDv(dv: String): Boolean {
        return dv.matches(Regex("[0-9K]"))
    }

    /**
     * Formatea un RUT agregando puntos y guión
     * @param rut RUT sin formato
     * @return RUT formateado (ej: "12.345.678-9")
     */
    fun formatRut(rut: String): String {
        if (!isValidRut(rut)) return rut

        val cleanRut = rut.replace(".", "").replace("-", "").trim()
        val number = cleanRut.substring(0, cleanRut.length - 1)
        val dv = cleanRut.last()

        val formattedNumber = number.reversed().chunked(3).joinToString(".").reversed()

        return "$formattedNumber-$dv"
    }

    /**
     * Limpia un RUT removiendo puntos y guiones
     * @param rut RUT con formato
     * @return RUT sin formato
     */
    fun cleanRut(rut: String): String {
        return rut.replace(".", "").replace("-", "").trim().uppercase()
    }

    /**
     * Genera ejemplos de RUTs válidos para testing
     * @return Lista de RUTs válidos
     */
    fun getValidRutExamples(): List<String> {
        return listOf(
            "11111111-1",
            "22222222-2",
            "33333333-3",
            "12345678-5",
            "87654321-8"
        )
    }
}
