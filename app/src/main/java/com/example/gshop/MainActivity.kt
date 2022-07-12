package com.example.gshop

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.*
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.MainScreen
import com.example.gshop.ui.RecipeScreen
import com.example.gshop.ui.RecipesListScreen
import com.example.gshop.ui.theme.GShopTheme
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tryLoadSavedState()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContent {
            val state: State by appStore.stateFlow.collectAsState()
            GShopApp(state, appStore.dispatch)
        }
    }

    private fun tryLoadSavedState() {
        val savedJsonState = Database.readJsonState() ?: return
        try {
            val state = Json.decodeFromString(State.serializer(), savedJsonState)
            appStore.dispatch(SetState(state))
        } catch (e: Exception) {
        }
    }
}

@Composable
private fun GShopApp(state: State, dispatch: Dispatch) {
    GShopTheme {
        when (state.currentScreen) {
            is Screen.Main -> MainScreen(state, dispatch)
            is Screen.RecipesList -> RecipesListScreen(state, dispatch)
            is Screen.Recipe -> RecipeScreen(state, dispatch)
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DefaultPreview() {
    GShopApp(state = mockState(), dispatch = {})
}