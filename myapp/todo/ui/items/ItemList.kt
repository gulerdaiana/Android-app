package com.ilazar.myapp.todo.ui.items

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilazar.myapp.todo.data.Item
import com.ilazar.myapp.ui.theme.Desert
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
typealias OnItemFn = (id: String?) -> Unit

@Composable
fun ItemList(itemList: List<Item>, onItemClick: OnItemFn) {
    Log.d("ItemList", "recompose")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(itemList) { item ->
            ItemDetail(item, onItemClick)
        }
    }
}

@Composable
fun ItemDetail(item: Item, onItemClick: OnItemFn) {
    val infiniteTransition = rememberInfiniteTransition()
    val translateAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Surface(
            modifier = Modifier
                .clickable { onItemClick(item._id) }
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = item.text,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    ),
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .graphicsLayer {
                            // Apply the translation animation to the X-axis
                            translationX = translateAnim.value
                        }
                )
                Text(
                    text = "Date: ${item.date}",
                    fontSize = 14.sp,
                    color = Desert
                )
                Text(
                    text = "Number: ${item.number}",
                    fontSize = 14.sp,
                    color = Desert
                )
                Text(
                    text = "Boolean: ${item.boolean}",
                    fontSize = 14.sp,
                    color = Desert
                )
            }
        }
    }
}
