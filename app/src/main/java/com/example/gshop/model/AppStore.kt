package com.example.gshop.model

import com.example.gshop.redux.Action
import com.example.gshop.redux.Store
import com.example.gshop.redux.loggerMiddleware
import com.example.gshop.redux.thunkMiddleware
import kotlinx.serialization.Serializable

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
    val navigationStack: NavigationStack = listOf(Screen.Main),
)

fun rootReducer(state: State, action: Action): State = when (action) {
    is NavAction -> state.copy(navigationStack = navigationReducer(
        state.navigationStack,
        action
    ))
    is ItemFieldAction.Submit -> state.copy(
        itemField = itemFieldReducer(state.itemField, action),
        shoppingList = shoppingListReducer(state.shoppingList, doAddItem(state.itemField.toItem()))
    )
    is ItemFieldAction -> state.copy(itemField = itemFieldReducer(state.itemField, action))
    is ListAction -> state.copy(shoppingList = shoppingListReducer(state.shoppingList, action))
    else -> state
}
