package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.crimewavee.data.model.Cart
import com.example.crimewavee.data.model.CartItem
import com.example.crimewavee.ui.theme.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onOrderProcessed: () -> Unit
) {
    val cartState by cartViewModel.cartState
    var isProcessing by remember { mutableStateOf(false) }
    val isAdmin = cartViewModel.isCurrentUserAdmin()

    if (isAdmin) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OrderSummaryCard(cart = cartState)
            }

            item {
                DeliveryInfoCard()
            }

            item {
                PaymentInfoCard()
            }

            item {
                CheckoutSummaryCard(
                    cart = cartState,
                    isProcessing = isProcessing,
                    onConfirmOrder = {
                        isProcessing = true
                        val order = cartViewModel.checkout()
                        isProcessing = false
                        onOrderProcessed()
                    }
                )
            }
        }
    }
}

@Composable
fun OrderSummaryCard(cart: Cart) {
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
                text = "Resumen de la Orden",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            cart.items.forEach { cartItem ->
                CheckoutItemRow(cartItem = cartItem)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CheckoutItemRow(cartItem: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cartItem.clothingItem.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Talla: ${cartItem.selectedSize} • Cant: ${cartItem.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Text(
            text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cartItem.totalPrice),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DeliveryInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Entrega estándar (3-5 días hábiles)",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Dirección: Usar dirección predeterminada",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun PaymentInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información de Pago",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Método de pago: Tarjeta de crédito",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "**** **** **** 1234",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CheckoutSummaryCard(
    cart: Cart,
    isProcessing: Boolean,
    onConfirmOrder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Total del Pedido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Subtotal:", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cart.subtotal),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Envío ${if (cart.shippingFee == 0.0) "(Gratis)" else ""}:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (cart.shippingFee == 0.0) "Gratis" else NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cart.shippingFee),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (cart.shippingFee == 0.0) Color.Green else MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "IVA incluido (19%):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cart.iva),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cart.totalAmount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConfirmOrder,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Procesando...")
                } else {
                    Icon(
                        Icons.Default.ShoppingCartCheckout,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmar Pedido",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
