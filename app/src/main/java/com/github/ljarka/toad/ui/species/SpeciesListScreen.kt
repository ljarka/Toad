package com.github.ljarka.toad.ui.species

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.ljarka.toad.data.species.SpeciesEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesListScreen(
    viewModel: SpeciesViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val seasonFilter by viewModel.seasonFilter.collectAsState()
    val edibilityFilter by viewModel.edibilityFilter.collectAsState()
    val habitatFilter by viewModel.habitatFilter.collectAsState()
    val species by viewModel.species.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Przewodnik grzybowy") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("Szukaj gatunku…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
            )

            Text(
                "Sezon",
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val months = listOf(
                "Sty" to 1, "Lut" to 2, "Mar" to 3, "Kwi" to 4,
                "Maj" to 5, "Cze" to 6, "Lip" to 7, "Sie" to 8,
                "Wrz" to 9, "Paź" to 10, "Lis" to 11, "Gru" to 12,
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(months) { (name, month) ->
                    FilterChip(
                        selected = seasonFilter == month,
                        onClick = { viewModel.onSeasonFilterChange(if (seasonFilter == month) null else month) },
                        label = { Text(name) },
                    )
                }
            }

            Text(
                "Jadalność",
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val edibilityChips = listOf(
                "Jadalne" to setOf("edible"),
                "Niejadalne" to setOf("inedible"),
                "Trujące" to setOf("toxic", "deadly"),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(edibilityChips) { (label, edibilities) ->
                    val selected = edibilityFilter == edibilities
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onEdibilityFilterChange(if (selected) emptySet() else edibilities) },
                        label = { Text(label) },
                    )
                }
            }

            Text(
                "Siedlisko",
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val habitatChips = listOf(
                "Las mieszany" to "mixed",
                "Las iglasty" to "conifer",
                "Las liściasty" to "deciduous",
                "Łąka" to "meadow",
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(habitatChips) { (label, key) ->
                    val selected = habitatFilter == key
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onHabitatFilterChange(if (selected) null else key) },
                        label = { Text(label) },
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(species, key = { it.id }) { entity ->
                    SpeciesCard(entity = entity, onClick = { onNavigateToDetail(entity.id) })
                }
            }
        }
    }
}

@Composable
internal fun SpeciesCard(entity: SpeciesEntity, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text("🍄", style = MaterialTheme.typography.displayMedium)
            }
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    entity.namePl,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                EdibilityBadge(entity.edibility)
            }
        }
    }
}

@Composable
internal fun EdibilityBadge(edibility: String) {
    val (label, color) = when (edibility) {
        "edible" -> "Jadalny" to Color(0xFF2E7D32)
        "inedible" -> "Niejadalny" to Color(0xFF757575)
        "toxic" -> "Toksyczny" to Color(0xFFE65100)
        "deadly" -> "Śmiertelny" to Color(0xFFC62828)
        else -> edibility to Color(0xFF757575)
    }
    Surface(color = color, shape = RoundedCornerShape(4.dp)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
        )
    }
}

@Composable
internal fun habitatLabel(key: String): String = when (key) {
    "mixed" -> "Las mieszany"
    "conifer" -> "Las iglasty"
    "deciduous" -> "Las liściasty"
    "meadow" -> "Łąka"
    else -> key
}

@Composable
internal fun SeasonBar(seasonStart: Int, seasonEnd: Int) {
    val monthAbbr = listOf("S", "L", "M", "K", "M", "C", "L", "S", "W", "P", "L", "G")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        monthAbbr.forEachIndexed { index, abbr ->
            val month = index + 1
            val isActive = if (seasonStart <= seasonEnd) {
                month in seasonStart..seasonEnd
            } else {
                month >= seasonStart || month <= seasonEnd
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(2.dp),
                        ),
                )
                Text(abbr, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
