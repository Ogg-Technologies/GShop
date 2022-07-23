package com.example.gshop.model.store

import com.example.gshop.model.utilities.ai.calculateDiceCoefficient
import com.example.gshop.model.utilities.ai.classifyWithKNN
import com.example.gshop.redux.Action

data class AddItemCategoryAssociation(val itemName: String, val category: Category) : Action

fun itemCategoryAssociationsReducer(
    itemCategoryAssociations: Map<String, Category>,
    action: AddItemCategoryAssociation,
): Map<String, Category> = itemCategoryAssociations + (action.itemName to action.category)

fun guessCategory(itemName: String, itemCategoryAssociations: Map<String, Category>): Category {
    // If the item has an exact match in our associations, use that
    itemCategoryAssociations[itemName]?.let {
        return it
    }

    // If no exact match exist, predict it using KNN with Dices coefficient
    return guessCategoryWithKNN(itemName, itemCategoryAssociations)
}

private fun guessCategoryWithKNN(
    itemName: String,
    itemCategoryAssociations: Map<String, Category>,
): Category = classifyWithKNN(
    trainingData = itemCategoryAssociations,
    predictionItem = itemName,
    k = 3,
    distanceFunction = { a, b -> 1 - calculateDiceCoefficient(a, b) }
)

private data class CategoryAssociation(val name: Category, val knownProducts: Set<String>)

const val DEFAULT_CATEGORY = "Other"

private val startingCategoryAssociationData: List<CategoryAssociation> = listOf(
    CategoryAssociation(
        "Vegetables/Fruit", setOf(
            "vegetable",
            "fruit",
            "bell pepper",
            "tomato",
            "cucumber",
            "mushroom",
            "carrot",
            "grapes",
            "orange",
            "pear",
            "apple",
            "potato",
            "leek",
        )
    ),
    CategoryAssociation(
        "Bread", setOf(
            "bread",
            "toast",
        )
    ),
    CategoryAssociation(
        "Cookies", setOf(
            "cookies",
            "oreo",
            "cake",
        )
    ),
    CategoryAssociation(
        "Meat", setOf(
            "ground meat",
            "beef",
            "chicken",
            "pork",
            "hot dog",
            "ham",
            "salami",
            "bacon",
        )
    ),
    CategoryAssociation(
        "Dairy", setOf(
            "milk",
            "cream",
        )
    ),
    CategoryAssociation(
        "Yoghurt/Juice", setOf(
            "juice",
            "yoghurt"
        )
    ),
    CategoryAssociation(
        "Frozen Food", setOf(
            "frozen",
            "pancakes",
        )
    ),
    CategoryAssociation(
        "Butter/Cheese/Eggs", setOf(
            "butter",
            "cheese",
            "cheddar",
            "egg",
        )
    ),
    CategoryAssociation(
        "Taco", setOf(
            "taco",
            "enchilada",
            "tortilla",
        )
    ),
    CategoryAssociation(
        "Spice/Oil", setOf(
            "spice",
            "oregano",
            "pepper",
            "salt",
            "oil",
            "soy sauce",
        )
    ),
    CategoryAssociation(
        "Rice/Canned Food", setOf(
            "rice",
            "canned",
            "diced tomatoes",
            "crushed tomatoes",
        )
    ),
    CategoryAssociation(
        "Pasta/Baking", setOf(
            "pasta",
            "spaghetti",
            "lasagne sheets",
            "flour",
            "sugar",
            "baking powder",
        )
    ),
    CategoryAssociation(
        "Flakes/Grain", setOf(
            "flakes",
            "grain",
            "muesli",
            "oats",
        )
    ),
    CategoryAssociation(
        "Coffee", setOf(
            "coffee",
        )
    ),
    CategoryAssociation(
        "Cleaning", setOf(
            "cleaning",
            "plastic bags",
            "detergent",
        )
    ),
    CategoryAssociation(
        "Dental/Schampo", setOf(
            "toothpaste",
            "dental floss",
            "deodorant",
            "mouthwash",
            "shampoo",
        )
    ),
    CategoryAssociation(
        "Candy/Chips", setOf(
            "candy",
            "chocolate",
            "chips",
        )
    ),
    CategoryAssociation(
        DEFAULT_CATEGORY, setOf(
            "raisin",
            "glass",
            "nuts",
        )
    ),
    CategoryAssociation(
        "Large Items", setOf(
            "large",
            "paper",
        )
    )
)

val allCategories: List<Category> = startingCategoryAssociationData.map { it.name }

fun getStartingItemCategoryAssociations(): Map<String, Category> =
    startingCategoryAssociationData
        .flatMap { data -> data.knownProducts.map { it to data.name } }
        .toMap()
