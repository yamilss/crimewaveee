package com.example.crimewavee.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.crimewavee.utils.RutValidator

/**
 * Campo de texto personalizado para RUT chileno con validación automática
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "RUT",
    placeholder: String = "12.345.678-9",
    isError: Boolean = false,
    enabled: Boolean = true,
    autoFormat: Boolean = true,
    showValidation: Boolean = true
) {
    var internalValue by remember { mutableStateOf(value) }
    var isValid by remember { mutableStateOf(true) }

    // Sincronizar el valor interno con el externo
    LaunchedEffect(value) {
        internalValue = value
        isValid = if (value.isBlank()) true else RutValidator.isValidRut(value)
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = internalValue,
            onValueChange = { newValue ->
                // Filtrar solo caracteres válidos para RUT
                val filteredValue = newValue.filter { it.isDigit() || it == '.' || it == '-' || it.uppercase() == "K" }

                if (autoFormat && filteredValue.length <= 12) {
                    // Auto-formatear mientras el usuario escribe
                    val formatted = formatRutInput(filteredValue)
                    internalValue = formatted
                    onValueChange(formatted)
                } else {
                    internalValue = filteredValue.take(12)
                    onValueChange(internalValue)
                }

                // Validar RUT si tiene contenido
                isValid = if (internalValue.isBlank()) true else RutValidator.isValidRut(internalValue)
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            isError = isError || (!isValid && showValidation && internalValue.isNotBlank()),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Mostrar mensaje de validación
        if (showValidation && internalValue.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            ) {
                Text(
                    text = if (isValid) "✓ RUT válido" else "✗ RUT inválido",
                    color = if (isValid) Color.Green else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Formatea la entrada del RUT mientras el usuario escribe
 */
private fun formatRutInput(input: String): String {
    // Remover formato existente
    val clean = input.replace(".", "").replace("-", "")

    if (clean.length <= 1) return clean

    // Separar número y dígito verificador
    val hasVerifier = clean.length > 1 && (clean.last().isLetter() || clean.dropLast(1).all { it.isDigit() })

    if (hasVerifier && clean.length >= 2) {
        val number = clean.dropLast(1)
        val verifier = clean.last().toString().uppercase()

        // Formatear el número con puntos
        val formattedNumber = when {
            number.length <= 3 -> number
            number.length <= 6 -> "${number.dropLast(3)}.${number.takeLast(3)}"
            else -> {
                val millions = number.dropLast(6)
                val thousands = number.drop(millions.length).dropLast(3)
                val hundreds = number.takeLast(3)
                "$millions.$thousands.$hundreds"
            }
        }

        return "$formattedNumber-$verifier"
    }

    return clean
}

/**
 * Composable que muestra información sobre el formato de RUT
 */
@Composable
fun RutFormatHelp(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Formato de RUT Chileno",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• Formato: 12.345.678-9\n" +
                        "• Sin puntos ni guión: 123456789\n" +
                        "• El dígito verificador puede ser 0-9 o K\n" +
                        "• Ejemplo válido: 11.111.111-1",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
