package com.example.crimewavee.ui.theme.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crimewavee.data.model.ProductType
import com.example.crimewavee.data.model.ClothingItem
import com.example.crimewavee.ui.theme.viewmodel.ClothingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    clothingViewModel: ClothingViewModel,
    onNavigateBack: () -> Unit,
    onReportSubmitted: () -> Unit
) {
    val context = LocalContext.current
    var productName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ProductType.POLERAS) }
    var expanded by remember { mutableStateOf(false) }
    var selectedSize by remember { mutableStateOf("S") }
    var sizeExpanded by remember { mutableStateOf(false) }
    var sizeQuantity by remember { mutableStateOf("1") }
    var selectedSizes by remember { mutableStateOf(mutableMapOf<String, Int>()) }

    var imageUrl by remember { mutableStateOf("") }



    val availableOptions = remember(selectedCategory) {
        when (selectedCategory) {
            ProductType.CUADROS -> listOf("30x39", "40x50", "50x70", "70x81")
            else -> listOf("XS", "S", "M", "L", "XL", "XXL")
        }
    }

    LaunchedEffect(selectedCategory) {
        selectedSizes.clear()
        selectedSize = availableOptions.first()
    }

    val isValidPrice = price.toDoubleOrNull()?.let { it >= 15000 } ?: false
    val isValidStock = stock.toIntOrNull()?.let { it >= 0 } ?: false
    val isValidSizeQuantity = sizeQuantity.toIntOrNull()?.let { it > 0 } ?: false

    // Validación de URL de imagen
    val isValidImageUrl = imageUrl.isBlank() || run {
        val url = imageUrl.trim().lowercase()
        (url.startsWith("http://") || url.startsWith("https://")) &&
        (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png") ||
         url.contains(".gif") || url.contains(".webp") || url.contains(".bmp") ||
         url.contains("imgur.com") || url.contains("cdn.") || url.contains("cloudinary.com") ||
         url.contains("amazonaws.com") || url.contains("googleusercontent.com"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8EAF6),
                        Color(0xFFF3E5F5)
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2196F3),
                            Color(0xFF3F51B5)
                        )
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))


                Spacer(modifier = Modifier.width(30.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Vista Previa - Agregar Producto",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Esta función es solo visual - no se agregan productos realmente",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }


                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFF3E5F5)
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agregando como: Administrador(Administrador)",
                    color = Color(0xFF1976D2),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nombre del Producto *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            placeholder = { Text("Ej. Polera Anime Naruto", color = Color.Gray.copy(alpha = 0.6f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                }

                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Precio * (Min: $15,000)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { newPrice ->
                                if (newPrice.all { it.isDigit() || it == '.' }) {
                                    price = newPrice
                                }
                            },
                            placeholder = { Text("15000", color = Color.Gray.copy(alpha = 0.6f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = price.isNotEmpty() && !isValidPrice,
                            supportingText = if (price.isNotEmpty() && !isValidPrice) {
                                { Text("El precio mínimo es $15,000 CLP", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isValidPrice || price.isEmpty()) Color(0xFF2196F3) else MaterialTheme.colorScheme.error,
                                unfocusedBorderColor = if (isValidPrice || price.isEmpty()) Color.Gray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Categoría
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFF2196F3), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Categoría *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = getCategoryDisplayText(selectedCategory),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                                    .background(Color.White),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2196F3),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                ProductType.entries.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(getCategoryDisplayText(category)) },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Color(0xFFFF9800), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stock Total",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { newStock ->
                                if (newStock.all { it.isDigit() } && (newStock.isEmpty() || newStock.toIntOrNull() != null)) {
                                    stock = newStock
                                }
                            },
                            placeholder = { Text("50", color = Color.Gray.copy(alpha = 0.6f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = stock.isNotEmpty() && !isValidStock,
                            supportingText = if (stock.isNotEmpty() && !isValidStock) {
                                { Text("El stock no puede ser negativo", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isValidStock || stock.isEmpty()) Color(0xFF2196F3) else MaterialTheme.colorScheme.error,
                                unfocusedBorderColor = if (isValidStock || stock.isEmpty()) Color.Gray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color(0xFF9C27B0), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Descripción *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Describe el producto, sus características, materiales, etc", color = Color.Gray.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(Color.White),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2196F3),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color(0xFFE91E63), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedCategory == ProductType.CUADROS) "Medidas y Cantidades" else "Tallas y Cantidades",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = sizeExpanded,
                            onExpandedChange = { sizeExpanded = !sizeExpanded },
                            modifier = Modifier
                                .width(80.dp)
                                .height(56.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedSize,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sizeExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxSize(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2196F3),
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = sizeExpanded,
                                onDismissRequest = { sizeExpanded = false }
                            ) {
                                availableOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedSize = option
                                            sizeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = sizeQuantity,
                            onValueChange = { newQuantity ->
                                if (newQuantity.all { it.isDigit() } && (newQuantity.isEmpty() || newQuantity.toIntOrNull()?.let { it > 0 } == true)) {
                                    sizeQuantity = newQuantity
                                } else if (newQuantity.isEmpty()) {
                                    sizeQuantity = newQuantity
                                }
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(56.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = sizeQuantity.isNotEmpty() && !isValidSizeQuantity,
                            placeholder = { Text("1", color = Color.Gray.copy(alpha = 0.6f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isValidSizeQuantity || sizeQuantity.isEmpty()) Color(0xFF2196F3) else MaterialTheme.colorScheme.error,
                                unfocusedBorderColor = if (isValidSizeQuantity || sizeQuantity.isEmpty()) Color.Gray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Button(
                            onClick = {
                                val quantity = sizeQuantity.toIntOrNull()
                                if (quantity != null && quantity > 0 && selectedSize.isNotEmpty()) {
                                    selectedSizes = selectedSizes.toMutableMap().apply {
                                        this[selectedSize] = quantity
                                    }
                                    sizeQuantity = "1" 
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .width(56.dp),
                            enabled = isValidSizeQuantity && selectedSize.isNotEmpty(),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    if (selectedSizes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedCategory == ProductType.CUADROS) "Medidas agregadas:" else "Tallas agregadas:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedSizes.forEach { (size, quantity) ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                                    modifier = Modifier.wrapContentSize()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "$size: $quantity",
                                            fontSize = 12.sp,
                                            color = Color(0xFF1976D2)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                selectedSizes = selectedSizes.toMutableMap().apply {
                                                    remove(size)
                                                }
                                            },
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Text("×", color = Color(0xFF1976D2), fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color(0xFF607D8B), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "URL de la Imagen",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF424242)
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("URL de la imagen") },
                            placeholder = { Text("https://ejemplo.com/imagen.jpg") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = imageUrl.isNotBlank() && !isValidImageUrl,
                            supportingText = if (imageUrl.isNotBlank() && !isValidImageUrl) {
                                { Text("URL inválida. Debe comenzar con http:// o https:// y contener una extensión de imagen válida (.jpg, .png, etc.)", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isValidImageUrl) Color(0xFF2196F3) else MaterialTheme.colorScheme.error,
                                unfocusedBorderColor = if (isValidImageUrl) Color.Gray.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Introduce la URL completa de una imagen (debe comenzar con http:// o https://). Ejemplos: https://ejemplo.com/imagen.jpg o https://imgur.com/foto.png. Si no introduces ninguna URL, se usará una imagen predeterminada según la categoría.",
                            color = Color.Gray.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )

                        if (imageUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓ URL configurada",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(
                                    onClick = { imageUrl = "" }
                                ) {
                                    Text(
                                        text = "Limpiar",
                                        color = Color(0xFFFF5722),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF616161)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        Color(0xFF616161).copy(alpha = 0.5f)
                    )
                ) {
                    Text("CANCELAR", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        try {
                            val finalPrice = price.toDoubleOrNull()
                            val finalStock = stock.toIntOrNull() ?: 10

                            if (finalPrice == null || finalPrice < 15000) {
                                android.widget.Toast.makeText(
                                    context,
                                    "❌ El precio mínimo debe ser $15,000 CLP",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            if (finalStock < 0) {
                                android.widget.Toast.makeText(
                                    context,
                                    "❌ El stock no puede ser negativo",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            // FEEDBACK VISUAL: Creando producto
                            android.widget.Toast.makeText(
                                context,
                                "🚀 Creando producto: ${productName.trim()}...",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()

                            val productImageUrl = if (imageUrl.isNotBlank()) {
                                imageUrl.trim()
                            } else {
                                when (selectedCategory) {
                                    ProductType.POLERAS -> "satorupolera"
                                    ProductType.POLERONES -> "togahoodie"
                                    ProductType.CUADROS -> "givencuadro"
                                }
                            }

                            val newProduct = ClothingItem(
                                id = clothingViewModel.generateNextProductId(),
                                name = productName.trim(),
                                description = description.trim(),
                                price = finalPrice,
                                imageUrl = productImageUrl,
                                category = selectedCategory,
                                isNew = true,
                                isFeatured = false,
                                sizes = if (selectedSizes.isNotEmpty()) selectedSizes.keys.toList() else availableOptions.take(1),
                                stock = finalStock
                            )

                            // CREAR EL PRODUCTO CON LOGS DETALLADOS
                            android.util.Log.d("ReportScreen", "🚀 === CREANDO PRODUCTO ===")
                            android.util.Log.d("ReportScreen", "📦 Nombre: ${newProduct.name}")
                            android.util.Log.d("ReportScreen", "💰 Precio: ${newProduct.price}")
                            android.util.Log.d("ReportScreen", "📊 Stock: ${newProduct.stock}")
                            android.util.Log.d("ReportScreen", "🖼️ Imagen: ${newProduct.imageUrl}")

                            clothingViewModel.createProductWithFeedback(newProduct) { success, message ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (success) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "✅ ¡PRODUCTO CREADO EXITOSAMENTE EN SERVIDOR! Verificar en Postman.",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            "❌ ERROR CREANDO PRODUCTO: $message",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                            onReportSubmitted()
                        } catch (e: Exception) {
                            android.widget.Toast.makeText(
                                context,
                                "❌ Error: ${e.message}",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                            android.util.Log.e("ReportScreen", "❌ Error creando producto: ${e.message}")
                            return@Button
                        }
                    },
                    modifier = Modifier
                        .height(56.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    enabled = productName.trim().isNotBlank() &&
                             description.trim().isNotBlank() &&
                             price.isNotBlank() &&
                             isValidPrice &&
                             isValidStock &&
                             isValidImageUrl &&
                             productName.trim().length <= 100 &&
                             description.trim().length <= 500,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Vista Previa - Agregar Producto",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        android.widget.Toast.makeText(
                            context,
                            "🧪 Probando datos mínimos...",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        clothingViewModel.testMinimalCreation { success, message ->
                            CoroutineScope(Dispatchers.Main).launch {
                                if (success) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "✅ DATOS MÍNIMOS OK: $message",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    android.widget.Toast.makeText(
                                        context,
                                        "❌ DATOS MÍNIMOS FALLAN: $message",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF607D8B)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🧪 DATOS MÍNIMOS", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        // CREAR PRODUCTO DE PRUEBA SIMPLE
                        android.widget.Toast.makeText(
                            context,
                            "🧪 Creando producto de prueba...",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        val testProduct = ClothingItem(
                            id = "test_${System.currentTimeMillis()}",
                            name = "PRODUCTO DE PRUEBA DIRECTO",
                            description = "Este es un producto creado para probar la conexión directa con la API",
                            price = 20000.0,
                            imageUrl = "https://via.placeholder.com/300x300.jpg",
                            category = ProductType.POLERAS,
                            isNew = true,
                            isFeatured = false,
                            sizes = listOf("S", "M", "L"),
                            stock = 5
                        )

                        android.util.Log.d("ReportScreen", "🧪 === CREANDO PRODUCTO DE PRUEBA ===")
                        android.util.Log.d("ReportScreen", "📦 ${testProduct.name}")
                        android.util.Log.d("ReportScreen", "💰 Precio: ${testProduct.price}")
                        android.util.Log.d("ReportScreen", "📊 Stock: ${testProduct.stock}")

                        clothingViewModel.createProductWithFeedback(testProduct) { success, message ->
                            CoroutineScope(Dispatchers.Main).launch {
                                if (success) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "✅ PRODUCTO CREADO EN SERVIDOR - Visible en Postman",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    android.widget.Toast.makeText(
                                        context,
                                        "❌ FALLÓ LA CREACIÓN - $message",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🧪 CREAR PRODUCTO DE PRUEBA", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        // BOTÓN DE PRUEBA CON FEEDBACK VISUAL DIRECTO
                        android.widget.Toast.makeText(
                            context,
                            "🧪 Probando conexión con servidor...",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        clothingViewModel.testServerConnectionWithFeedback { success, message ->
                            CoroutineScope(Dispatchers.Main).launch {
                                if (success) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "✅ SERVIDOR FUNCIONANDO - Puedes crear productos",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    android.widget.Toast.makeText(
                                        context,
                                        "❌ SERVIDOR NO DISPONIBLE - $message",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        android.util.Log.d("ReportScreen", "🧪 Probando conexión API antes de crear producto...")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🧪 PROBAR CONEXIÓN", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ESTADO PARA EL DIÁLOGO DE ERROR
            var showErrorDialog by remember { mutableStateOf(false) }
            var errorTitle by remember { mutableStateOf("") }
            var errorMessage by remember { mutableStateOf("") }

            // BOTÓN DE DIAGNÓSTICO AVANZADO CON DIÁLOGO
            Button(
                onClick = {
                    android.widget.Toast.makeText(
                        context,
                        "🔍 Ejecutando diagnóstico completo...",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

                    clothingViewModel.runAdvancedDiagnosticWithDialog(
                        onSuccess = { mensaje ->
                            CoroutineScope(Dispatchers.Main).launch {
                                android.widget.Toast.makeText(
                                    context,
                                    mensaje,
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        onError = { titulo, mensaje ->
                            errorTitle = titulo
                            errorMessage = mensaje
                            showErrorDialog = true
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9C27B0)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🔍 DIAGNÓSTICO COMPLETO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // DIÁLOGO DE ERROR DETALLADO
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = {
                        Text(
                            text = errorTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    },
                    text = {
                        Text(
                            text = errorMessage,
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showErrorDialog = false }
                        ) {
                            Text("ENTENDIDO", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showErrorDialog = false
                                // Copiar error a clipboard si es posible
                                android.util.Log.d("ReportScreen", "Error copiable: $errorMessage")
                            }
                        ) {
                            Text("VER LOGS")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }


}

private fun getCategoryDisplayText(category: ProductType): String {
    return when (category) {
        ProductType.POLERAS -> "Poleras"
        ProductType.POLERONES -> "Polerones"
        ProductType.CUADROS -> "Cuadros"
    }
}
