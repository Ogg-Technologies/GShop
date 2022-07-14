package com.example.gshop.model.store

import kotlinx.serialization.Serializable

@Serializable
data class CategoryAssociation(val name: Category, val knownProducts: Set<String>)

const val DEFAULT_CATEGORY = "Other"

val allCategories = listOf(
    "Vegetables/Fruit",
    "Bread",
    "Cookies",
    "Meat",
    "Dairy",
    "Yoghurt/Juice",
    "Frozen Food",
    "Butter/Cheese/Eggs",
    "Taco",
    "Spice/Oil",
    "Rice/Canned Food",
    "Pasta/Baking",
    "Flakes/Grain",
    "Coffee",
    "Cleaning",
    "Dental/Schampo",
    "Candy/Chips",
    DEFAULT_CATEGORY,
    "Large Items",
)

fun createEmptyCategoryAssociations() = allCategories.map { CategoryAssociation(it, emptySet()) }
