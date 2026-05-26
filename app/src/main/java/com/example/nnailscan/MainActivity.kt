package com.example.nnailscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nnailscan.navigation.NailScanNavHost
import com.example.nnailscan.ui.theme.NNailScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NNailScanTheme {
                NailScanNavHost()
            }
        }
    }
}
