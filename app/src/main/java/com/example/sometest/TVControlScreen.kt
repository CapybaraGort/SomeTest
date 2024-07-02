package com.example.sometest

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun TVControlScreen(viewModel: TVControlViewModel, context: Context) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            scope.launch {
                viewModel.startDiscovery(context)
            }
        }) {
            Text("Start Discovery")
        }

        Spacer(modifier = Modifier.height(16.dp))

        uiState.devices.forEach { device ->
            Button(
                onClick = {
                    scope.launch {
                        viewModel.connectToDevice(device)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(device.friendlyName)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TVRemoteCommandsScreen(viewModel = viewModel)

        uiState.error?.let {
            Text(it, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}

@Composable
fun TVRemoteCommandsScreen(modifier: Modifier = Modifier, viewModel: TVControlViewModel) {
    Column(modifier = modifier) {
        Row {
            Button(onClick = { viewModel.volumeUp() }) {
                Text(text = "Громкость +")
            }
            Button(onClick = { viewModel.volumeDown() }) {
                Text(text = "Громкость -")
            }
        }
    }
}