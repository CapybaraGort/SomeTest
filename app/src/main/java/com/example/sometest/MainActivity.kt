package com.example.sometest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.sometest.ui.theme.SomeTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: TVControlViewModel by viewModels()
        enableEdgeToEdge()
        setContent {
            SomeTestTheme {
                TVControlScreen(viewModel, this)
            }
        }
    }
}
