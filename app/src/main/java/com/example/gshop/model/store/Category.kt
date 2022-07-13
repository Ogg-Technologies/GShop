package com.example.gshop.model.store

import kotlinx.serialization.Serializable

@Serializable
data class CategoryAssociation(val name: Category, val knownProducts: Set<String>)

fun State.allCategories(): List<Category> = categoryAssociations.map { it.name }

const val DEFAULT_CATEGORY = "Other"

fun createEmptyCategoryAssociations() = listOf(
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
).map { CategoryAssociation(it, emptySet()) }
