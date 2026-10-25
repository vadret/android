package fi.kroon.vadret.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.launch

private const val HALF = 2f

private val indicatorSpring: AnimationSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow
)

private data class OptionBounds(
    val left: Float,
    val width: Float
)

@Composable
fun VadretSlidingSelector(
    optionCount: Int,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    role: Role,
    modifier: Modifier = Modifier,
    isEqualWidth: Boolean = false,
    optionContent: @Composable RowScope.(index: Int, contentColor: Color) -> Unit
) {
    val indicatorColor: Color = VadretTheme.colorScheme.primary
    val optionBounds: SnapshotStateList<OptionBounds?> = remember(optionCount) {
        mutableStateListOf<OptionBounds?>().apply { repeat(times = optionCount) { add(null) } }
    }
    val indicatorLeft: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }
    val indicatorWidth: Animatable<Float, AnimationVector1D> = remember { Animatable(initialValue = 0f) }
    var isIndicatorPlaced: Boolean by remember { mutableStateOf(false) }
    val target: OptionBounds? = optionBounds.getOrNull(index = selectedIndex)

    LaunchedEffect(target) {
        if (target == null) {
            return@LaunchedEffect
        }
        if (isIndicatorPlaced) {
            launch { indicatorLeft.animateTo(targetValue = target.left, animationSpec = indicatorSpring) }
            indicatorWidth.animateTo(targetValue = target.width, animationSpec = indicatorSpring)
        } else {
            indicatorLeft.snapTo(targetValue = target.left)
            indicatorWidth.snapTo(targetValue = target.width)
            isIndicatorPlaced = true
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .selectableGroup()
            .drawBehind {
                if (isIndicatorPlaced) {
                    drawRoundRect(
                        color = indicatorColor,
                        topLeft = Offset(x = indicatorLeft.value, y = 0f),
                        size = Size(width = indicatorWidth.value, height = size.height),
                        cornerRadius = CornerRadius(x = size.height / HALF, y = size.height / HALF)
                    )
                }
            }
    ) {
        repeat(times = optionCount) { index: Int ->
            val isSelected: Boolean = index == selectedIndex
            val contentColor: Color by animateColorAsState(
                targetValue = if (isSelected) {
                    VadretTheme.colorScheme.onPrimary
                } else {
                    VadretTheme.colorScheme.onSurfaceVariant
                },
                label = "selectorContent"
            )
            val widthModifier: Modifier = if (isEqualWidth) {
                Modifier.weight(weight = 1f)
            } else {
                Modifier
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = widthModifier
                    .fillMaxHeight()
                    .onPlaced { coordinates ->
                        optionBounds[index] = OptionBounds(
                            left = coordinates.positionInParent().x,
                            width = coordinates.size.width.toFloat()
                        )
                    }.clip(shape = CircleShape)
                    .selectable(
                        selected = isSelected,
                        role = role,
                        onClick = { onOptionSelected(index) }
                    )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    optionContent(index, contentColor)
                }
            }
        }
    }
}