package com.github.ljarka.toad.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.ljarka.toad.data.probability.FactorBreakdown
import com.github.ljarka.toad.data.probability.ProbabilityLabel
import com.github.ljarka.toad.data.probability.ProbabilityResult

private fun ProbabilityLabel.displayName(): String = when (this) {
    ProbabilityLabel.LOW -> "Małe szanse"
    ProbabilityLabel.MODERATE -> "Umiarkowane"
    ProbabilityLabel.GOOD -> "Dobre warunki"
    ProbabilityLabel.EXCELLENT -> "Doskonałe warunki"
}

private val ProbabilityLabel.gaugeColor: Color
    get() = when (this) {
        ProbabilityLabel.LOW -> Color(0xFFEF5350)
        ProbabilityLabel.MODERATE -> Color(0xFFFFB74D)
        ProbabilityLabel.GOOD -> Color(0xFF9CCC65)
        ProbabilityLabel.EXCELLENT -> Color(0xFF43A047)
    }

@Composable
fun ProbabilityCard(result: ProbabilityResult?, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        if (result == null) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🌦️", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(12.dp))
                Text("Sprawdzam warunki grzybowe…", style = MaterialTheme.typography.bodyMedium)
            }
            return@Card
        }

        var expanded by remember { mutableStateOf(false) }
        val color = result.label.gaugeColor

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProbabilityGauge(score = result.score, color = color)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(result.label.displayName(), style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Szansa na grzyby: ${result.score}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Ukryj szczegóły" else "Szczegóły")
            }
            AnimatedVisibility(visible = expanded) {
                FactorBreakdownTable(factors = result.factors)
            }
        }
    }
}

@Composable
private fun FactorBreakdownTable(factors: FactorBreakdown) {
    val rows = listOf(
        "Opady" to factors.precipitation,
        "Temperatura" to factors.temperature,
        "Sezon" to factors.season,
        "Wilgotność gleby" to factors.soilProxy,
        "Susza" to factors.drought,
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Spacer(Modifier.height(4.dp))
        rows.forEach { (name, value) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    name,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(130.dp),
                )
                LinearProgressIndicator(
                    progress = { value },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${(value * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(32.dp),
                )
            }
        }
    }
}
