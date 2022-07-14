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
    data class Recipe(val recipeIndex: Int) : Screen()

    @Serializable
    data class IngredientsSelection(
        val recipeIndex: Int,
        val selectedIngredients: List<Pair<String, Boolean>>,
    ) : Screen()
}

infix fun Screen.canNavigateTo(destination: Screen): Boolean {
    return when (this) {
        is Screen.Main -> destination is Screen.RecipesList
        is Screen.RecipesList -> destination is Screen.Recipe
        is Screen.Recipe -> destination is Screen.IngredientsSelection
        else -> false
    }
}

const val SCREEN_CHANGE_DELAY: Long = 40
private fun doScreenChangeDispatch(action: Action) = doDelayedDispatch(action, SCREEN_CHANGE_DELAY)

sealed interface NavAction : Action {
    data class Goto(val screen: Screen) : NavAction
    object Back : NavAction
    object Home : NavAction
}

fun doNavigateTo(screen: Screen) = doScreenChangeDispatch(NavAction.Goto(screen))
fun doNavigateBack() = doScreenChangeDispatch(NavAction.Back)
fun doNavigateHome() = doScreenChangeDispatch(NavAction.Home)

typealias NavigationStack = List<Screen>

fun NavigationStack.editLast(block: (Screen) -> Screen): NavigationStack =
    dropLast(1) + block(last())

fun navigationReducer(
    navigationStack: NavigationStack,
    action: Action,
): NavigationStack = when (action) {
    is NavAction.Goto -> if (navigationStack.last() canNavigateTo action.screen) navigationStack + action.screen else navigationStack
    is NavAction.Back -> if (navigationStack.isNotEmpty()) navigationStack.dropLast(1) else navigationStack
    is NavAction.Home -> listOf(Screen.Main)
    else -> navigationStack
}

sealed interface ScreenAction : Action {
    data class ToggleIngredient(val ingredientIndex: Int) : ScreenAction
}

private fun List<Pair<String, Boolean>>.toggle(index: Int): List<Pair<String, Boolean>> =
    edit(index) { (ingredient, selected) -> ingredient to !selected }

fun screenReducer(
    screen: Screen,
    action: ScreenAction,
): Screen =
    if (screen is Screen.IngredientsSelection && action is ScreenAction.ToggleIngredient)
        screen.copy(selectedIngredients = screen.selectedIngredients.toggle(action.ingredientIndex))
    else screen
