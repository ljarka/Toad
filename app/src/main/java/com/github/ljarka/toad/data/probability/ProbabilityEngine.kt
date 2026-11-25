package com.github.ljarka.toad.data.probability

import javax.inject.Inject
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt

class ProbabilityEngine @Inject constructor() {
    fun calculate(input: WeatherInput): ProbabilityResult {
        val precip = precipitationScore(input.precipitationMm)
        val temp = temperatureScore(input.temperatureMean)
        val season = seasonScore(input.currentMonth)
        val soil = soilProxyScore(input.temperatureMean)
        val drought = droughtScore(input.precipitationMm)
        val score = (0.35 * precip + 0.30 * temp + 0.20 * season + 0.10 * soil + 0.05 * drought) * 100
        return ProbabilityResult(
            score = score.roundToInt(),
            label = labelFor(score),
            factors = FactorBreakdown(precip, temp, season, soil, drought)
        )
    }

    private fun precipitationScore(days: List<Double>): Float {
        val rain7d = days.takeLast(7).sum()
        val lastRainDaysAgo = days.takeLast(5).indexOfLast { it > 1.0 }
            .let { if (it < 0) 99 else 4 - it }
        val recency = when {
            lastRainDaysAgo <= 2 -> 1.0f
            lastRainDaysAgo <= 5 -> 0.7f
            else -> 0.3f
        }
        val gaussianPeak = exp(-0.5 * ((rain7d - 25) / 15.0).pow(2)).toFloat()
        return (gaussianPeak * recency).coerceIn(0f, 1f)
    }

    private fun temperatureScore(days: List<Double>): Float {
        val mean = days.takeLast(7).average()
        return trapezoid(mean, low = 5.0, lowPlateau = 10.0, highPlateau = 18.0, high = 24.0)
    }

    private fun seasonScore(month: Int): Float = when (month) {
        4, 5 -> 0.5f
        6, 7, 8, 9, 10 -> 1.0f
        11 -> 0.6f
        else -> 0.1f
    }

    private fun soilProxyScore(days: List<Double>): Float {
        val approx = days.takeLast(3).average()
        return trapezoid(approx, 5.0, 8.0, 20.0, 25.0)
    }

    private fun droughtScore(days: List<Double>): Float {
        val dryDays = days.takeLast(14).count { it < 1.0 }
        return (1 - dryDays / 10.0f).coerceIn(0f, 1f)
    }

    private fun trapezoid(x: Double, low: Double, lowPlateau: Double, highPlateau: Double, high: Double): Float =
        when {
            x <= low || x >= high -> 0f
            x <= lowPlateau -> ((x - low) / (lowPlateau - low)).toFloat()
            x >= highPlateau -> ((high - x) / (high - highPlateau)).toFloat()
            else -> 1f
        }

    private fun labelFor(score: Double): ProbabilityLabel = when {
        score < 25 -> ProbabilityLabel.LOW
        score < 50 -> ProbabilityLabel.MODERATE
        score < 75 -> ProbabilityLabel.GOOD
        else -> ProbabilityLabel.EXCELLENT
    }
}
