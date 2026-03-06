package de.asp.vicoreproducer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.asp.vicoreproducer.ui.theme.VicoReproducerTheme

class MainActivity : ComponentActivity() {

    private val viewModel = ChartScreenViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VicoReproducerTheme {
                ChartScreen(viewModel)
            }
        }
    }
}