package com.example.gshop.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.store.*
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

@Composable
fun RecipesListScreen(state: State, dispatch: Dispatch) {
    BackHandler {
        dispatch(doScreenChangeDispatch(doNavigateBack()))
    }
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

@Preview(showBackground = true)
@Composable
fun RecipesListPreview() {
    GShopTheme {
        RecipesListScreen(state = mockState(), dispatch = {})
    }
}
