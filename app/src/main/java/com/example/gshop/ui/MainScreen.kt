package com.example.gshop.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import com.example.gshop.model.*
import com.example.gshop.model.State
import com.example.gshop.redux.Dispatch

@Composable
fun MainScreen(state: State, dispatch: Dispatch) {
    Scaffold(
        topBar = { MainTopBar(dispatch) },
        content = { ShoppingListView(state.shoppingList, dispatch) },
        floatingActionButton = {
            if (!state.itemField.isOpened) {
                FloatingActionButton(onClick = { dispatch(ItemFieldAction.Open) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        },
        bottomBar = {
            if (state.itemField.isOpened) {
                ItemFieldView(state.itemField, dispatch)
            }
        },
    )
}

@Composable
fun ShoppingListView(shoppingList: List<Item>, dispatch: Dispatch) {
    LazyColumn {
        shoppingList.forEach { listItem ->
            item {
                ItemView(listItem, dispatch)
            }
        }
    }

}

@Composable
fun ItemView(item: Item, dispatch: Dispatch) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize(),
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = {
                dispatch(ListAction.ToggleItem(item.id))
            }
        )
        Text(text = item.name)
    }
}

@Composable
fun MainTopBar(dispatch: Dispatch) {
    TopAppBar(
        title = { Text("GShop") },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            IconButton(onClick = {
                dispatch(doStaggeredClearCompleted())
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
            var showMenu by remember { mutableStateOf(false) }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(onClick = {
                    dispatch(doNavigateTo(Screen.RecipesList))
                    showMenu = false
                }) {
                    Text(text = "Recipes")
                }
            }
        }
    )
}

@Composable
fun ItemFieldView(itemField: ItemField, dispatch: Dispatch) {
    val focusRequester = FocusRequester()
    val view = LocalView.current
    LaunchedEffect(view) { focusRequester.requestFocus() }
    OnKeyboardClose(view) { dispatch(ItemFieldAction.Close) }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            value = itemField.text,
            onValueChange = {
                dispatch(ItemFieldAction.SetText(it))
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { dispatch(ItemFieldAction.Submit) }
            ),
            placeholder = { Text("Add a todo...") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
    }
}
