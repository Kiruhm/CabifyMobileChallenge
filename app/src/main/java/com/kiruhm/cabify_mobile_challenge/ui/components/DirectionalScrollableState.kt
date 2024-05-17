package com.kiruhm.cabify_mobile_challenge.ui.components

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

class DirectionalLazyStaggeredGridState(
    private val lazyStaggeredGridState: LazyStaggeredGridState
) {
    private var positionY = lazyStaggeredGridState.firstVisibleItemScrollOffset
    private var visibleItem = lazyStaggeredGridState.firstVisibleItemIndex

    val scrollDirection by derivedStateOf {
        if (!lazyStaggeredGridState.isScrollInProgress) {
            ScrollDirection.None
        } else {
            val firstVisibleItemIndex = lazyStaggeredGridState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset =
                lazyStaggeredGridState.firstVisibleItemScrollOffset

            // We are scrolling while first visible item hasn't changed yet
            if (firstVisibleItemIndex == visibleItem) {
                val direction = if (firstVisibleItemScrollOffset > positionY) {
                    ScrollDirection.Down
                } else {
                    ScrollDirection.Up
                }
                positionY = firstVisibleItemScrollOffset

                direction
            } else {

                val direction = if (firstVisibleItemIndex > visibleItem) {
                    ScrollDirection.Down
                } else {
                    ScrollDirection.Up
                }
                positionY = firstVisibleItemScrollOffset
                visibleItem = firstVisibleItemIndex
                direction
            }
        }
    }
}

@Composable
fun rememberDirectionalLazyStaggeredGridState(
    lazyStaggeredGridState: LazyStaggeredGridState,
): DirectionalLazyStaggeredGridState {
    return remember {
        DirectionalLazyStaggeredGridState(lazyStaggeredGridState)
    }
}

enum class ScrollDirection {
    None, Up, Down
}