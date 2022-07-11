package com.example.gshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gshop.model.Increment
import com.example.gshop.model.State
import com.example.gshop.model.appStore
import com.example.gshop.model.doIncrementStream
import com.example.gshop.ui.theme.GShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GShopTheme {
                val state: State by appStore.stateFlow.collectAsState()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        Text(text = "${state.number}")
                        Button(onClick = { appStore.dispatch(Increment) }) {
                            Text(text = "Increment")
                        }
                        Button(onClick = { appStore.dispatch(doIncrementStream(10)) }) {
                            Text(text = "Increment 10!")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GShopTheme {
        Greeting("Android")
    }
}