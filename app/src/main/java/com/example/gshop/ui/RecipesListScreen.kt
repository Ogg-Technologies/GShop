package com.example.gshop.ui

import android.content.res.Configuration
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.store.Screen
import com.example.gshop.model.store.State
import com.example.gshop.model.store.doNavigateTo
import com.example.gshop.model.store.mockState
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

@Composable
fun RecipesListScreen(state: State, dispatch: Dispatch) {
    Scaffold(
        topBar = { RecipesListTopBar(dispatch) },
        content = {
            Text(text = "Recipes")
            Button(onClick = { dispatch(doNavigateTo(Screen.Recipe(0))) }) {
                Text(text = "Goto recipe 0")
            }
        },
    )
}

@Composable
fun RecipesListTopBar(dispatch: Dispatch) {
    TopAppBar(
        title = { Text(text = "Recipes") },
        backgroundColor = MaterialTheme.colors.primary,
        navigationIcon = { BackButton(dispatch) }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun RecipesListPreview() {
    GShopTheme {
        RecipesListScreen(state = mockState(), dispatch = {})
    }
}
