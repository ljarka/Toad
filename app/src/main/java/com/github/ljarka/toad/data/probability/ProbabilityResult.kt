package com.github.ljarka.toad.data.probability

data class WeatherInput(
    val precipitationMm: List<Double>,
    val temperatureMean: List<Double>,
    val currentMonth: Int
)

data class ProbabilityResult(
    val score: Int,
    val label: ProbabilityLabel,
    val factors: FactorBreakdown
)

enum class ProbabilityLabel { LOW, MODERATE, GOOD, EXCELLENT }

data class FactorBreakdown(
    val precipitation: Float,
    val temperature: Float,
    val season: Float,
    val soilProxy: Float,
    val drought: Float
)
