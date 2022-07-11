package com.example.gshop

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.*
import com.example.gshop.model.State
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            val state: State by appStore.stateFlow.collectAsState()
            GShopApp(state, appStore.dispatch)
        }
    }
}

@Composable
private fun GShopApp(state: State, dispatch: Dispatch) {
    GShopTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            // Display the correct screen based on the state
            MainScreen(state, dispatch)
        }
    }
}

@Composable
fun MainScreen(state: State, dispatch: Dispatch) {
    Scaffold(
        topBar = { MainTopBar() },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Button(onClick = {
                    dispatch(
                        ListAction.AddItems(
                            listOf(
                                Item(
                                    "Item 1",
                                    false,
                                    1,
                                    "other"
                                )
                            )
                        )
                    )
                }) {
                    Text(text = "Add item")
                }
                ShoppingListView(state.shoppingList, dispatch)
            }
        },
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
fun MainTopBar() {
    TopAppBar(
        title = { Text("GShop") },
        backgroundColor = MaterialTheme.colors.primary,
    )
}

@Composable
fun ItemFieldView(itemField: ItemField, dispatch: Dispatch) {

    val focusRequester = FocusRequester()
    val view = LocalView.current
    LaunchedEffect(view) {
        focusRequester.requestFocus()
    }

    val onKeyboardDismiss = {
        dispatch(ItemFieldAction.Close)
    }

    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight <= screenHeight * 0.15) {
                onKeyboardDismiss()
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DefaultPreview() {
    GShopApp(state = mockState(), dispatch = {})
}