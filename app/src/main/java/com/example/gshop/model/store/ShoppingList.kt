package com.example.gshop.model.store

import com.example.gshop.redux.Action
import com.example.gshop.redux.AsyncThunk
import com.example.gshop.redux.Thunk
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.util.*

typealias Category = String
typealias Identifier = Int

fun generateId(): Identifier = UUID.randomUUID().leastSignificantBits.toInt()

@Serializable
data class Item(
    val name: String,
    val isChecked: Boolean,
    val id: Identifier,
    val category: Category,
)

fun Item.toggle() = copy(isChecked = !isChecked)
fun List<Item>.toggle(id: Identifier): List<Item> = map { if (it.id == id) it.toggle() else it }

sealed interface ListAction : Action {
    data class AddItems(val items: List<Item>) : ListAction
    data class ToggleItem(val id: Identifier) : ListAction
    data class RemoveItems(val ids: List<Identifier>) : ListAction
}

fun doAddItem(item: Item): Action = ListAction.AddItems(listOf(item))
fun doRemoveItem(id: Identifier): Action = ListAction.RemoveItems(listOf(id))
fun doEditItem(id: Identifier): Action = Thunk { state, dispatch ->
    val item = state.shoppingList.first { it.id == id }
    dispatch(doRemoveItem(id))
    dispatch(ItemFieldAction.SetText(item.name))
    dispatch(ItemFieldAction.SetCategory(item.category))
    dispatch(ItemFieldAction.Open)
}

fun doStaggeredClearCompleted(timeDelay: Long = 100): Action = AsyncThunk { state, dispatch ->
    val completedIds = state.shoppingList.filter { it.isChecked }.map { it.id }
    completedIds.forEach {
        delay(timeDelay)
        dispatch(doRemoveItem(it))
    }
}

fun shoppingListReducer(shoppingList: List<Item>, action: Action): List<Item> = when (action) {
    is ListAction.AddItems -> shoppingList + action.items
    is ListAction.ToggleItem -> shoppingList.toggle(action.id)
    is ListAction.RemoveItems -> shoppingList.filter { it.id !in action.ids }
    else -> shoppingList
}
