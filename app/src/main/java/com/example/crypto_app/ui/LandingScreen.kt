package com.example.crypto_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LandingScreen(
    onBbsPlusClick: () -> Unit,
    onBbsSharpClick: () -> Unit,
    onZkSnarkClick: () -> Unit,
    onZkStarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Algorithm",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Button(
            onClick = onBbsPlusClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(56.dp)
        ) {
            Text("BBS+", style = MaterialTheme.typography.titleMedium)
        }

        Button(
            onClick = onBbsSharpClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(56.dp)
        ) {
            Text("BBS#", style = MaterialTheme.typography.titleMedium)
        }

        Button(
            onClick = onZkSnarkClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(56.dp)
        ) {
            Text("zk-SNARKs", style = MaterialTheme.typography.titleMedium)
        }

        Button(
            onClick = onZkStarkClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(56.dp)
        ) {
            Text("zk-STARKs", style = MaterialTheme.typography.titleMedium)
        }
    }
}
