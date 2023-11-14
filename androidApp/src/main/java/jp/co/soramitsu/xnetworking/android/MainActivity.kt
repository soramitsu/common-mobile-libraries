package jp.co.soramitsu.xnetworking.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.xnetworking.basic.common.platform
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DepBuilder.build(applicationContext)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
private fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Button(
            onClick = {
                GlobalScope.launch {
                    try {
                        Log.e("foxxx", "r start btn 1")
                        val r = DepBuilder.networkService.getAssetsInfo()
                        Log.e("foxxx", "r = ${r}")
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t= ${t.localizedMessage}")
                    }
                }
            },
            content = {
                Text(text = "btn1")
            },
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                GlobalScope.launch {
                    Log.e("foxxx", "r start btn 2")
                    try {
                        val r = DepBuilder.networkService.getHistorySora(1) {
                            true
                        }!!
                        Log.e(
                            "foxxx",
                            "r = ${r.endReached} ${r.page} ${r.items.size} ${r.errorMessage}"
                        )
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t= ${t.localizedMessage}")
                    }
                }
            },
            content = {
                Text(text = "btn2")
            },
        )
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                GlobalScope.launch {
                    Log.e("foxxx", "r start btn 3")
                    try {
                        val r = DepBuilder.networkService.getSoraConfig()
                        Log.e("foxxx", "r = $r")
                    } catch (t: Throwable) {
                        Log.e("foxxx", "t = ${t.localizedMessage}")
                    }
                }
            },
            content = {
                Text(text = "btn3")
            },
        )
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}