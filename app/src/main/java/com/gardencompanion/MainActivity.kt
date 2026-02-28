package com.gardencompanion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import com.gardencompanion.presentation.navigation.GardenNavHost
import com.gardencompanion.presentation.theme.GardenCompanionPlannerTheme
import com.gardencompanion.presentation.viewmodel.LocalAppContainer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as GardenApplication).container

        setContent {
            GardenCompanionPlannerTheme {
                Surface {
                    CompositionLocalProvider(LocalAppContainer provides container) {
                        GardenNavHost()
                    }
                }
            }
        }
    }
}
