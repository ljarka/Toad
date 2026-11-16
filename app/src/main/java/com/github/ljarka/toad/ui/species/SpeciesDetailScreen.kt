package com.github.ljarka.toad.ui.species

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesDetailScreen(
    speciesId: Int,
    viewModel: SpeciesViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    LaunchedEffect(speciesId) {
        viewModel.loadSpeciesDetail(speciesId)
    }

    val entity by viewModel.detailSpecies.collectAsState()
    val lookalikes by viewModel.lookalikes.collectAsState()

    val current = entity
    if (current == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(current.namePl, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🍄", style = MaterialTheme.typography.displayLarge)
                }
            }

            if (current.edibility == "deadly") {
                item {
                    Surface(color = Color(0xFFC62828), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                            Text(
                                "UWAGA: Grzyb śmiertelnie trujący!",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
            } else if (current.edibility == "toxic") {
                item {
                    Surface(color = Color(0xFFE65100), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                            Text(
                                "Uwaga: Grzyb toksyczny!",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(current.namePl, style = MaterialTheme.typography.headlineMedium)
                    Text(current.nameLatin, style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic)
                    if (current.nameCommon.isNotBlank()) {
                        Text(
                            current.nameCommon,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    EdibilityBadge(current.edibility)

                    Text("Sezon", style = MaterialTheme.typography.labelLarge)
                    SeasonBar(current.seasonStart, current.seasonEnd)

                    Text("Siedlisko", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        current.habitat.split(",").map { it.trim() }.forEach { h ->
                            SuggestionChip(onClick = {}, label = { Text(habitatLabel(h)) })
                        }
                    }

                    Text("Opis", style = MaterialTheme.typography.labelLarge)
                    Text(current.description, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (lookalikes.isNotEmpty()) {
                item {
                    Text(
                        "Podobne gatunki",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
                items(lookalikes, key = { "lookalike_${it.id}" }) { lookalike ->
                    ListItem(
                        headlineContent = { Text(lookalike.namePl) },
                        supportingContent = { Text(lookalike.nameLatin, fontStyle = FontStyle.Italic) },
                        trailingContent = { EdibilityBadge(lookalike.edibility) },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
