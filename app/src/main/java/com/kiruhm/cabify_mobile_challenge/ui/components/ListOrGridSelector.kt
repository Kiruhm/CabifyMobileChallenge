package com.kiruhm.cabify_mobile_challenge.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridSelector(
    modifier: Modifier = Modifier,
    displayGrid: Boolean,
    onToggleGrid: (Boolean) -> Unit
) {

    val dimensions = LocalDim.current

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
    ) {
        SegmentedButton(
            selected = !displayGrid,
            onClick = { if (displayGrid) onToggleGrid.invoke(false) },
            shape = RoundedCornerShape(dimensions.cornersLarge, 0.dp, 0.dp, dimensions.cornersLarge),
            icon = {},
            label = {
                Icon(
                    modifier = Modifier.size(dimensions.iconSmallSize + dimensions.iconExtraSmallSize),
                    imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "List",
                )
            }
        )
        SegmentedButton(
            selected = displayGrid,
            onClick = { if (!displayGrid) onToggleGrid.invoke(true) },
            shape = RoundedCornerShape(0.dp, dimensions.cornersLarge, dimensions.cornersLarge, 0.dp),
            icon = {},
            label = {
                Icon(
                    modifier = Modifier.size(dimensions.iconSmallSize + dimensions.iconExtraSmallSize),
                    imageVector = Icons.Default.GridView, contentDescription = "Grid"
                )
            }
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
fun GridSelectorPreview() {

    var displayGrid by remember {
        mutableStateOf(false)
    }

    AppSurface {
        GridSelector(
            modifier = Modifier,
            displayGrid = displayGrid,
            onToggleGrid = { displayGrid = it }
        )
    }
}

