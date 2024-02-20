package com.ilazar.myapp.todo.ui.items

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.ui.sensor.ProximitySensor
import com.ilazar.myservices.ui.MyJobs
import com.ilazar.myservices.ui.MyNetworkStatus

@Composable
fun ItemsScreen(onItemClick: (id: String?) -> Unit, onAddItem: () -> Unit, onLogout: () -> Unit) {
    Log.d("ItemsScreen", "recompose")
    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
    val itemsUiState = itemsViewModel.uiState

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = "Books") },
                    actions = {
                        Button(onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF1493))) { Text("Logout") }
                    }
                )
                Row {
                    MyNetworkStatus()
                    Spacer(modifier = Modifier.width(30.dp)) // Adjust the width as needed
                    MyJobs()
                }
            }
        },
        snackbarHost = {
            ProximitySensor()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("ItemsScreen", "add")
                    onAddItem()
                },
            ) { Icon(Icons.Rounded.Add, "Add") }
        }
    ) {
        when (itemsUiState) {
            is ItemsUiState.Success ->
                ItemList(itemList = itemsUiState.items, onItemClick = onItemClick)
            is ItemsUiState.Loading -> CircularProgressIndicator()
            is ItemsUiState.Error -> Text(text = "Failed to load items - $it, ${itemsUiState.exception?.message}")
        }
    }
}

@Preview
@Composable
fun PreviewItemsScreen() {
    ItemsScreen(onItemClick = {}, onAddItem = {}, onLogout = {})
}