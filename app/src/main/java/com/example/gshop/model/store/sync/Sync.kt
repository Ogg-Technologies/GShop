package com.example.gshop.model.store.sync

import com.example.gshop.App
import com.example.gshop.model.store.Item
import com.example.gshop.model.utilities.GResult
import com.example.gshop.model.utilities.suspenedGResult
import com.example.gshop.redux.AsyncThunk
import com.example.gshop.ui.toast
import com.garmin.android.connectiq.ConnectIQ

fun syncWithWatch() = AsyncThunk { state, dispatch ->
    val syncResult = syncShoppingListWithWatch(state.shoppingList) { status ->
        // Use status to show feedback to the user
        println(status)
    }
    App.context.toast(syncResult.toString())
    println(syncResult)
}

const val GARMIN_APP_ID = "1cbcd060-595c-4b40-bbac-b26475f39d5b"

var cachedCommunication: GarminCommunication? = null

sealed interface GarminSyncStatus {
    object EstablishingConnection : GarminSyncStatus
    object FetchingWatchList : GarminSyncStatus
    object SendingList : GarminSyncStatus
}

suspend fun syncShoppingListWithWatch(
    shoppingList: List<Item>,
    emitStatus: (GarminSyncStatus) -> Unit
): GResult<Unit, String> =
    suspenedGResult {
        val communication = cachedCommunication ?: run {
            emitStatus(GarminSyncStatus.EstablishingConnection)
            val com = createCommunication().bind()
            cachedCommunication = com
            com
        }
        emitStatus(GarminSyncStatus.FetchingWatchList)
        val watchList = communication.getShoppingListFromWatch(3000).bind()
        emitStatus(GarminSyncStatus.SendingList)
        val shoppingListMessage = shoppingList.shoppingListToGarminProtocolFormat()
        communication.sendMessageToWatchForStatus(shoppingListMessage, 3000).bind()
        GResult.Ok(Unit)
    }

private suspend fun createCommunication() =
    ConnectIQ.getInstance(App.context, ConnectIQ.IQConnectType.WIRELESS)
        .setupCommunication(App.context, GARMIN_APP_ID, 1000)

private const val GARMIN_SYNC_PROMPT_STRING = "sync"

private suspend fun GarminCommunication.getShoppingListFromWatch(timeoutMillis: Long? = null): GResult<List<Item>, String> =
    suspenedGResult {
        val responseAny = getGarminResponseFromPrompt(
            GARMIN_SYNC_PROMPT_STRING,
            timeoutMillis
        ).bind()
        val response = responseAny.anyToGarminProtocolFormat().bind()
        val shoppingList = response.garminProtocolFormatToShoppingList()
        GResult.Ok(shoppingList)
    }

@Suppress("UNCHECKED_CAST")
private fun Any.anyToGarminProtocolFormat(): GResult<List<List<String>>, String> {
    val castedResponse = this as? List<List<String>>
        ?: return GResult.Err("Garmin response is not in the correct Garmin protocol format (a list of lists of strings)")
    return GResult.Ok(castedResponse)
}

private fun List<Item>.shoppingListToGarminProtocolFormat(): List<List<String>> =
    map { item ->
        listOf(
            item.name,
            if (item.isChecked) "T" else "F",
            item.id.toString(),
            item.category
        )
    }

private fun List<List<String>>.garminProtocolFormatToShoppingList(): List<Item> =
    mapNotNull { it.garminProtocolFormatToShoppingItemOrNull() }

private fun List<String>.garminProtocolFormatToShoppingItemOrNull(): Item? {
    val name = getOrNull(0) ?: return null
    val isChecked = when (getOrNull(1)) {
        "T" -> true
        "F" -> false
        else -> return null
    }
    val id = getOrNull(2)?.toIntOrNull() ?: return null
    val category = getOrNull(3) ?: return null
    return Item(name, isChecked, id, category)
}
