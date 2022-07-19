package com.example.gshop.model.store.sync

import com.example.gshop.model.store.Item
import com.example.gshop.model.store.ListAction
import com.example.gshop.model.utilities.GResult
import com.example.gshop.model.utilities.suspenedGResult
import com.example.gshop.redux.Action
import com.example.gshop.redux.AsyncThunk

data class SetShoppingListAtLastSync(val list: List<Item>) : Action

fun syncWithWatch() = AsyncThunk { state, dispatch ->
    val syncResult = syncShoppingListWithWatch(
        shoppingListAtLastSync = state.shoppingListAtLastSync,
        shoppingList = state.shoppingList,
        onSyncStatusUpdate = { status ->
            SyncNotifications.notify(status)
        },
        onMergedListCreated = { mergedList ->
            dispatch(ListAction.SetShoppingList(mergedList))
            dispatch(SetShoppingListAtLastSync(mergedList))
        }
    )
    when (syncResult) {
        is GResult.Ok -> SyncNotifications.notify(SyncStatus.SyncComplete)
        is GResult.Err -> SyncNotifications.notify(SyncStatus.SyncFailed(syncResult.error))
    }
}

const val GARMIN_APP_ID = "1cbcd060-595c-4b40-bbac-b26475f39d5b"

suspend fun syncShoppingListWithWatch(
    shoppingListAtLastSync: List<Item>,
    shoppingList: List<Item>,
    onSyncStatusUpdate: suspend (SyncStatus.Intermediate) -> Unit,
    onMergedListCreated: suspend (mergedList: List<Item>) -> Unit,
): GResult<List<Item>, String> =
    suspenedGResult {
        val communication = GarminCommunicationManager.getCommunication(
            onCommunicationSetup = { onSyncStatusUpdate(SyncStatus.EstablishingConnection) },
        ).bind()
        onSyncStatusUpdate(SyncStatus.FetchingWatchList)
        val watchList = communication.getShoppingListFromWatch(5000).bind()
        val mergedList = mergeShoppingLists(shoppingListAtLastSync, watchList, shoppingList)
        onMergedListCreated(mergedList)
        onSyncStatusUpdate(SyncStatus.SendingList)
        val shoppingListMessage = mergedList.shoppingListToGarminProtocolFormat()
        communication.sendMessageToWatchForStatus(shoppingListMessage, 3000).bind()
        GResult.Ok(mergedList)
    }

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
