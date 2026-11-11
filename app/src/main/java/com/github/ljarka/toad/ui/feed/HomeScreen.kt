package com.github.ljarka.toad.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.ljarka.toad.R
import com.github.ljarka.toad.ui.theme.ToadTheme

/** Placeholder home screen until the feed (T-008) lands. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    versionName: String,
    onOpenProfile: (() -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            if (onOpenProfile != null) {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        TextButton(onClick = onOpenProfile) {
                            Text(stringResource(R.string.home_profile_button))
                        }
                    },
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_mushroom),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
            Text(
                stringResource(R.string.home_coming_soon),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.home_version, versionName),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    ToadTheme { HomeScreen(versionName = "v0.1.0") }
}
