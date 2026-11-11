package com.github.ljarka.toad.screenshots

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.github.ljarka.toad.ui.feed.HomeScreen
import com.github.ljarka.toad.ui.theme.ToadTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, maxPercentDifference = 0.1)

    @Test
    fun light() = paparazzi.snapshot { ToadTheme(darkTheme = false) { HomeScreen(versionName = "v0.1.0") } }

    @Test
    fun dark() = paparazzi.snapshot { ToadTheme(darkTheme = true) { HomeScreen(versionName = "v0.1.0") } }
}
