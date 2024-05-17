package com.kiruhm.cabify_mobile_challenge.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun Double.formatPrice(
    symbol: String,
    decimalPartStyle: SpanStyle = MaterialTheme.typography.bodySmall.toSpanStyle()
): AnnotatedString {

    val formattedNumber = DecimalFormat.getInstance(Locale.getDefault()).format(this)
    val (wholePart, decimalPart) = formattedNumber.split(DecimalFormatSymbols.getInstance(Locale.getDefault()).decimalSeparator).run {
        when(size){
            1 -> this[0] to "00"
            2 -> this[0] to if (this[1].toInt() < 10) "${this[1]}0" else this[1]
            else -> throw IllegalArgumentException("Invalid number format")
        }
    }

    return buildAnnotatedString {
        append(wholePart)
        append('\'')
        withStyle(style = decimalPartStyle) {
            append(decimalPart.take(2))
            append(' ')
        }
        append(symbol)
    }
}

fun NavController.navigateToAndClear(id: String) {
    CoroutineScope(Dispatchers.Main).launch {
        navigate(id) {
            popUpTo(graph.id) { inclusive = true }
        }
    }
}