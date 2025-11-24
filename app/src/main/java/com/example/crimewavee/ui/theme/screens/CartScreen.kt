package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.crimewavee.data.model.CartItem
import com.example.crimewavee.data.model.Cart
import com.example.crimewavee.ui.theme.viewmodel.CartViewModel
import com.example.crimewavee.ui.theme.components.ProductImage
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val cartState by cartViewModel.cartState
    val isAdmin = cartViewModel.isCurrentUserAdmin()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (cartState.isEmpty) {
            EmptyCartContent(
                onNavigateBack = onNavigateBack,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            CartContent(
                cart = cartState,
                cartViewModel = cartViewModel,
                isAdmin = isAdmin,
                onNavigateToCheckout = onNavigateToCheckout,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun EmptyCartContent(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¡Agrega algunos productos para comenzar!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar Comprando")
        }
    }
}

@Composable
fun CartContent(
    cart: Cart,
    cartViewModel: CartViewModel,
    isAdmin: Boolean,
    onNavigateToCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cart.items) { cartItem ->
                CartItemCard(
                    cartItem = cartItem,
                    isAdmin = isAdmin,
                    onQuantityChange = { newQuantity ->
                        cartViewModel.updateQuantity(cartItem.id, newQuantity)
                    },
                    onRemove = {
                        cartViewModel.removeFromCart(cartItem.id)
                    }
                )
            }
        }

        CartSummary(
            cart = cart,
            isAdmin = isAdmin,
            onCheckout = onNavigateToCheckout
        )
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    isAdmin: Boolean,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductImage(
                imageUrl = cartItem.clothingItem.imageUrl,
                contentDescription = cartItem.clothingItem.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.clothingItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Talla: ${cartItem.selectedSize}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cartItem.clothingItem.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isAdmin) {
                    Text(
                        text = "Cantidad: ${cartItem.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    
                    Text(
                        text = "(Solo visualización)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity - 1) },
                            enabled = cartItem.quantity > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                        }

                        Text(
                            text = cartItem.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity + 1) }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Aumentar")
                        }
                    }

                    TextButton(
                        onClick = onRemove
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun CartSummary(
    cart: Cart,
    isAdmin: Boolean,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal (${cart.totalItems} items)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cart.subtotal),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Envío ${if (cart.shippingFee == 0.0) "(Gratis)" else ""}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (cart.shippingFee == 0.0) "Gratis" else NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cart.shippingFee),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (cart.shippingFee == 0.0) Color.Green else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "IVA incluido (19%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).format(cart.iva),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
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
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isAdmin) "Vista Previa - Proceder al pago" else "Proceder al pago",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            if (isAdmin) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "* Los administradores ven una vista previa del carrito",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AdminRestrictedContent(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Acceso Restringido",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Los administradores no tienen acceso al carrito de compras.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

