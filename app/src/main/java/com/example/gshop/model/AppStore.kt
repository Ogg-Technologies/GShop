package com.example.gshop.model

import com.example.gshop.redux.Action
import com.example.gshop.redux.Store
import com.example.gshop.redux.loggerMiddleware
import com.example.gshop.redux.thunkMiddleware
import kotlinx.serialization.Serializable
import java.util.*

val appStore = Store(
    initialState = State(),
    rootReducer = ::rootReducer,
    middlewares = listOf(
        loggerMiddleware,
        thunkMiddleware,
    )
)

@Serializable
data class State(
    val itemField: ItemField = ItemField(),
    val shoppingList: List<Item> = emptyList(),
)

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

@Serializable
data class ItemField(
    val text: String = "",
    val category: Category = "",
    val isOpened: Boolean = false,
)

sealed interface ListAction : Action {
    data class AddItems(val items: List<Item>) : ListAction
    data class ToggleItem(val id: Identifier) : ListAction
    data class RemoveItems(val ids: List<Identifier>) : ListAction
}

sealed interface ItemFieldAction : Action {
    object Open : ItemFieldAction
    object Close : ItemFieldAction
    object Submit : ItemFieldAction
    data class SetText(val text: String) : ItemFieldAction
}

fun rootReducer(state: State, action: Action): State = when (action) {
    is ItemFieldAction -> state.copy(itemField = itemFieldReducer(state.itemField, action))
    is ListAction -> state.copy(shoppingList = shoppingListReducer(state.shoppingList, action))
    else -> state
}

fun itemFieldReducer(itemField: ItemField, action: ItemFieldAction): ItemField = when (action) {
    is ItemFieldAction.Open -> itemField.copy(isOpened = true)
    is ItemFieldAction.Close -> itemField.copy(isOpened = false)
    is ItemFieldAction.Submit -> itemField.copy(text = "")
    is ItemFieldAction.SetText -> itemField.copy(text = action.text)
}

fun shoppingListReducer(shoppingList: List<Item>, action: Action): List<Item> = when (action) {
    is ListAction.AddItems -> shoppingList + action.items
    is ListAction.ToggleItem -> shoppingList.map { if (it.id == action.id) it.toggle() else it }
    is ListAction.RemoveItems -> shoppingList.filter { it.id !in action.ids }
    else -> shoppingList
}