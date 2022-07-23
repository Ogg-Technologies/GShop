package com.example.gshop.ui.utilities

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.gshop.model.store.doNavigateBack
import com.example.gshop.redux.Dispatch

fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun <I> LazyListScope.itemsWithDividers(
    items: List<I>,
    divider: @Composable () -> Unit = { Divider() },
    itemContent: @Composable LazyItemScope.(item: I) -> Unit,
) = itemsIndexedWithDividers(items, divider) { _, item ->
    itemContent(item)
}

fun <I> LazyListScope.itemsIndexedWithDividers(
    items: List<I>,
    divider: @Composable () -> Unit = { Divider() },
    itemContent: @Composable LazyItemScope.(index: Int, item: I) -> Unit,
) {
    items.forEachIndexed { index, item ->
        item {
            if (index != 0) divider()
            itemContent(index, item)
        }
    }
}

@Composable
fun SimpleStringOverflowMenu(
    content: SimpleStringOverflowMenuScope.() -> Unit,
) {
    SimpleStringOverflowMenu(SimpleStringOverflowMenuScope().apply(content).items)
}

class SimpleStringOverflowMenuScope {
    val items = mutableListOf<SimpleStringOverflowMenuItem>()
    infix fun String.does(action: () -> Unit) {
        items.add(SimpleStringOverflowMenuItem(this, action))
    }
}

data class SimpleStringOverflowMenuItem(val title: String, val action: () -> Unit)

@Composable
fun SimpleStringOverflowMenu(
    items: List<SimpleStringOverflowMenuItem>,
) {
    Box {
        var isExpanded by remember { mutableStateOf(false) }
        IconButton(onClick = { isExpanded = !isExpanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            for (item in items) {
                DropdownMenuItem(onClick = {
                    item.action()
                    isExpanded = false
                }) {
                    Text(text = item.title)
                }
            }
        }
    }
}

@Composable
fun SimpleStringSpinner(
    items: List<String>,
    selectedItem: String,
    onSelectItem: (String) -> Unit,
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    Row(
        Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .padding(8.dp),
    ) {
        Text(text = selectedItem)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Filled.ArrowDropDown, "drop down arrow")
    }
    DropdownMenu(
        properties = PopupProperties(focusable = false),
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .requiredHeightIn(min = 0.dp, max = 350.dp),
    ) {
        items.forEach { item ->
            DropdownMenuItem(onClick = {
                onSelectItem(item)
                expanded = false
            }) {
                Text(text = item)
            }
        }
    }
}

@Composable
fun OnKeyboardClose(view: View, lambda: () -> Unit) =
    KeyboardVisibilityChangeListener(view) { isOpened -> if (!isOpened) lambda() }

@Composable
fun KeyboardVisibilityChangeListener(view: View, onChange: (isOpened: Boolean) -> Unit) {
    var wasKeyboardOpened = remember { view.hasKeyboard() }

    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpened = view.hasKeyboard()
            if (isKeyboardOpened != wasKeyboardOpened) {
                // Keyboard visibility has changed
                wasKeyboardOpened = isKeyboardOpened
                onChange(isKeyboardOpened)
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
}

private fun View.hasKeyboard(): Boolean {
    val rect = Rect()
    getWindowVisibleDisplayFrame(rect)
    val screenHeight = rootView.height
    val keypadHeight = screenHeight - rect.bottom
    return keypadHeight > screenHeight * 0.15
}

@Composable
fun BackButton(dispatch: Dispatch) {
    IconButton(
        onClick = { dispatch(doNavigateBack()) },
    ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
    }
}

