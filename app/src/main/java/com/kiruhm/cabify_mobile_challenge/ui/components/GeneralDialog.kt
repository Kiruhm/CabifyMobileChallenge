package com.kiruhm.cabify_mobile_challenge.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.kiruhm.cabify_mobile_challenge.R
import com.kiruhm.cabify_mobile_challenge.ui.theme.LocalDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralDialog(
    modifier: Modifier = Modifier,
    description: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onAccept: () -> Unit
) {

    val dimensions = LocalDim.current
    val context = LocalContext.current

    BasicAlertDialog(modifier = modifier, onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.spaceMedium),
                verticalArrangement = Arrangement.Absolute.spacedBy(dimensions.spaceLarge)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.End),
                    horizontalArrangement = Arrangement.Absolute.spacedBy(LocalDim.current.spaceLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .semantics {
                                contentDescription = context.getString(R.string.negative_button)
                            }
                            .clickable(
                                role = Role.Button,
                                onClick = { onCancel.invoke() }
                            )
                            .padding(horizontal = dimensions.spaceExtraSmall),
                        text = negativeButtonText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                    Text(
                        modifier = Modifier
                            .semantics {
                                contentDescription = context.getString(R.string.positive_button)
                            }
                            .clickable(
                                role = Role.Button,
                                onClick = { onAccept.invoke() }
                            )
                            .padding(horizontal = dimensions.spaceExtraSmall),
                        style = MaterialTheme.typography.bodyMedium,
                        text = positiveButtonText
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmPurchaseDialogPreview() {
    AppSurface {
        GeneralDialog(
            description = "Description",
            positiveButtonText = "Accept",
            negativeButtonText = "Cancel",
            onAccept = {},
            onCancel = {},
            onDismiss = {}
        )
    }
}