@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.crimewavee.ui.theme.viewmodel.ClothingViewModel
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.data.model.ProductType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ProductManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: ClothingViewModel
) {
    val products by viewModel.products
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ClothingItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStockDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar
        TopAppBar(
            title = { Text("Gestión de Productos") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                // Botón de sincronización
                IconButton(onClick = { viewModel.syncWithServerManually() }) {
                    Icon(Icons.Default.Sync,
                         contentDescription = "Sincronizar con servidor",
                         tint = if (viewModel.needsSync()) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.onSurface)
                }
                // BOTÓN TEMPORAL: Solucionar problema Cuadro Gojo
                IconButton(
                    onClick = {
                        viewModel.solveCuadroGojoProblem()
                        // También mostrar en logs que se ejecutó
                        android.util.Log.d("ProductManagement", "🔧 Solucionando problema Cuadro Gojo...")
                    }
                ) {
                    Icon(Icons.Default.Build,
                         contentDescription = "Solucionar Cuadro Gojo",
                         tint = MaterialTheme.colorScheme.error)
                }
                // Botón de añadir producto
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                }
            }
        )

        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos disponibles")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductManagementCard(
                        product = product,
                        onEdit = {
                            selectedProduct = product
                            showEditDialog = true
                        },
                        onDelete = {
                            selectedProduct = product
                            showDeleteDialog = true
                        },
                        onUpdateStock = {
                            selectedProduct = product
                            showStockDialog = true
                        }
                    )
                }
            }
        }
    }

    // Diálogos
    if (showCreateDialog) {
        CreateProductDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { product ->
                viewModel.createProduct(product)
                showCreateDialog = false
            }
        )
    }

    if (showEditDialog && selectedProduct != null) {
        EditProductDialog(
            product = selectedProduct!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { product ->
                viewModel.updateProductInService(product)
                showEditDialog = false
                selectedProduct = null
            }
        )
    }

    if (showDeleteDialog && selectedProduct != null) {
        DeleteProductDialog(
            product = selectedProduct!!,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteProductWithFeedback(selectedProduct!!.id, selectedProduct!!.name)
                showDeleteDialog = false
                selectedProduct = null
            }
        )
    }

    if (showStockDialog && selectedProduct != null) {
        UpdateStockDialog(
            product = selectedProduct!!,
            onDismiss = { showStockDialog = false },
            onConfirm = { newStock ->
                viewModel.updateStock(selectedProduct!!.id, newStock)
                showStockDialog = false
                selectedProduct = null
            }
        )
    }
}

@Composable
fun ProductManagementCard(
    product: ClothingItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUpdateStock: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Precio: $${String.format("%,.0f", product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Categoría: ${product.category.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Badges
                Row {
                    if (product.isNew) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Nuevo", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    if (product.isFeatured) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Destacado", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Botones de acción
            Column {
                IconButton(onClick = onUpdateStock) {
                    Icon(Icons.Default.Inventory, contentDescription = "Actualizar stock")
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun CreateProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (ClothingItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ProductType.POLERAS) }
    var isNew by remember { mutableStateOf(false) }
    var isFeatured by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Crear Nuevo Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de imagen") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown para categoría (implementación simple)
                Box {
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ProductType.entries.forEach { productType ->
                            DropdownMenuItem(
                                text = { Text(productType.name) },
                                onClick = {
                                    category = productType
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Checkboxes
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isNew,
                        onCheckedChange = { isNew = it }
                    )
                    Text("Producto nuevo")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isFeatured,
                        onCheckedChange = { isFeatured = it }
                    )
                    Text("Producto destacado")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && price.isNotBlank()) {
                                val newProduct = ClothingItem(
                                    id = System.currentTimeMillis().toString(),
                                    name = name,
                                    description = description,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    imageUrl = imageUrl,
                                    imageUrls = listOf(imageUrl),
                                    category = category,
                                    isNew = isNew,
                                    isFeatured = isFeatured,
                                    sizes = when (category) {
                                        ProductType.CUADROS -> listOf("30x39", "40x50", "50x70", "70x81")
                                        else -> listOf("S", "M", "L", "XL")
                                    }
                                )
                                onConfirm(newProduct)
                            }
                        }
                    ) {
                        Text("Crear")
                    }
                }
            }
        }
    }
}

@Composable
fun EditProductDialog(
    product: ClothingItem,
    onDismiss: () -> Unit,
    onConfirm: (ClothingItem) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var imageUrl by remember { mutableStateOf(product.imageUrl) }
    var category by remember { mutableStateOf(product.category) }
    var isNew by remember { mutableStateOf(product.isNew) }
    var isFeatured by remember { mutableStateOf(product.isFeatured) }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Editar Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de imagen") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Checkboxes
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isNew,
                        onCheckedChange = { isNew = it }
                    )
                    Text("Producto nuevo")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isFeatured,
                        onCheckedChange = { isFeatured = it }
                    )
                    Text("Producto destacado")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && price.isNotBlank()) {
                                val updatedProduct = product.copy(
                                    name = name,
                                    description = description,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    imageUrl = imageUrl,
                                    imageUrls = listOf(imageUrl),
                                    category = category,
                                    isNew = isNew,
                                    isFeatured = isFeatured
                                )
                                onConfirm(updatedProduct)
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteProductDialog(
    product: ClothingItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Producto") },
        text = {
            Column {
                Text("¿Estás seguro de que quieres eliminar \"${product.name}\"?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⚠️ Esta acción:",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text("• Eliminará el producto de la base de datos")
                Text("• Lo quitará de la página principal")
                Text("• No se puede deshacer")
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UpdateStockDialog(
    product: ClothingItem,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var stock by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Stock") },
        text = {
            Column {
                Text("Actualizar stock para: ${product.name}")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Cantidad en stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val stockValue = stock.toIntOrNull() ?: 0
                    if (stockValue >= 0) {
                        onConfirm(stockValue)
                    }
                }
            ) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
