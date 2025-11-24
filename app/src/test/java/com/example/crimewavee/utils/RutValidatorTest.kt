package com.example.crimewavee.utils

import com.example.crimewavee.utils.RutValidator
import org.junit.Test
import org.junit.Assert.*

/**
 * Test unitario para RutValidator
 * Verifica el correcto funcionamiento del validador de RUT chileno
 */
class RutValidatorTest {

    @Test
    fun `isValidRut should return true for valid RUTs`() {
        val validRuts = listOf(
            "11.111.111-1",
            "22.222.222-2",
            "12.345.678-5",
            "87.654.321-8",
            "11111111-1",  // Sin formato
            "123456785",   // Sin guión
            "12345678K"    // Con K
        )

        validRuts.forEach { rut ->
            assertTrue("RUT $rut debería ser válido", RutValidator.isValidRut(rut))
        }
    }

    @Test
    fun `isValidRut should return false for invalid RUTs`() {
        val invalidRuts = listOf(
            "11.111.111-2",  // Dígito verificador incorrecto
            "22.222.222-3",  // Dígito verificador incorrecto
            "12.345.678-0",  // Dígito verificador incorrecto
            "123456789",     // Sin dígito verificador
            "abc.def.ghi-1", // Letras en el número
            "11.111.111-Z",  // Dígito verificador inválido
            "",              // Vacío
            "1-1",           // Muy corto
            "111111111111-1" // Muy largo
        )

        invalidRuts.forEach { rut ->
            assertFalse("RUT $rut debería ser inválido", RutValidator.isValidRut(rut))
        }
    }

    @Test
    fun `calculateDv should return correct verification digit`() {
        val testCases = mapOf(
            "11111111" to "1",
            "22222222" to "2",
            "12345678" to "5",
            "87654321" to "8",
            "24965471" to "K",
            "24965472" to "0"
        )

        testCases.forEach { (number, expectedDv) ->
            val actualDv = RutValidator.calculateDv(number)
            assertEquals("Dígito verificador para $number", expectedDv, actualDv)
        }
    }

    @Test
    fun `calculateDv should return empty string for invalid input`() {
        val invalidInputs = listOf(
            "",
            "abc",
            "123abc",
            "12.345.678"
        )

        invalidInputs.forEach { input ->
            assertEquals("Input inválido '$input' debería retornar string vacío",
                        "", RutValidator.calculateDv(input))
        }
    }

    @Test
    fun `formatRut should format RUT correctly`() {
        val testCases = mapOf(
            "111111111" to "11.111.111-1",
            "111111112" to "111111112", // Inválido, no se formatea
            "12345678-5" to "12.345.678-5",
            "12.345.678-5" to "12.345.678-5" // Ya formateado
        )

        testCases.forEach { (input, expected) ->
            assertEquals("Formateo de '$input'", expected, RutValidator.formatRut(input))
        }
    }

    @Test
    fun `cleanRut should remove formatting`() {
        val testCases = mapOf(
            "12.345.678-9" to "123456789",
            "12345678-9" to "123456789",
            "12.345.678K" to "12345678K",
            "  12.345.678-9  " to "123456789" // Con espacios
        )

        testCases.forEach { (input, expected) ->
            assertEquals("Limpieza de '$input'", expected, RutValidator.cleanRut(input))
        }
    }

    @Test
    fun `getValidRutExamples should return valid RUTs`() {
        val examples = RutValidator.getValidRutExamples()

        assertTrue("Debería retornar al menos un ejemplo", examples.isNotEmpty())

        examples.forEach { rut ->
            assertTrue("Ejemplo $rut debería ser válido", RutValidator.isValidRut(rut))
        }
    }

    @Test
    fun `edge cases should be handled correctly`() {
        // RUT con K mayúscula y minúscula
        assertTrue("RUT con K mayúscula", RutValidator.isValidRut("24965471-K"))
        assertTrue("RUT con k minúscula", RutValidator.isValidRut("24965471-k"))

        // RUT mínimo válido
        assertTrue("RUT mínimo", RutValidator.isValidRut("1000000-K"))

        // Espacios en blanco
        assertFalse("Solo espacios", RutValidator.isValidRut("   "))

        // Un solo carácter
        assertFalse("Un carácter", RutValidator.isValidRut("1"))
    }
}
