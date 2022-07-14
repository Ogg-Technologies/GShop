package com.example.gshop.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gshop.model.store.*
import com.example.gshop.redux.Dispatch

@Composable
fun IngredientsSelectionScreen(
    screen: Screen.IngredientsSelection,
    state: State,
    dispatch: Dispatch,
) {
    BackHandler {
        dispatch(doNavigateBack())
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add ingredients") },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = { BackButton(dispatch) }
            )
        },
        content = {
            LazyColumn {
                itemsIndexedWithDividers(screen.selectedIngredients) { index, (ingredient, selected) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = { dispatch(ScreenAction.ToggleIngredient(index)) }
                        )
                        Text(ingredient, fontSize = 20.sp)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                screen.selectedIngredients
                    .filter { (_, selected) -> selected }
                    .map { (ingredient, _) -> ingredient }
                    .forEach { dispatch(doAddItemByName(it)) }
                dispatch(doNavigateHome())
            }) {
                Icon(Icons.Filled.Send, contentDescription = "Finish")
            }
        }
    )
}
