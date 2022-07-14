package com.example.gshop.model.store

import com.example.gshop.redux.*
import kotlinx.serialization.Serializable

val appStore = Store(
    initialState = State(),
    rootReducer = ::rootReducer,
    middlewares = listOf(
        loggerMiddleware,
        thunkMiddleware,
        persistentStorageMiddleware,
    )
)

@Serializable
data class State(
    val itemField: ItemField = ItemField(),
    val shoppingList: List<Item> = emptyList(),
    val navigationStack: NavigationStack = listOf(Screen.Main),
    val itemCategoryAssociations: Map<String, Category> = getStartingItemCategoryAssociations(),
)

data class SetState(val state: State) : Action

fun rootReducer(state: State, action: Action): State = when (action) {
    is SetState -> action.state
    is NavAction -> state.copy(navigationStack = navigationReducer(
        state.navigationStack,
        action
    ))
    is ItemFieldAction.Submit -> state.copy(
        itemField = itemFieldReducer(state, action),
        shoppingList = shoppingListReducer(state.shoppingList, doAddItem(state.itemField.toItem())),
        itemCategoryAssociations = state.itemCategoryAssociations + (state.itemField.trimmedText() to state.itemField.category),
    )
    is ItemFieldAction -> state.copy(itemField = itemFieldReducer(state, action))
    is ListAction -> state.copy(shoppingList = shoppingListReducer(state.shoppingList, action))
    else -> state
}
