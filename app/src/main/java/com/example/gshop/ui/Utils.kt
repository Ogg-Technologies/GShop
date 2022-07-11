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
import com.example.gshop.model.doNavigateBack
import com.example.gshop.redux.Dispatch

@Composable
fun OnKeyboardClose(view: View, block: () -> Unit) {
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight <= screenHeight * 0.15) {
                block()
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
}

@Composable
fun BackButton(dispatch: Dispatch) {
    IconButton(
        onClick = { dispatch(doNavigateBack()) },
    ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
    }
}

