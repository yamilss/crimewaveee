package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crimewavee.ui.theme.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDetailsScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onUpdateSuccess: () -> Unit,
    currentEmail: String,
    currentPhone: String?
) {
    var phone by remember { mutableStateOf(currentPhone ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authState by authViewModel.authState

    val isValidPhone = phone.isEmpty() || (phone.length == 9 && phone.all { it.isDigit() })
    val isValidNewPassword = newPassword.isEmpty() || newPassword.length >= 4
    val passwordsMatch = newPassword == confirmNewPassword
    val hasRequiredPassword = currentPassword.isNotBlank()

    val isFormValid = hasRequiredPassword && isValidPhone && isValidNewPassword &&
                     (newPassword.isEmpty() || passwordsMatch) &&
                     (phone != currentPhone || newPassword.isNotBlank())

    LaunchedEffect(authState.error) {
        authState.error?.let { error ->
            errorMessage = error
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Detalles") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Edita tus datos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "E-mail *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = currentEmail,
                            onValueChange = {  },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color.Gray,
                                disabledTextColor = Color.Gray
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Teléfono",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { newValue ->
                                if (newValue.length <= 9 && newValue.all { it.isDigit() }) {
                                    phone = newValue
                                    errorMessage = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Contraseña actual *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = {
                                currentPassword = it
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray
                            ),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Nueva contraseña",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray
                            ),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Confirmar nueva contraseña",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = {
                                confirmNewPassword = it
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray
                            ),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {


                        Button(
                            onClick = {
                                try {
                                    errorMessage = null
                                    authViewModel.clearError()

                                    if (!isValidPhone) {
                                        errorMessage = "El teléfono debe tener exactamente 9 dígitos"
                                        return@Button
                                    }

                                    if (!isValidNewPassword) {
                                        errorMessage = "La nueva contraseña debe tener al menos 4 caracteres"
                                        return@Button
                                    }

                                    if (!passwordsMatch) {
                                        errorMessage = "Las nuevas contraseñas no coinciden"
                                        return@Button
                                    }

                                    val success = authViewModel.updateUserDetails(
                                        currentPassword = currentPassword,
                                        newPhone = if (phone.isNotBlank() && phone != currentPhone) phone else null,
                                        newPassword = if (newPassword.isNotBlank()) newPassword else null
                                    )

                                    if (success) {
                                        onUpdateSuccess()
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error inesperado al actualizar los datos"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            enabled = isFormValid,
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF6366F1),
                                                Color(0xFF8B5CF6)
                                            )
                                        ),
                                        shape = RoundedCornerShape(25.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CONFIRMAR",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

