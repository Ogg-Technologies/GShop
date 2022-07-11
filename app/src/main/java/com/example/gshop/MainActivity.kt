package com.example.gshop

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.State
import com.example.gshop.model.appStore
import com.example.gshop.model.mockState
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.MainScreen
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


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DefaultPreview() {
    GShopApp(state = mockState(), dispatch = {})
}