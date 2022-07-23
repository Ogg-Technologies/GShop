package com.example.gshop.model.store

import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.gshop.App
import com.example.gshop.redux.Action
import com.example.gshop.redux.Thunk
import com.example.gshop.ui.utilities.toast
import kotlinx.serialization.Serializable

private fun readDocumentFile(file: DocumentFile): String {
    val contentResolver = App.context.contentResolver
    contentResolver.openInputStream(file.uri)!!.bufferedReader().use {
        return it.readText()
    }
}

private fun readRecipesFromUri(uri: Uri): List<Recipe>? {
    // According to the fromTreeUri documentation, it will return null on versions before LOLLIPOP
    val recipesRoot = DocumentFile.fromTreeUri(App.context, uri) ?: return null

    return recipesRoot.listFiles()
        .map { readDocumentFile(it) }
        .map { recipeFromMarkdownString(it) }
}


fun doSelectRecipesFolder(uri: Uri) = Thunk { _, dispatch ->
    val folder = uri.toString()
    val recipes = readRecipesFromUri(uri)
    if (recipes == null) {
        println("Could not read recipes from $folder")
        return@Thunk
    }
    val recipesData = RecipesData.Initialized(folder, recipes)
    dispatch(SetRecipesData(recipesData))
}

private fun State.recipesFolder(): String? = when (recipesData) {
    is RecipesData.Initialized -> recipesData.folder
    else -> null
}

fun State.selectedRecipeIndex(): Int = when (currentScreen) {
    is Screen.Recipe -> (currentScreen as Screen.Recipe).recipeIndex
    is Screen.IngredientsSelection -> (currentScreen as Screen.IngredientsSelection).recipeIndex
    else -> throw IllegalStateException("No recipe selected")
}

fun State.getRecipe(recipeIndex: Int): Recipe {
    val recipes = (recipesData as? RecipesData.Initialized)?.recipes
    checkNotNull(recipes) { "Recipes have not been initialized" }
    val recipe = recipes.getOrNull(recipeIndex)
    checkNotNull(recipe) { "Recipe with index $recipeIndex does not exist. recipes.size=${recipes.size}" }
    return recipe
}

fun State.selectedRecipe(): Recipe = getRecipe(selectedRecipeIndex())

fun doRefreshRecipes() = Thunk { state, dispatch ->
    state.recipesFolder()?.let {
        dispatch(doSelectRecipesFolder(it.toUri()))
    } ?: App.context.toast("No folder selected")
}

data class SetRecipesData(val recipesData: RecipesData) : Action

@Serializable
sealed class RecipesData {
    @Serializable
    object NoFolderSelected : RecipesData()

    @Serializable
    data class Initialized(val folder: String, val recipes: List<Recipe> = emptyList()) :
        RecipesData()
}

@Serializable
class Recipe(
    val title: String,
    val contents: String,
    val ingredients: List<String>,
)

fun recipeFromMarkdownString(rawString: String): Recipe {
    val title = rawString.lineSequence().iterator().next().removePrefix("# ")
    val ingredients = parseIngredients(rawString)
    return Recipe(title, rawString, ingredients)
}

private fun parseIngredients(recipeContents: String): List<String> {
    val ingredientsRegex =
        Regex("## Ingredients.*?##", setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL))
    val rawIngredients = ingredientsRegex.find(recipeContents)?.value
        ?: throw IllegalStateException("Can not find ingredient section $recipeContents")

    val ingredients = rawIngredients.lines()
        .filter { it.startsWith("- ") }
        .map { it.removePrefix("- ") }

    check(ingredients.isNotEmpty()) { "Could not parse ingredients in recipe $recipeContents" }

    return ingredients
}
