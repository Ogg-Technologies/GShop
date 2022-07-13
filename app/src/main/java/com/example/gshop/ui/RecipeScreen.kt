package com.example.gshop.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.store.State
import com.example.gshop.model.store.doNavigateBack
import com.example.gshop.model.store.doScreenChangeDispatch
import com.example.gshop.model.store.mockState
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

@Composable
fun RecipeScreen(state: State, dispatch: Dispatch) {
    BackHandler {
        dispatch(doScreenChangeDispatch(doNavigateBack()))
    }
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

@Preview(showBackground = true)
@Composable
fun RecipePreview() {
    GShopTheme {
        RecipeScreen(state = mockState(), dispatch = {})
    }
}
