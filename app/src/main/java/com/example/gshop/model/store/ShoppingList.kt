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
fun List<Item>.sortedByCategoryAndName() = sortedWith(compareBy(
    { allCategories.indexOf(it.category) },
    { it.name }
))

sealed interface ListAction : Action {
    data class AddItem(val item: Item) : ListAction
    data class ToggleItem(val id: Identifier) : ListAction
    data class RemoveItem(val id: Identifier) : ListAction
}

fun doAddItem(item: Item) = Thunk { _, dispatch ->
    dispatch(ListAction.AddItem(item))
    dispatch(AddItemCategoryAssociation(item.name, item.category))
}

fun doEditItem(id: Identifier): Action = Thunk { state, dispatch ->
    val item = state.shoppingList.first { it.id == id }
    dispatch(ListAction.RemoveItem(id))
    dispatch(ItemFieldAction.SetText(item.name))
    dispatch(ItemFieldAction.SetCategory(item.category))
    dispatch(ItemFieldAction.Open)
}

fun doStaggeredClearCompleted(timeDelay: Long = 100): Action = AsyncThunk { state, dispatch ->
    val completedIds = state.shoppingList.filter { it.isChecked }.map { it.id }
    completedIds.forEach {
        delay(timeDelay)
        dispatch(ListAction.RemoveItem(it))
    }
}

fun shoppingListReducer(shoppingList: List<Item>, action: Action): List<Item> = when (action) {
    is ListAction.AddItem -> (shoppingList + action.item).sortedByCategoryAndName()
    is ListAction.ToggleItem -> shoppingList.toggle(action.id)
    is ListAction.RemoveItem -> shoppingList.filter { it.id != action.id }
    else -> shoppingList
}
