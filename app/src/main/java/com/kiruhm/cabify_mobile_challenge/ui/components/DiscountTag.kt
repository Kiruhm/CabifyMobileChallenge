package com.kiruhm.cabify_mobile_challenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kiruhm.cabify_mobile_challenge.domain.models.enums.DiscountType
import com.kiruhm.cabify_mobile_challenge.ui.theme.BulkColor
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim
import com.kiruhm.cabify_mobile_challenge.ui.theme.TwoForOneColor

@Composable
fun DiscountTag(
    modifier: Modifier = Modifier,
    discountType: DiscountType
) {

    val dimensions = LocalDim.current

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.CenterEnd)
            .padding(top = dimensions.spaceXXSmall)
            .background(
                color = when (discountType) {
                    is DiscountType.Bulk -> BulkColor
                    DiscountType.None -> Color.Transparent
                    DiscountType.TwoForOne -> TwoForOneColor
                },
                shape = RoundedCornerShape(
                    topStart = dimensions.cornersSmall,
                    topEnd = 0.dp,
                    bottomStart = 0.dp,
                    bottomEnd = dimensions.cornersSmall
                )
            )
    ) {
        Text(
            text = stringResource(id = discountType.name),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = dimensions.spaceExtraSmall)
                .wrapContentSize(Alignment.Center)
        )
    }
}