package com.github.ljarka.toad.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.ljarka.toad.data.species.SpeciesEntity
import com.github.ljarka.toad.ui.species.EdibilityBadge
import java.util.Calendar

@Composable
fun SeasonSection(
    species: List<SpeciesEntity>,
    onSpeciesClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            currentMonthLabel(),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            style = MaterialTheme.typography.titleMedium,
        )
        if (species.isEmpty()) {
            Text(
                "Brak gatunków w sezonie",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(species, key = { it.id }) { entity ->
                    SeasonSpeciesCard(entity = entity, onClick = { onSpeciesClick(entity.id) })
                }
            }
        }
    }
}

@Composable
private fun SeasonSpeciesCard(entity: SpeciesEntity, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.width(120.dp)) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("🍄", style = MaterialTheme.typography.headlineMedium)
            Text(
                entity.namePl,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            EdibilityBadge(entity.edibility)
        }
    }
}

private fun currentMonthLabel(): String {
    val idx = Calendar.getInstance().get(Calendar.MONTH)
    val names = listOf(
        "Styczeń", "Luty", "Marzec", "Kwiecień",
        "Maj", "Czerwiec", "Lipiec", "Sierpień",
        "Wrzesień", "Październik", "Listopad", "Grudzień",
    )
    return "${names.getOrElse(idx) { "Ten miesiąc" }} 🍄"
}
