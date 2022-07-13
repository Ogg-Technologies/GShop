package com.example.gshop.ui

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gshop.model.store.*
import com.example.gshop.model.store.State
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

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
                ItemFieldView(state, dispatch)
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
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
            .fillMaxWidth()
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = {
                dispatch(ListAction.ToggleItem(item.id))
            },
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = item.name, fontSize = 24.sp)
            Text(text = item.category, fontSize = 12.sp, color = Color.LightGray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemViewPreview() {
    GShopTheme {
        Surface {
            ItemView(item = mockItem("Chicken"), dispatch = { })
        }
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
fun ItemFieldView(state: State, dispatch: Dispatch) {
    val focusRequester = FocusRequester()
    val view = LocalView.current
    LaunchedEffect(view) { focusRequester.requestFocus() }
    OnKeyboardClose(view) { dispatch(ItemFieldAction.Close) }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            TextField(
                value = state.itemField.text,
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
                    .focusRequester(focusRequester)
            )
            SimpleTextSpinner(
                items = state.allCategories(),
                selectedItem = state.itemField.category,
                onSelectItem = { dispatch(ItemFieldAction.SetCategory(it)) },
            )
        }
    }
}
