package com.example.gshop.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gshop.model.store.*
import com.example.gshop.model.store.State
import com.example.gshop.model.store.sync.SyncNotifications
import com.example.gshop.model.store.sync.syncWithWatch
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
                ItemCreationView(state, dispatch)
            }
        },
        snackbarHost = {
            SnackbarHost(SyncNotifications.snackbarHostState) { data ->
                SyncStatusSnackbar(message = data.message)
            }
        }
    )
}

@Composable
fun SyncStatusSnackbar(message: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        modifier = Modifier
            .padding(16.dp)
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SyncStatusSnackbarPreview() {
    GShopTheme {
        Surface(Modifier.fillMaxSize()) {
            SyncStatusSnackbar("Sync in progress...")
        }
    }
}

@Composable
fun ShoppingListView(shoppingList: List<Item>, dispatch: Dispatch) {
    LazyColumn {
        itemsWithDividers(items = shoppingList) { item ->
            ItemView(item, dispatch)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListViewPreview() {
    GShopTheme {
        Surface {
            ShoppingListView(shoppingList = mockState().shoppingList, dispatch = {})
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
        Spacer(modifier = Modifier.weight(1f))
        SimpleStringOverflowMenu {
            "Delete item" does { dispatch(ListAction.RemoveItem(item.id)) }
            "Edit item" does { dispatch(doEditItem(item.id)) }
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
            SimpleStringOverflowMenu {
                "View recipes" does { dispatch(doNavigateTo(Screen.RecipesList)) }
                "Sync with watch" does { dispatch(syncWithWatch()) }
            }
        }
    )
}

@Composable
fun ItemCreationView(state: State, dispatch: Dispatch) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            ItemCreationTextField(state, dispatch)
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SimpleStringSpinner(
                    items = allCategories,
                    selectedItem = state.itemField.category,
                    onSelectItem = { dispatch(ItemFieldAction.SetCategory(it)) },
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { dispatch(doSubmitItemField(closeOnSubmit = true)) }) {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCreationTextField(state: State, dispatch: Dispatch) {
    val text = state.itemField.text
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = text)) }
    val textFieldValue = textFieldValueState.copy(text = text)

    val focusRequester = FocusRequester()
    val view = LocalView.current
    LaunchedEffect(view) {
        focusRequester.requestFocus()
        textFieldValueState = textFieldValueState.copy(selection = TextRange(text.length))
    }
    OnKeyboardClose(view) { dispatch(ItemFieldAction.Close) }

    TextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValueState = it
            if (text != it.text) {
                dispatch(ItemFieldAction.SetText(it.text))
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { dispatch(doSubmitItemField()) }
        ),
        placeholder = { Text("Add a todo...") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )
}