package com.ilazar.myapp.todo.ui

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.data.getCurrentDate
import com.ilazar.myapp.todo.ui.item.ItemViewModel
import com.ilazar.myapp.ui.TAG
import com.ilazar.myservices.util.createNotificationChannel
import com.ilazar.myservices.util.showSimpleNotification
import java.util.*
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.keyframes


@Composable
fun ItemScreen(itemId: String?, onClose: () -> Unit) {
    val localContext = LocalContext.current
    val itemViewModel = viewModel<ItemViewModel>(factory = ItemViewModel.Factory(itemId))
    val itemUiState = itemViewModel.uiState
    var date by rememberSaveable { mutableStateOf(itemUiState.item?.date ?: getCurrentDate()) }
    var boolean by rememberSaveable { mutableStateOf(itemUiState.item?.boolean ?: false) }
    var number by rememberSaveable { mutableStateOf(itemUiState.item?.number ?: 0) }
    var text by rememberSaveable { mutableStateOf(itemUiState.item?.text ?: "") }

    var lat by rememberSaveable { mutableStateOf(itemUiState.item?.lat ?: 46.0) }
    var lng by rememberSaveable { mutableStateOf(itemUiState.item?.lng ?: 23.0) }
    Log.d("ItemScreen", "recompose, text = $text")

    val context = LocalContext.current
    val channelId = "MyTestChannel"
    val notificationId = 0

    LaunchedEffect(Unit) {
        createNotificationChannel(channelId, context)
    }

    LaunchedEffect(itemUiState.savingCompleted) {
        Log.d("ItemScreen", "Saving completed = ${itemUiState.savingCompleted}");
        if (itemUiState.savingCompleted) {
            onClose();
        }
    }

    val markerState = rememberMarkerState(position = LatLng(lat, lng))
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    var textInitialized by remember { mutableStateOf(itemId == null) }
    LaunchedEffect(itemId, itemUiState.isLoading) {
        Log.d("ItemScreen", "Saving completed = ${itemUiState.savingCompleted}");
        if (textInitialized) {
            return@LaunchedEffect
        }
        if (itemUiState.item != null && !itemUiState.isLoading) {
            date = itemUiState.item.date
            boolean = itemUiState.item.boolean
            number = itemUiState.item.number
            text = itemUiState.item.text
            lat = itemUiState.item.lat
            lng = itemUiState.item.lng
            markerState.position = LatLng(lat,lng)
            cameraPositionState.position=CameraPosition.fromLatLngZoom(markerState.position, 10f)
            Log.d("Location","${markerState.position}")
            textInitialized = true
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.5f at 0 with FastOutSlowInEasing
                1.0f at 1
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.item)) },
                actions = {
                    Button(onClick = {
                        Log.d("ItemScreen", "save item text = $text");
                        itemViewModel.saveOrUpdateItem(date, boolean, number, text,lat,lng)
                        showSimpleNotification(
                            context,
                            channelId,
                            notificationId,
                            "Saved book",
                            "save item text = $text"
                        )
                    }) { Text("Save") }
                }
            )
        }
    ) {
        if (itemUiState.isLoading) {
            CircularProgressIndicator()
            return@Scaffold
        }
        Column(
            modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 500)),
        ) {
        if (itemUiState.loadingError != null) {
            Text(text = "Failed to load item - ${itemUiState.loadingError.message}")
        }
        Row {
            ClickableText(
                text = AnnotatedString(date),
                onClick = {
                    val calendar = Calendar.getInstance()

                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val dat = calendar.get(Calendar.DAY_OF_MONTH)

                    val mDatePickerDialog = DatePickerDialog(
                        localContext,
                        { _: DatePicker, cYear: Int, cMonth: Int, cDay: Int ->
                            date = "$cYear/${cMonth + 1}/$cDay"
                        }, year, month, dat
                    )
                    mDatePickerDialog.show()
                }
            )
        }

        Row {
            TextField(
                value = number.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { number = try{it.toInt()}catch (e: java.lang.Exception){0} },
                label = { Text("Number") },

            )
        }
        Row {
            TextField(
                value = text,
                onValueChange = { text = it }, label = { Text("Text") },
//                modifier = Modifier.fillMaxSize()
            )
        }
            Row {
                Text(text = "Published: ")
                Checkbox(
                    checked = boolean,
                    onCheckedChange = { boolean = it },
                    modifier = Modifier.graphicsLayer(
                        rotationZ = rotationAngle
                    )
                )
            }
        Row{
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    Log.d(TAG, "onMapClick $it")
                },
                onMapLongClick = {
                    Log.d(TAG, "onMapLongClick $it")
                    markerState.position = it
                    lat=it.latitude
                    lng=it.longitude
                },
            ) {
                Marker(
                    state = markerState,
                    title = "User location title",
                    snippet = "User location",
                )
            }
        }
        if (itemUiState.isSaving) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { LinearProgressIndicator() }
        }
        if (itemUiState.savingError != null) {
            Text(text = "Failed to save item - ${itemUiState.savingError.message}")
        }
        }
    }
}

@Preview
@Composable
fun PreviewItemScreen() {
    ItemScreen(itemId = "0", onClose = {})
}
