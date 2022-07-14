package com.example.gshop.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gshop.model.store.*
import com.example.gshop.redux.Dispatch

@Composable
fun RecipeScreen(screen: Screen.Recipe, state: State, dispatch: Dispatch) {
    BackHandler {
        dispatch(doNavigateBack())
    }
    val recipe = state.getRecipe(screen.recipeIndex)
    Scaffold(
        topBar = { RecipeTopBar(recipe, dispatch) },
        content = {
            Text(recipe.contents, modifier = Modifier.padding(16.dp))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val selectedIngredients = recipe.ingredients.map { it to true }
                val newScreen = Screen.IngredientsSelection(screen.recipeIndex, selectedIngredients)
                dispatch(doNavigateTo(newScreen))
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
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
