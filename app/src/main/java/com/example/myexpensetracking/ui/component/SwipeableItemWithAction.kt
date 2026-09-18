package com.example.myexpensetracking.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlin.math.roundToInt

@Composable
fun SwipeableItemWithAction(
    actions: @Composable RowScope.() -> Unit,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    var actionMenuWidth by remember { mutableStateOf(0f) }
    val offset = remember {
        Animatable(0f)
    }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .onSizeChanged({
                    actionMenuWidth = it.width.toFloat()
                }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .then(
                    if (enabled) {
                        Modifier.pointerInput(actionMenuWidth) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { _, dragAmount ->
                                    scope.launch {
                                        val newOffset = (offset.value + dragAmount)
                                            .coerceIn(0f - actionMenuWidth, 0f)
                                        offset.snapTo(newOffset)
                                    }
                                },
                                onDragEnd = {
                                    when {
                                        offset.value <= -(actionMenuWidth / 2) -> {
                                            scope.launch {
                                                offset.snapTo(0f - actionMenuWidth)
                                                onExpanded()
                                            }
                                        }

                                        else -> {
                                            scope.launch {
                                                offset.snapTo(0f)
                                                onCollapsed()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    } else {
                        Modifier
                    }
                )

        ) {
            content()
        }
    }
}