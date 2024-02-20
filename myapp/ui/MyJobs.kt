package com.ilazar.myservices.ui

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.*
import com.ilazar.myservices.util.MyWorker
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

data class MyJobUiState(val isRunning: Boolean = false)

class MyJobsViewModel(application: Application) : AndroidViewModel(application) {
    var uiState by mutableStateOf(MyJobUiState())
        private set
    private var workManager: WorkManager
    private var workId: UUID? = null

    init {
        workManager = WorkManager.getInstance(getApplication())
        startJob()
    }

    private fun startJob() {
        viewModelScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val inputData = Data.Builder()
                .putInt("to", 10)
                .build()
            val myPeriodicWork = PeriodicWorkRequestBuilder<MyWorker>(20, TimeUnit.SECONDS)
            val myWork = OneTimeWorkRequest.Builder(MyWorker::class.java)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()
            workId = myWork.id
            uiState = uiState.copy(isRunning = true)
            workManager.apply {
                // enqueue Work
                enqueue(myWork)
                // observe work status
                getWorkInfoByIdLiveData(workId!!).asFlow().collect {
                    Log.d("MyJobsViewModel", "$it")
                    uiState = uiState.copy(
                        isRunning = !it.state.isFinished,
                    )
                    if (it.state.isFinished) {
                        uiState = uiState.copy()
                    }
                }
            }
        }
    }

    fun cancelJob() { // send signal to WorkManager to cancel a specific task and update its state
        workManager.cancelWorkById(workId!!)
        uiState = uiState.copy(isRunning = false)

    }


    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MyJobsViewModel(application)
            }
        }
    }
}

@Composable
fun MyJobs() {
    val myJobsViewModel = viewModel<MyJobsViewModel>(
        factory = MyJobsViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )

    Column {
        Row {
            Button(onClick = { myJobsViewModel.cancelJob() }) {
                Text("Cancel Job")
            }
            Spacer(modifier = Modifier.width(30.dp)) // Adjust the width as needed
            Text("Ui state: "+
                    "${myJobsViewModel.uiState.isRunning}",
            )
        }

    }
}
