# 0006 — Mushroom occurrence probability: client-side, weather-based

Status: Accepted · Date: 2026-11-10

## Decision

Probability is calculated **on the Android device** using the last 14 days of weather data
from Open-Meteo (ADR 0003). No server-side computation in v1.

## Algorithm

Five factors, each scored 0.0–1.0, then combined as a weighted average.

### 1. Precipitation score (weight 0.35)
```
rain_7d = sum of daily precipitation over last 7 days (mm)
rain_14d = sum over last 14 days

ideal: rain_7d in [15, 40] mm AND at least one rain event > 5 mm in last 5 days
score = gaussian_peak(rain_7d, peak=25, sigma=15) * recency_bonus(last_rain_days_ago)
```

`recency_bonus`: 1.0 if rain < 3 days ago, 0.7 if 3–5 days, 0.3 if > 5 days.

### 2. Temperature score (weight 0.30)
```
temp_mean_7d = mean of temperature_2m_mean over last 7 days (°C)

Optimal range: 10–18 °C (general mushroom range)
score = trapezoid(temp_mean_7d, low=5, low_plateau=10, high_plateau=18, high=24)
```
Below 5°C or above 24°C → score = 0.

### 3. Season score (weight 0.20)
Calendar-based, computed from current month (1–12):
```
Spring (Morchella, Cantharellus early): April–May → 0.5
Summer–Autumn peak: June–October → 1.0
Late autumn (Armillaria): November → 0.6
Winter / early spring: December–March → 0.1
```
(Override per species when user filters by species.)

### 4. Soil temperature proxy (weight 0.10)
Approximate from air temp (soil lags ~3 days):
```
soil_temp_approx = mean(temp_mean_7d[-3:])
score = trapezoid(soil_temp_approx, 5, 8, 20, 25)
```

### 5. Drought stress score (weight 0.05)
```
dry_days_14 = count of days with precipitation < 1 mm in last 14 days
score = 1 - clamp(dry_days_14 / 10, 0, 1)
```

### Final probability
```
p = 0.35 * precipitation + 0.30 * temperature + 0.20 * season + 0.10 * soil + 0.05 * drought
```
Displayed as: 0–30% Low, 30–60% Moderate, 60–80% Good, 80–100% Excellent.

## Display

A single indicator on the home screen and map (ADR pending: heatmap vs radius circle).
In v1: show for user's current GPS location only. Heatmap grid in v2.

## Testing

Unit tests with historical dates where known mass fruiting occurred (e.g., September 2023 in
Poland, rain after drought). Tests in `app/data/probability/ProbabilityEngineTest.kt`.
