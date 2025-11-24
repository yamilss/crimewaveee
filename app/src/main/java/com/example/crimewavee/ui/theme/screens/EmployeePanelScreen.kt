package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.data.model.ProductType
import com.example.crimewavee.ui.theme.viewmodel.ClothingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeePanelScreen(
    clothingViewModel: ClothingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddProduct: () -> Unit
) {
    val products by clothingViewModel.products
    var showDeleteDialog by remember { mutableStateOf<ClothingItem?>(null) }
    var showEditDialog by remember { mutableStateOf<ClothingItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Empleados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen del Inventario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard("Total", products.size.toString())
                        StatCard("Poleras", products.count { it.category == ProductType.POLERAS }.toString())
                        StatCard("Polerones", products.count { it.category == ProductType.POLERONES }.toString())
                        StatCard("Cuadros", products.count { it.category == ProductType.CUADROS }.toString())
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Gestión de Productos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay productos disponibles",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(400.dp)
                        ) {
                            items(products) { product ->
                                ProductManagementItem(
                                    product = product,
                                    onEdit = { showEditDialog = product },
                                    onDelete = { showDeleteDialog = product }
                                )
                            }
                        }
                    }
                }
            }
        }

        showDeleteDialog?.let { product ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Está seguro de que desea eliminar '${product.name}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            clothingViewModel.removeProduct(product.id)
                            showDeleteDialog = null
                        }
                    ) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        showEditDialog?.let { product ->
            var newPrice by remember { mutableStateOf(product.price.toString()) }
            var newStock by remember { mutableStateOf(product.stock.toString()) }

            AlertDialog(
                onDismissRequest = { showEditDialog = null },
                title = { Text("Editar Producto") },
                text = {
                    Column {
                        Text("Producto: ${product.name}")
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPrice,
                            onValueChange = { newPrice = it },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newStock,
                            onValueChange = { newStock = it },
                            label = { Text("Stock") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val price = newPrice.toDoubleOrNull() ?: product.price
                            val stock = newStock.toIntOrNull() ?: product.stock
                            clothingViewModel.updateProductLocal(product.copy(price = price, stock = stock))
                            showEditDialog = null
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProductManagementItem(
    product: ClothingItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = getCategoryText(product.category),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${String.format("%,.0f", product.price)} CLP • Stock: ${product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

private fun getCategoryText(category: ProductType): String {
    return when (category) {
        ProductType.POLERAS -> "👕 Poleras"
        ProductType.POLERONES -> "🧥 Polerones"
        ProductType.CUADROS -> "🖼️ Cuadros"
    }
}