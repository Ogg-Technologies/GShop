package com.example.gshop.model.store

import com.example.gshop.redux.Action
import kotlinx.serialization.Serializable

val State.currentScreen get() = navigationStack.last()

@Serializable
sealed class Screen {
    @Serializable
    object Main : Screen()

    @Serializable
    object RecipesList : Screen()

    @Serializable
    data class Recipe(val index: Int) : Screen()
}

const val SCREEN_CHANGE_DELAY: Long = 40
fun doScreenChangeDispatch(action: Action) = doDelayedDispatch(action, SCREEN_CHANGE_DELAY)

infix fun Screen.canNavigateTo(destination: Screen): Boolean {
    return when (this) {
        is Screen.Main -> destination is Screen.RecipesList
        is Screen.RecipesList -> destination is Screen.Recipe
        else -> false
    }
}

sealed interface NavAction : Action {
    data class Goto(val screen: Screen) : NavAction
    object Back : NavAction
}

fun doNavigateTo(screen: Screen) = doScreenChangeDispatch(NavAction.Goto(screen))
fun doNavigateBack() = doScreenChangeDispatch(NavAction.Back)

typealias NavigationStack = List<Screen>

fun navigationReducer(
    navigationStack: NavigationStack,
    action: Action,
): NavigationStack = when (action) {
    is NavAction.Goto -> if (navigationStack.last() canNavigateTo action.screen) navigationStack + action.screen else navigationStack
    is NavAction.Back -> if (navigationStack.isNotEmpty()) navigationStack.dropLast(1) else navigationStack
    else -> navigationStack
}
