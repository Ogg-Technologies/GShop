package com.example.gshop.ui

import android.content.res.Configuration
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.store.State
import com.example.gshop.model.store.mockState
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

@Composable
fun RecipeScreen(state: State, dispatch: Dispatch) {
    Scaffold(
        topBar = { RecipeTopBar(dispatch) },
        content = {
            Text(text = "Recipe details")
        },
    )
}

@Composable
fun RecipeTopBar(dispatch: Dispatch) {
    TopAppBar(
        title = { Text(text = "Recipe") },
        backgroundColor = MaterialTheme.colors.primary,
        navigationIcon = { BackButton(dispatch) }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun RecipePreview() {
    GShopTheme {
        RecipeScreen(state = mockState(), dispatch = {})
    }
}
