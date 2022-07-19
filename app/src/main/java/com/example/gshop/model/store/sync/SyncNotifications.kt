package com.example.gshop.model.store.sync

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface SyncStatus {
    sealed interface Intermediate : SyncStatus
    object EstablishingConnection : Intermediate
    object FetchingWatchList : Intermediate
    object SendingList : Intermediate

    object SyncComplete : SyncStatus
    data class SyncFailed(val error: String) : SyncStatus
}


object SyncNotifications {
    val snackbarHostState = SnackbarHostState()
    private var currentSnackbarShowingJob: Job? = null

    private fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Long) {
        currentSnackbarShowingJob?.cancel()
        currentSnackbarShowingJob = GlobalScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun notify(status: SyncStatus) {
        when (status) {
            is SyncStatus.EstablishingConnection -> showSnackbar("Establishing connection...")
            is SyncStatus.FetchingWatchList -> showSnackbar("Fetching watch list...")
            is SyncStatus.SendingList -> showSnackbar("Sending list...")
            is SyncStatus.SyncComplete -> showSnackbar("Sync complete!", SnackbarDuration.Short)
            is SyncStatus.SyncFailed -> showSnackbar("Sync failed: ${status.error}")
        }
    }
}