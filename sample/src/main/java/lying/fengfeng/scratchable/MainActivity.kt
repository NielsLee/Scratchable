package lying.fengfeng.scratchable

import android.media.ImageReader
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lying.fengfeng.scratchable.ui.theme.ScratchableTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val context = this

        setContent {
            ScratchableTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        var clickCounter by remember { mutableIntStateOf(0)}
                        var isSwitchChecked by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Scratch to see your fortune today...👉",
                                modifier = Modifier.weight(1f)
                            )

                            Scratchable(
                                modifier = Modifier
                            ) {
                                Text(text = "Lucky!🎉")
                            }
                        }

                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "When scratched you will get a...👉",
                                modifier = Modifier.weight(1f)
                            )

                            Scratchable(
                                modifier = Modifier,
                                onFinished = {
                                    Toast.makeText(context, "Right Here!🍞", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            ) {
                                Text(text = "Toast!", modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }

                        Column(
                            modifier = Modifier.padding(20.dp),
                        ) {
                            Text(
                                text = "👇sometimes, you will get other things...",
                            )

                            Scratchable(
                                modifier = Modifier.padding(4.dp),
                                cover = Color.Blue
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Like a button:")
                                    Button(
                                        onClick = {
                                            clickCounter++
                                        },
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) { Text("Click time: $clickCounter") }
                                }
                            }

                            Scratchable(
                                modifier = Modifier.padding(4.dp),
                                cover = Color.Yellow
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Or a switch:")
                                    Switch(
                                        modifier = Modifier.padding(start = 8.dp),
                                        checked = isSwitchChecked,
                                        onCheckedChange = {
                                            isSwitchChecked = !isSwitchChecked
                                        }
                                    )
                                }
                            }

                            Scratchable(
                                modifier = Modifier.padding(4.dp),
                                scratcherRadius = 20.dp,
                                cover = Color.Green,
                                onFinished = {
                                    Toast.makeText(context, "Meow🐱", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("Even a picture with a bobee:")
                                    Image(
                                        painter = painterResource(R.drawable.bobee),
                                        contentDescription = null,
                                        modifier = Modifier.padding(start = 8.dp).scale(0.8f)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}