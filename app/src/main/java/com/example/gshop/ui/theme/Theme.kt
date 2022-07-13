package com.example.gshop.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@Composable
fun GShopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = darkColors(
            primary = Purple200,
            primaryVariant = Purple700,
            secondary = Teal200
        ),
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}