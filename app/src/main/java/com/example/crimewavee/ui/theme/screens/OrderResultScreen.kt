package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.crimewavee.data.model.Order
import com.example.crimewavee.data.model.OrderStatus
import com.example.crimewavee.ui.theme.viewmodel.CartViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderResultScreen(
    cartViewModel: CartViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val lastOrder by cartViewModel.lastOrder
    val isAdmin = cartViewModel.isCurrentUserAdmin()

    if (isAdmin) {
        LaunchedEffect(Unit) {
            onNavigateToHome()
        }
        return
    }

    when {
        lastOrder?.status == OrderStatus.APPROVED -> {
            OrderSuccessScreen(
                order = lastOrder!!,
                onNavigateToHome = onNavigateToHome
            )
        }
        lastOrder?.status == OrderStatus.REJECTED -> {
            OrderRejectedScreen(
                order = lastOrder!!,
                onNavigateToCart = onNavigateToCart,
                onNavigateToHome = onNavigateToHome
            )
        }
        else -> {
            
            ErrorScreen(onNavigateToHome = onNavigateToHome)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessScreen(
    order: Order,
    onNavigateToHome: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¡Compra Exitosa!") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                
                Card(
                    modifier = Modifier.size(120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Green.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Green
                        )
                    }
                }
            }

            item {
                Text(
                    text = "¡Pedido Confirmado!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green,
                    textAlign = TextAlign.Center
                )
            }

            item {
                Text(
                    text = "Tu pedido ha sido procesado exitosamente y pronto recibirás un email de confirmación.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            item {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Detalles del Pedido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Número de Pedido:", fontWeight = FontWeight.Medium)
                            Text(text = "ORDEN N-12345")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Fecha:", fontWeight = FontWeight.Medium)
                            Text(text = dateFormat.format(Date(order.timestamp)))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total:", fontWeight = FontWeight.Medium)
                            Text(
                                text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(order.totalAmount),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Productos (${order.items.size}):",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            items(order.items) { cartItem ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cartItem.clothingItem.name,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Talla: ${cartItem.selectedSize} • Cantidad: ${cartItem.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cartItem.totalPrice),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Información de Entrega",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• Tu pedido será procesado en 1-2 días hábiles",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Tiempo de entrega estimado: 3-5 días hábiles",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• Recibirás un código de seguimiento por email",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continuar Comprando")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRejectedScreen(
    order: Order,
    onNavigateToCart: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compra Rechazada") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        
            Card(
                modifier = Modifier.size(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.1f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Compra Rechazada",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Motivo del Rechazo:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = order.rejectionReason ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Qué puedes hacer?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "• Revisa tu carrito y elimina algunos productos",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Reduce las cantidades para disminuir el total",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• El límite máximo por compra es $100,000 CLP",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Puedes hacer compras separadas si necesitas más productos",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNavigateToCart,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Revisar Carrito")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver al Inicio")
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(onNavigateToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ocurrió un error",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onNavigateToHome) {
            Text("Volver al Inicio")
        }
    }
}
