package com.gardencompanion.presentation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gardencompanion.R
import com.gardencompanion.domain.model.CompatibilityType
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
    val listState = rememberLazyListState()
    val scrollScope = rememberCoroutineScope()

    LaunchedEffect(draggingItemId) {
        val dragId = draggingItemId ?: return@LaunchedEffect
        while (draggingItemId != null) {
            val layoutInfo = listState.layoutInfo
            val viewStart = layoutInfo.viewportStartOffset
            val viewEnd = layoutInfo.viewportEndOffset
            val edgeSize = (viewEnd - viewStart) * 0.15f

            val draggedInfo = layoutInfo.visibleItemsInfo.find { it.key == dragId }
            if (draggedInfo != null) {
                val itemTop = draggedInfo.offset + dragVisualOffsetY
                val itemBottom = itemTop + draggedInfo.size

                val speed = when {
                    itemTop < viewStart + edgeSize -> {
                        val fraction = 1f - ((itemTop - viewStart) / edgeSize).coerceIn(0f, 1f)
                        -fraction * 12f
                    }
                    itemBottom > viewEnd - edgeSize -> {
                        val fraction = 1f - ((viewEnd - itemBottom) / edgeSize).coerceIn(0f, 1f)
                        fraction * 12f
                    }
                    else -> 0f
                }

                if (speed != 0f) {
                    val scrolled = listState.scrollBy(speed)
                    dragAccumulatedY += scrolled
                    dragVisualOffsetY += scrolled

                    var currentIdx = displayRows.indexOfFirst { it.id == dragId }
                    if (currentIdx != -1) {
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
                    }
                }
            }
            delay(16)
        }
    }

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
                state = listState,
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

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it != SwipeToDismissBoxValue.Settled) {
                                vm.deleteRow(row.id)
                                true
                            } else true
                        },
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = draggingItemId == null,
                        enableDismissFromEndToStart = draggingItemId == null,
                        backgroundContent = {
                            val alignment = when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                else -> Alignment.CenterEnd
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.error)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(id = R.string.delete_row),
                                    tint = MaterialTheme.colorScheme.onError,
                                )
                            }
                        },
                        modifier = Modifier
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer {
                                translationY = if (isDragging) dragVisualOffsetY else 0f
                                shadowElevation = if (isDragging) 12f else 0f
                            },
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bg)
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
                            val prevPlantId = displayRows.getOrNull(index - 1)?.plantId
                            val nextPlantId = displayRows.getOrNull(index + 1)?.plantId

                            val topCompat by produceState<CompatibilityType?>(
                                initialValue = null,
                                key1 = row.plantId,
                                key2 = prevPlantId,
                            ) {
                                value = if (row.plantId != null && prevPlantId != null) {
                                    vm.getCompatibilityType(row.plantId, prevPlantId)
                                } else null
                            }

                            val bottomCompat by produceState<CompatibilityType?>(
                                initialValue = null,
                                key1 = row.plantId,
                                key2 = nextPlantId,
                            ) {
                                value = if (row.plantId != null && nextPlantId != null) {
                                    vm.getCompatibilityType(row.plantId, nextPlantId)
                                } else null
                            }

                            Box(modifier = Modifier.fillMaxWidth()) {
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
                                topCompat?.let { type ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .background(compatibilityColor(type))
                                            .align(Alignment.TopStart)
                                    )
                                }
                                bottomCompat?.let { type ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .background(compatibilityColor(type))
                                            .align(Alignment.BottomStart)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun compatibilityColor(type: CompatibilityType): Color = when (type) {
    CompatibilityType.BENEFICIAL -> Color(0xFF4CAF50)
    CompatibilityType.DETRIMENTAL -> Color(0xFFF44336)
    CompatibilityType.NEUTRAL -> Color(0xFF9E9E9E)
}
