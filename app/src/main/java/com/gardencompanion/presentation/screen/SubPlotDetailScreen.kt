package com.gardencompanion.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gardencompanion.R
import com.gardencompanion.domain.model.Row
import com.gardencompanion.presentation.viewmodel.LocalAppContainer
import com.gardencompanion.presentation.viewmodel.SubPlotDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubPlotDetailScreen(
    planId: String,
    year: Int,
    subPlotId: String,
    onBack: () -> Unit,
    onChoosePlant: (rowId: String) -> Unit,
) {
    val container = LocalAppContainer.current

    val vm: SubPlotDetailViewModel = viewModel(
        factory = remember {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SubPlotDetailViewModel(
                        subPlotId = subPlotId,
                        plantRepository = container.plantRepository,
                        gardenRepository = container.gardenRepository,
                        settingsRepository = container.settingsRepository
                    ) as T
                }
            }
        },
    )

    val rows by vm.rows.collectAsState()
    val displayRows = remember { mutableStateListOf<Row>() }
    val languageTag by container.settingsRepository.languageTag.collectAsState(initial = null)


    LaunchedEffect(rows) {
        displayRows.clear()
        displayRows.addAll(rows.sortedBy { it.orderIndex })
    }

    var draggingItemId by remember { mutableStateOf<String?>(null) }
    var dragAccumulatedY by remember { mutableFloatStateOf(0f) }
    var dragVisualOffsetY by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val rowStepPx = with(density) { 88.dp.toPx() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${stringResource(id = R.string.rows_title)} ($year)") },
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
        floatingActionButton = {
            Button(onClick = { vm.addRow() }) {
                Text(stringResource(id = R.string.add_row))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(
                    items = displayRows,
                    key = { _, item -> item.id },
                ) { index, row ->
                    val isDragging = draggingItemId == row.id
                    val bg = if (isDragging) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg)
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer {
                                translationY = if (isDragging) dragVisualOffsetY else 0f
                                shadowElevation = if (isDragging) 12f else 0f
                            }
                            .pointerInput(row.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggingItemId = row.id
                                        dragAccumulatedY = 0f
                                        dragVisualOffsetY = 0f
                                    },
                                    onDragEnd = {
                                        draggingItemId = null
                                        dragAccumulatedY = 0f
                                        dragVisualOffsetY = 0f
                                        vm.reorder(displayRows.map { it.id })
                                    },
                                    onDragCancel = {
                                        draggingItemId = null
                                        dragAccumulatedY = 0f
                                        dragVisualOffsetY = 0f
                                    },
                                    onDrag = { _, dragAmount ->
                                        val dragId = draggingItemId
                                            ?: return@detectDragGesturesAfterLongPress
                                        var currentIdx = displayRows.indexOfFirst { it.id == dragId }
                                        if (currentIdx == -1) return@detectDragGesturesAfterLongPress

                                        dragAccumulatedY += dragAmount.y
                                        dragVisualOffsetY += dragAmount.y

                                        while (dragAccumulatedY > rowStepPx && currentIdx < displayRows.lastIndex) {
                                            val item = displayRows.removeAt(currentIdx)
                                            displayRows.add(currentIdx + 1, item)
                                            currentIdx++
                                            dragAccumulatedY -= rowStepPx
                                            dragVisualOffsetY -= rowStepPx
                                        }

                                        while (dragAccumulatedY < -rowStepPx && currentIdx > 0) {
                                            val item = displayRows.removeAt(currentIdx)
                                            displayRows.add(currentIdx - 1, item)
                                            currentIdx--
                                            dragAccumulatedY += rowStepPx
                                            dragVisualOffsetY += rowStepPx
                                        }
                                    },
                                )
                            },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${index + 1}")
                                val emptyRowText = stringResource(id = R.string.empty_row)
                                val plantName by produceState(
                                    initialValue = emptyRowText,
                                    key1 = row.plantId,
                                    key2 = languageTag,
                                ) {
                                    value = if (row.plantId == null) {
                                        emptyRowText
                                    } else {
                                        vm.getPlantDisplayName(row.plantId, languageTag) ?: emptyRowText
                                    }
                                }
                                Text(text = plantName)
                            }
                            IconButton(onClick = { onChoosePlant(row.id) }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = stringResource(id = R.string.change_plant),
                                )
                            }
                            IconButton(onClick = { vm.clearRow(row.id) }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = stringResource(id = R.string.clear_plant),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
