package com.gardencompanion.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gardencompanion.R
import com.gardencompanion.presentation.viewmodel.LocalAppContainer
import com.gardencompanion.presentation.viewmodel.PlantSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantSelectionScreen(
    planId: String,
    year: Int,
    subPlotId: String,
    rowId: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val container = LocalAppContainer.current

    val vm: PlantSelectionViewModel = viewModel(
        factory = remember {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return PlantSelectionViewModel(
                        planYear = year,
                        subPlotId = subPlotId,
                        rowId = rowId,
                        plantRepository = container.plantRepository,
                        gardenRepository = container.gardenRepository,
                        settingsRepository = container.settingsRepository,
                        companionPlantingService = container.companionPlantingService,
                        cropRotationService = container.cropRotationService,
                    ) as T
                }
            }
        },
    )

    val state by vm.uiState.collectAsState()
    val languageTag by container.settingsRepository.languageTag.collectAsState(initial = null)

    if (state.pendingDecision != null) {
        AlertDialog(
            onDismissRequest = { vm.dismissDecision() },
            title = { Text(stringResource(id = R.string.review_selection)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val pending = state.pendingDecision
                    if (pending != null) {
                        if (pending.hasDetrimentalNeighbor) {
                            Text(stringResource(id = R.string.warning_detrimental_neighbor))
                        }
                        if (pending.cropRotation.isWarning) {
                            Text(stringResource(id = R.string.warning_crop_rotation_same_family))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.confirmKeepAnyway(onDone) }) {
                    Text(stringResource(id = R.string.keep_anyway))
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.dismissDecision() }) {
                    Text(stringResource(id = R.string.dialog_cancel))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.plant_selection_title)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::updateQuery,
                label = { Text(stringResource(id = R.string.search)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.filteredPlants, key = { it.id }) { plant ->
                    val name = container.getLocalizedPlantNameUseCase.execute(plant, languageTag)
                    Card(onClick = { vm.selectPlant(plant.id, onDone = onDone) }) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = name)
                        }
                    }
                }
            }
        }
    }
}
