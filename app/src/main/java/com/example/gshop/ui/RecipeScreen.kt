package com.example.gshop.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gshop.model.store.*
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

@Composable
fun RecipeScreen(state: State, dispatch: Dispatch) {
    BackHandler {
        dispatch(doScreenChangeDispatch(doNavigateBack()))
    }
    val recipe = state.selectedRecipe()
    Scaffold(
        topBar = { RecipeTopBar(recipe, dispatch) },
        content = {
            Text(recipe.contents, modifier = Modifier.padding(16.dp))
        },
    )
}

@Composable
fun RecipeTopBar(recipe: Recipe, dispatch: Dispatch) {
    TopAppBar(
        title = { Text(recipe.title) },
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
