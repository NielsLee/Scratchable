package lying.fengfeng.scratchable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.math.PI


/**
 * A container used to add a scratchable surface upon content
 *
 * @param modifier the modifier to be applied to this layout
 * @param scratcherRadius radius of the circular scratcher
 * @param cover the color of the cover surface
 * @param coverExitTransition exit transition of the cover surface when reached moving threshold
 * @param movingThresholdMillis determines how long this container takes to display its entire contents when scratching
 * @param onFinished callback when reached moving threshold
 * @param contentAlignment alignment inside the container(box)
 * @param content content of this container
 */
@Composable
fun Scratchable(
    modifier: Modifier = Modifier,
    scratcherRadius: Dp = 10.dp,
    cover: Color = Color.Gray,
    movingThresholdMillis: Long = 1000,
    coverExitTransition: ExitTransition = fadeOut(),
    onFinished: (() -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    var contentSize by remember { mutableStateOf(Size(0f, 0f)) }

    var offset by remember { mutableStateOf(listOf<Offset>()) }
    var totalArea by remember { mutableFloatStateOf(Float.MAX_VALUE) }
    var scratchedArea by remember { mutableFloatStateOf(0f) }
    var isScratching by remember { mutableStateOf(false) }
    var lastMoveTimestamp = 0L
    var movingMillis = 0L
    val coverVisible by remember {
        derivedStateOf {
            if (isScratching) {
                true
            } else {
                movingMillis  < movingThresholdMillis
            }
        }
    }

    LaunchedEffect(key1 = coverVisible) {
        if (!coverVisible) onFinished?.invoke()
    }

    Box(
        modifier = modifier
            .onSizeChanged { newSize ->
                contentSize = newSize.toSize()
                totalArea = contentSize.width * contentSize.height
            },
        contentAlignment = contentAlignment
    ) {
        content()

        AnimatedVisibility(
            visible = coverVisible,
            exit = coverExitTransition
        ) {
            val density = LocalDensity.current.density
            val widthDp = (contentSize.width / density).dp
            val heightDp = (contentSize.height / density).dp
            Canvas(
                modifier = Modifier
                    .size(width = widthDp, height = heightDp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()

                                event.changes.forEach { inputChange ->
                                    offset = offset
                                        .toMutableList()
                                        .apply { add(inputChange.position) }
                                    inputChange.consume()

                                    val radiusPx = scratcherRadius.toPx()
                                    scratchedArea += (PI * radiusPx * radiusPx).toFloat()
                                }

                                when (event.type) {
                                    PointerEventType.Press -> {
                                        isScratching = true
                                        lastMoveTimestamp = System.currentTimeMillis()
                                    }

                                    PointerEventType.Move -> {
                                        // calculate how long we scratching
                                        movingMillis += System.currentTimeMillis() - lastMoveTimestamp
                                        lastMoveTimestamp = System.currentTimeMillis()
                                    }

                                    PointerEventType.Release -> {
                                        isScratching = false
                                    }
                                }
                            }
                        }
                    },
            ) {

                // create cover path
                val coverPath = Path().apply {
                    addRect(Rect(0f, 0f, contentSize.width, contentSize.height))
                }

                val radiusPx = scratcherRadius.toPx()
                val scratchedPath = Path().apply {
                    offset.forEach { offset ->
                        addOval(
                            Rect(
                                offset.x - radiusPx, offset.y - radiusPx,
                                offset.x + radiusPx, offset.y + radiusPx
                            )
                        )
                    }
                }

                val resultPath = Path().apply {
                    addPath(coverPath)
                    op(this, scratchedPath, PathOperation.Difference)
                }

                drawPath(
                    path = resultPath,
                    color = cover
                )
            }
        }
    }


}