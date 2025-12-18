package com.example.crimewavee.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.data.model.ProductType
import com.example.crimewavee.ui.theme.viewmodel.ClothingViewModel
import com.example.crimewavee.ui.theme.viewmodel.CartViewModel
import com.example.crimewavee.ui.theme.components.ProductImage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    itemId: String,
    clothingViewModel: ClothingViewModel,
    cartViewModel: CartViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val products by clothingViewModel.products
    val selectedProduct = remember(itemId, products) {
        products.find { it.id == itemId } ?: ClothingItem(
            id = itemId,
            name = "Producto no encontrado",
            description = "No se pudo encontrar el producto solicitado.",
            price = 0.0,
            imageUrl = "default_product",
            imageUrls = listOf("default_product", "default_product_2", "default_product_3"),
            category = ProductType.POLERAS,
            isNew = false,
            sizes = listOf("N/A"),
            stock = 0,
            reviewCount = 0
        )
    }

    var selectedSize by remember { mutableStateOf(selectedProduct.sizes.firstOrNull() ?: "") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductImageGallery(
                images = selectedProduct.allImages,
                productName = selectedProduct.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedProduct.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (selectedProduct.isNew) {
                            NewBadge()
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Envíos gratis en todo 🇨🇱 en compras sobre 50k ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$${String.format(Locale.forLanguageTag("es-CL"), "%,.0f", selectedProduct.price)} CLP",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = selectedProduct.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (selectedProduct.category != ProductType.CUADROS) {
                CombinedRecommendationSection()
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información del Producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ProductInfoRow("Categoría", getCategoryText(selectedProduct.category))

                    if (selectedProduct.sizes.isNotEmpty() && selectedProduct.sizes != listOf("N/A")) {
                        Column {
                            Text(
                                text = if (selectedProduct.category == ProductType.CUADROS) "Medidas Disponibles" else "Tallas Disponibles",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedProduct.sizes.take(4).forEach { size ->
                                    FilterChip(
                                        onClick = { selectedSize = size },
                                        label = { Text(size) },
                                        selected = selectedSize == size,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            if (selectedProduct.sizes.size > 4) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedProduct.sizes.drop(4).forEach { size ->
                                        FilterChip(
                                            onClick = { selectedSize = size },
                                            label = { Text(size) },
                                            selected = selectedSize == size,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat(4 - selectedProduct.sizes.drop(4).size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    ProductInfoRow("Stock", "${selectedProduct.stock} unidades")
                }
            }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Acciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (selectedProduct.stock > 0) {
                        Button(
                            onClick = {
                                if (selectedSize.isNotEmpty()) {
                                    if (cartViewModel.isCurrentUserAdmin()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "🔍 Vista previa para administrador: ${selectedProduct.name}",
                                                duration = SnackbarDuration.Long
                                            )
                                        }
                                    } else {
                                        cartViewModel.addToCart(selectedProduct, selectedSize)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "✅ ${selectedProduct.name} agregado al carrito",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedSize.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (cartViewModel.isCurrentUserAdmin()) "Vista Previa - Agregar" else "Agregar al Carrito"
                            )
                        }
                        
                        if (cartViewModel.isCurrentUserAdmin()) {
                            Text(
                                text = "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        
                        if (selectedSize.isEmpty() && selectedProduct.sizes.isNotEmpty() && selectedProduct.sizes != listOf("N/A")) {
                            Text(
                                text = if (selectedProduct.category == ProductType.CUADROS) "* Selecciona una medida" else "* Selecciona una talla",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        ) {
                            Text("Sin Stock")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onNavigateToCart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver el Carrito")
                    }
                }
            }
            Card(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⭐ Reseñas ⭐",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    when (selectedProduct.category) {
                        ProductType.POLERAS -> {
                            Text(
                                text = "— @carolina_m: La tela es suave y se siente de buena calidad.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "— @javier98: Muy buen material, cómodo y resistente.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        ProductType.POLERONES -> {
                            Text(
                                text = "— @danielarojas: Muy cómodo y abrigado, ideal para el invierno.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "— @tomasv: Buena calidad y el color no se destiñe con los lavados.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        ProductType.CUADROS -> {
                            Text(
                                text = "— @valeriarios: Los colores son vivos y el marco tiene buen acabado.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "— @martinpz: Llegó bien embalado y se ve increíble en mi pared.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }


        }
    }
}

@Composable
private fun ProductInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun NewBadge() {
    Surface(
        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
        contentColor = Color(0xFF4CAF50),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "NUEVO",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ProductImageGallery(
    images: List<String>,
    productName: String,
    modifier: Modifier = Modifier
) {
    val safeImages = if (images.isEmpty()) {
        listOf("default_product")
    } else {
        images
    }
    
    val pagerState = rememberPagerState(pageCount = { safeImages.size })
    val coroutineScope = rememberCoroutineScope()
    
    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                if (safeImages.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) { page ->
                        val imageUrl = safeImages.getOrNull(page) ?: "default_product"
                        ProductImage(
                            imageUrl = imageUrl,
                            contentDescription = "$productName - Imagen ${page + 1}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    ProductImage(
                        imageUrl = "default_product",
                        contentDescription = productName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                if (safeImages.size > 1) {
                    // Botón anterior
                    if (pagerState.currentPage > 0) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBackIos,
                                    contentDescription = "Imagen anterior",
                                    modifier = Modifier.padding(8.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    if (pagerState.currentPage < safeImages.size - 1) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Imagen siguiente",
                                    modifier = Modifier.padding(8.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                
                if (safeImages.size > 1) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1}/${safeImages.size}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (safeImages.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(safeImages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Surface(
                        modifier = Modifier
                            .size(if (isSelected) 12.dp else 8.dp)
                            .padding(horizontal = 3.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ) {}
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Desliza para ver más imágenes",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CombinedRecommendationSection() {
    var heightExpanded by remember { mutableStateOf(false) }
    var weightExpanded by remember { mutableStateOf(false) }
    var selectedHeightRange by remember { mutableStateOf<String?>(null) }
    var selectedWeightRange by remember { mutableStateOf<String?>(null) }

    val heightRanges = listOf(
        "1.50cm-1.60cm" to "S",
        "1.61cm-1.73cm" to "M",
        "1.74cm-1.84cm" to "L",
        "1.85cm-1.96cm" to "XL"
    )

    val weightRanges = listOf(
        "45kg-55kg" to "S",
        "56kg-70kg" to "M",
        "71kg-85kg" to "XL",
        "86kg-100kg" to "XL o no comprar"
    )

    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "¿Necesitas ayuda?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Selector de Estatura
            ExposedDropdownMenuBox(
                expanded = heightExpanded,
                onExpandedChange = { heightExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedHeightRange ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona tu altura") },
                    placeholder = { Text("Estatura") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = heightExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = heightExpanded,
                    onDismissRequest = { heightExpanded = false }
                ) {
                    heightRanges.forEach { (range, recommendedSize) ->
                        DropdownMenuItem(
                            text = { Text(range) },
                            onClick = {
                                selectedHeightRange = range
                                heightExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Mostrar recomendación de estatura
            selectedHeightRange?.let { range ->
                val recommendedSize = heightRanges.find { it.first == range }?.second
                if (recommendedSize != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "✨ Usuarios con esa estatura eligieron la talla: $recommendedSize",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50), // Verde
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Peso
            ExposedDropdownMenuBox(
                expanded = weightExpanded,
                onExpandedChange = { weightExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedWeightRange ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona tu peso") },
                    placeholder = { Text("Peso") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = weightExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = weightExpanded,
                    onDismissRequest = { weightExpanded = false }
                ) {
                    weightRanges.forEach { (range, recommendedSize) ->
                        DropdownMenuItem(
                            text = { Text(range) },
                            onClick = {
                                selectedWeightRange = range
                                weightExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Mostrar recomendación de peso
            selectedWeightRange?.let { range ->
                val recommendedSize = weightRanges.find { it.first == range }?.second
                if (recommendedSize != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⚖️ Usuarios con ese peso recomiendan: $recommendedSize",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2196F3), // Azul
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
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



