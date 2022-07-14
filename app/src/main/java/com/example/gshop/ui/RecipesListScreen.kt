package com.example.gshop.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gshop.App
import com.example.gshop.model.store.*
import com.example.gshop.redux.Dispatch
import com.example.gshop.ui.theme.GShopTheme

@Composable
fun RecipesListScreen(state: State, dispatch: Dispatch) {
    BackHandler {
        dispatch(doScreenChangeDispatch(doNavigateBack()))
    }
    Scaffold(
        topBar = { RecipesListTopBar(dispatch) },
        content = {
            when (state.recipesData) {
                is RecipesData.NoFolderSelected -> NoFolderSelectedView(dispatch)
                is RecipesData.Initialized -> RecipesListView(state.recipesData, dispatch)
            }
        },
    )
}

@Composable
fun RecipesListView(recipesData: RecipesData.Initialized, dispatch: Dispatch) {
    LazyColumn {
        itemsIndexedWithDividers(recipesData.recipes) { index, recipe ->
            Text(
                text = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        dispatch(doScreenChangeDispatch(NavAction.Goto(Screen.Recipe(index))))
                    }
                    .padding(16.dp),
            )
        }
    }

}

@Composable
fun NoFolderSelectedView(dispatch: Dispatch) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                println("Result code was not OK")
                return@rememberLauncherForActivityResult
            }
            val uri = result.data?.data
            if (uri == null) {
                println("Invalid result received. Result: $result")
                return@rememberLauncherForActivityResult
            }
            // Make the permission to read files from that folder persist, meaning that the
            // permission will still be valid after restarting the app.
            App.context.contentResolver.takePersistableUriPermission(uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
            dispatch(doSelectRecipesFolder(uri))
        }

    fun openFolderSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        launcher.launch(intent)
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "No recipes folder has been selected")
        Button(
            onClick = {
                openFolderSelection()
            },
        ) {
            Text(text = "Select folder")
        }
    }
}

@Composable
fun RecipesListTopBar(dispatch: Dispatch) {
    TopAppBar(
        title = { Text(text = "Recipes") },
        backgroundColor = MaterialTheme.colors.primary,
        navigationIcon = { BackButton(dispatch) },
        actions = {
            SimpleStringOverflowMenu {
                "Reset folder selection" does { dispatch(SetRecipesData(RecipesData.NoFolderSelected)) }
                "Refresh recipes" does { dispatch(doRefreshRecipes()) }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun RecipesListPreview() {
    GShopTheme {
        RecipesListScreen(state = mockState(), dispatch = {})
    }
}
