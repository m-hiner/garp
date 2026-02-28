package com.gardencompanion.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gardencompanion.R
import com.gardencompanion.presentation.viewmodel.LocalAppContainer
import com.gardencompanion.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {
    val container = LocalAppContainer.current

    val vm: SettingsViewModel = viewModel(
        factory = remember {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(container.settingsRepository) as T
                }
            }
        },
    )

    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { padding ->
        val state = uiState ?: return@Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.setting_crop_rotation))
                }
                Switch(
                    checked = state.cropRotationEnabled,
                    onCheckedChange = { vm.setCropRotationEnabled(it) },
                )
            }

            Text(stringResource(id = R.string.setting_language))

            LanguageRow(
                label = stringResource(id = R.string.language_system),
                selected = state.languageTag == null,
                onClick = { vm.setLanguageTag(null) },
            )
            LanguageRow(
                label = stringResource(id = R.string.language_en),
                selected = state.languageTag == "en",
                onClick = { vm.setLanguageTag("en") },
            )
            LanguageRow(
                label = stringResource(id = R.string.language_sk),
                selected = state.languageTag == "sk",
                onClick = { vm.setLanguageTag("sk") },
            )
        }
    }
}

@Composable
private fun LanguageRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label)
        Switch(checked = selected, onCheckedChange = { onClick() })
    }
}
