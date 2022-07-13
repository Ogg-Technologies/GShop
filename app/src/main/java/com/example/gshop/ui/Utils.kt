package com.example.gshop.ui

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.gshop.model.store.doNavigateBack
import com.example.gshop.redux.Dispatch

@Composable
fun SimpleTextSpinner(
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
    var wasKeyboardOpened = remember { false }

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
