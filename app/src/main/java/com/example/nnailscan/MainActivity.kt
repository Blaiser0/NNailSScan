package com.example.nnailscan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nnailscan.navigation.NailScanNavHost
import com.example.nnailscan.navigation.PasswordResetLinkHandler
import com.example.nnailscan.ui.theme.NNailScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handlePasswordResetLink(intent)
        setContent {
            NNailScanTheme {
                NailScanNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePasswordResetLink(intent)
    }

    private fun handlePasswordResetLink(intent: Intent?) {
        PasswordResetLinkHandler.handle(intent)
    }
}
