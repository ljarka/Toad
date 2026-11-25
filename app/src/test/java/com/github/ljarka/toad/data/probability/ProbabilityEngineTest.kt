package com.github.ljarka.toad.data.probability

import org.junit.Assert.assertTrue
import org.junit.Test

class ProbabilityEngineTest {
    private val engine = ProbabilityEngine()

    @Test
    fun `September heavy rain and warm temperature gives score above 70`() {
        // Warsaw-like September: steady moderate rain + 15°C
        val input = WeatherInput(
            precipitationMm = List(7) { 0.5 } + List(7) { 4.0 },
            temperatureMean = List(14) { 15.0 },
            currentMonth = 9
        )
        val result = engine.calculate(input)
        assertTrue("score=${result.score} should be ≥ 70", result.score >= 70)
    }

    @Test
    fun `January with cold temperatures gives score below 20`() {
        val input = WeatherInput(
            precipitationMm = List(14) { 0.0 },
            temperatureMean = List(14) { 0.0 },
            currentMonth = 1
        )
        val result = engine.calculate(input)
        assertTrue("score=${result.score} should be ≤ 20", result.score <= 20)
    }

    @Test
    fun `Zero precipitation last 14 days with cold temperature gives score below 25`() {
        // No rain + cold spring: drought + temp penalty dominates
        val input = WeatherInput(
            precipitationMm = List(14) { 0.0 },
            temperatureMean = List(14) { 2.0 },
            currentMonth = 9
        )
        val result = engine.calculate(input)
        assertTrue("score=${result.score} should be ≤ 25", result.score <= 25)
    }

    @Test
    fun `30mm rain 2 days ago and 15 degrees gives score above 65`() {
        // Single heavy rain event 2 days ago with ideal temperature
        val precip = MutableList(14) { 0.0 }
        precip[11] = 30.0
        val input = WeatherInput(
            precipitationMm = precip,
            temperatureMean = List(14) { 15.0 },
            currentMonth = 9
        )
        val result = engine.calculate(input)
        assertTrue("score=${result.score} should be ≥ 65", result.score >= 65)
    }
}
