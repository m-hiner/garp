package com.gardencompanion.presentation.viewmodel

import androidx.compose.runtime.staticCompositionLocalOf
import com.gardencompanion.data.AppContainer

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}
