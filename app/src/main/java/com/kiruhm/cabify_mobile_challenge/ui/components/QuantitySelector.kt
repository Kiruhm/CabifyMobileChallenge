package com.kiruhm.cabify_mobile_challenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.domain.models.utils.TestTags
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim

@Composable
fun QuantitySelector(
    modifier: Modifier,
    quantity: Int,
    onAddClicked: () -> Unit,
    onSubtractClicked: () -> Unit
) {

    val dimensions = LocalDim.current
    val context = LocalContext.current

    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .wrapContentHeight(Alignment.CenterVertically)
            .background(
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(dimensions.cornersLarge)
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier
                .testTag(TestTags.REMOVE_PRODUCT_TAG)
                .clip(RoundedCornerShape(topStart = dimensions.cornersLarge, bottomStart = dimensions.cornersLarge))
                .background(MaterialTheme.colorScheme.primary),
            onClick = onSubtractClicked) {
            if (quantity == 1) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.remove_quantity),
                    tint = Color.Red
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Remove,
                    tint = MaterialTheme.colorScheme.background,
                    contentDescription = stringResource(R.string.subtract_quantity),
                )
            }
        }

        Text(
            text = stringResource(R.string.quantity, quantity),
            modifier = Modifier.weight(1f).semantics {
                contentDescription = context.getString(R.string.quantity_description)
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        IconButton(
            modifier = Modifier
                .testTag(TestTags.ADD_PRODUCT_TAG)
                .clip(RoundedCornerShape(topEnd = dimensions.cornersLarge, bottomEnd = dimensions.cornersLarge))
                .background(MaterialTheme.colorScheme.primary),
            onClick = onAddClicked,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                tint = MaterialTheme.colorScheme.background,
                contentDescription = stringResource(R.string.add_quantity),
            )
        }
    }
}

@Preview
@Composable
private fun QuantitySelectorPreview() {

    var quantity by remember {
        mutableIntStateOf(1)
    }

    AppSurface {
        QuantitySelector(
            modifier = Modifier.fillMaxWidth(),
            quantity = quantity,
            onAddClicked = { quantity++ },
            onSubtractClicked = { quantity-- }
        )
    }
}