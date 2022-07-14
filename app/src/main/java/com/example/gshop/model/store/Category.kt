package com.example.gshop.model.store

import kotlinx.serialization.Serializable

@Serializable
data class CategoryAssociation(val name: Category, val knownProducts: Set<String>)

const val DEFAULT_CATEGORY = "Other"

val startingCategoryAssociations: List<CategoryAssociation> = listOf(
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

val allCategories: List<Category> = startingCategoryAssociations.map { it.name }

