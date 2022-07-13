package com.example.gshop.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun GShopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = darkColors(
            primary = Color(0xFF9575CD),
            primaryVariant = Color(0xFF512DA8),
            secondary = Color(0xFF66BB6A)
        ),
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}