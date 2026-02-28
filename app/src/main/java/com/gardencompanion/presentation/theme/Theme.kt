package com.gardencompanion.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Seed,
)

private val DarkColors = darkColorScheme(
    primary = Seed,
)

@Composable
fun GardenCompanionPlannerTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = if (dynamicColor) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (androidx.compose.foundation.isSystemInDarkTheme()) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        } else {
            if (androidx.compose.foundation.isSystemInDarkTheme()) DarkColors else LightColors
        }
    } else {
        if (androidx.compose.foundation.isSystemInDarkTheme()) DarkColors else LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
