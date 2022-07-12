package com.example.gshop.ui

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.example.gshop.model.doNavigateBack
import com.example.gshop.redux.Dispatch

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

