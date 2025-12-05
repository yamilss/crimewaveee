package com.example.crimewavee.utils

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidationUtilsTest {

    private lateinit var rutValidator: RutValidator

    @Before
    fun setup() {
        rutValidator = RutValidator
    }

    @Test
    fun `test_validar_rut_chileno_valido_con_formato`() {
        val rutValido = "12.345.678-5"
        val resultado = rutValidator.isValidRut(rutValido)
        assertTrue("El RUT $rutValido debe ser válido", resultado)
    }

    @Test
    fun `test_validar_rut_chileno_invalido_digito_verificador_incorrecto`() {
        val rutInvalido = "12.345.678-9"
        val resultado = rutValidator.isValidRut(rutInvalido)
        assertFalse("El RUT $rutInvalido debe ser inválido", resultado)
    }

    @Test
    fun `test_validar_rut_vacio_debe_retornar_falso`() {
        val rutVacio = ""
        val resultado = rutValidator.isValidRut(rutVacio)
        assertFalse("Un RUT vacío debe ser inválido", resultado)
    }

    @Test
    fun `test_validar_rut_sin_puntos_ni_guiones`() {
        val rutSinFormato = "123456785"
        val resultado = rutValidator.isValidRut(rutSinFormato)
        assertTrue("El RUT $rutSinFormato sin formato debe ser válido", resultado)
    }

    @Test
    fun `test_validar_email_con_formato_valido`() {
        val emailsValidos = listOf(
            "usuario@example.com",
            "test.user@gmail.com",
            "info+tag@company.co.uk"
        )

        val regexEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")

        for (email in emailsValidos) {
            assertTrue("El email $email debería ser válido", regexEmail.matches(email))
        }
    }

    @Test
    fun `test_validar_email_invalido_debe_fallar`() {
        val emailsInvalidos = listOf(
            "sin-arroba.com",
            "@example.com",
            "usuario@",
            "usuario @example.com",
            ""
        )

        val regexEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")

        for (email in emailsInvalidos) {
            assertFalse("El email $email debería ser inválido", regexEmail.matches(email))
        }
    }

    @Test
    fun `test_validar_telefono_chileno_valido`() {
        val telefonoValido = "987654321"
        val esValido = telefonoValido.length == 9 && telefonoValido.all { it.isDigit() }
        assertTrue("El teléfono $telefonoValido debería ser válido", esValido)
    }

    @Test
    fun `test_validar_telefono_con_caracteres_invalidos`() {
        val telefonoInvalido = "98765432a"
        val esValido = telefonoInvalido.length == 9 && telefonoInvalido.all { it.isDigit() }
        assertFalse("El teléfono $telefonoInvalido debería ser inválido", esValido)
    }

    @Test
    fun `test_validar_contrasena_fortaleza_minima`() {
        val contrasenaDebil = "123"
        val contrasenaValida = "MiPassword123"

        val esDebilValida = contrasenaDebil.length >= 6
        val esValidaValida = contrasenaValida.length >= 6

        assertFalse("La contraseña débil no debería cumplir requisitos", esDebilValida)
        assertTrue("La contraseña válida debería cumplir requisitos", esValidaValida)
    }

    @Test
    fun `test_validar_rut_con_caracteres_especiales_invalidos`() {
        val rutInvalido = "12.345.678-A"
        val resultado = rutValidator.isValidRut(rutInvalido)
        assertFalse("El RUT $rutInvalido con letra A debe ser inválido", resultado)
    }

    @Test
    fun `test_calcular_digito_verificador_correcto`() {
        val rutNumero = "12345678"
        val dvCalculado = rutValidator.calculateDv(rutNumero)
        assertEquals("El dígito verificador calculado debe ser 5", "5", dvCalculado)
    }

    @Test
    fun `test_formatear_rut_correctamente`() {
        val rutSinFormato = "123456785"
        val rutFormateado = rutValidator.formatRut(rutSinFormato)
        assertTrue("El RUT debe tener puntos y guión", rutFormateado.contains(".") && rutFormateado.contains("-"))
        assertTrue("El RUT debe terminar con el dígito verificador", rutFormateado.endsWith("5"))
    }
}

