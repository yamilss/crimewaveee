package com.example.crimewavee.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource
import com.example.crimewavee.utils.ImageUtils

@Composable
fun ProductImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (imageUrl.startsWith("/")) {

        var bitmap by remember(imageUrl) { mutableStateOf(ImageUtils.getBitmapFromPath(imageUrl)) }

        LaunchedEffect(imageUrl) {
            bitmap = ImageUtils.getBitmapFromPath(imageUrl)
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        } ?: run {

            Image(
                painter = painterResource(id = com.example.crimewavee.R.drawable.satorupolera),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
    } else {

        val resourceId = getImageResource(imageUrl)
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

private fun getImageResource(imageUrl: String): Int {
    return when (imageUrl) {
        "satorupolera" -> com.example.crimewavee.R.drawable.satorupolera
        "satoru" -> com.example.crimewavee.R.drawable.satoru
        "satorupolera3" -> com.example.crimewavee.R.drawable.satorupolera3
        "togahoodie" -> com.example.crimewavee.R.drawable.togahoodie
        "himiko2" -> com.example.crimewavee.R.drawable.himiko2
        "himiko3" -> com.example.crimewavee.R.drawable.himiko3
        "givencuadro" -> com.example.crimewavee.R.drawable.givencuadro
        "given2" -> com.example.crimewavee.R.drawable.given2
        "given3" -> com.example.crimewavee.R.drawable.given3
        "polerongojo" -> com.example.crimewavee.R.drawable.polerongojo
        "logocrimewave" -> com.example.crimewavee.R.drawable.logocrimewave
        "bolsaanime" -> com.example.crimewavee.R.drawable.bolsaanime
        "makima" -> com.example.crimewavee.R.drawable.makima
        "power" -> com.example.crimewavee.R.drawable.power
        "placeholder1" -> com.example.crimewavee.R.drawable.placeholder1
        "placerholder2" -> com.example.crimewavee.R.drawable.placerholder2
        else -> com.example.crimewavee.R.drawable.satorupolera // imagen por defecto
    }
}
