package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crimewavee.ui.theme.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBillingAddressScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdd: () -> Unit,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState
    val billingAddress = authState.currentUser?.billingAddress
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Direcciones de facturación",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (billingAddress != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Dirección #1",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "PRINCIPAL",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "NOMBRE / APELLIDO",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${billingAddress.nombre} ${billingAddress.apellidos}",
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "IDENTIFICACIÓN TRIBUTARIA",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = billingAddress.rut,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "DIRECCIÓN COMPLETA",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${billingAddress.direccion}, ${billingAddress.codigoPostal}, ${billingAddress.ciudad}, ${billingAddress.region}, ${billingAddress.pais}",
                                fontSize = 14.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "CELL",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = billingAddress.celular.ifEmpty { "No especificado" },
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (billingAddress.instagram.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "RAZÓN SOCIAL / NOMBRE EMPRESA",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = billingAddress.instagram,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToAdd,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF6B7280)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar")
                            }

                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF4444)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Borrar")
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay direcciones guardadas",
                            fontSize = 16.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agrega tu primera dirección de facturación",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (billingAddress == null) {
                Button(
                    onClick = onNavigateToAdd,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7C4DFF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar nueva dirección")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de que desea eliminar esta dirección de facturación?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.deleteBillingAddress()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
