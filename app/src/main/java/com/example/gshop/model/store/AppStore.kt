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
    val recipesData: RecipesData = RecipesData.NoFolderSelected,
)

data class SetState(val state: State) : Action

fun rootReducer(state: State, action: Action): State = when (action) {
    is SetRecipesData -> state.copy(recipesData = action.recipesData)
    is SetState -> action.state
    is NavAction -> state.copy(navigationStack = navigationReducer(
        state.navigationStack,
        action
    ))
    is ScreenAction -> state.copy(navigationStack = state.navigationStack.editLast {
        screenReducer(it,
            action)
    })
    is ItemFieldAction -> state.copy(itemField = itemFieldReducer(state, action))
    is ListAction -> state.copy(shoppingList = shoppingListReducer(state.shoppingList, action))
    is AddItemCategoryAssociation -> state.copy(itemCategoryAssociations = itemCategoryAssociationsReducer(
        state.itemCategoryAssociations,
        action
    ))
    else -> state
}
