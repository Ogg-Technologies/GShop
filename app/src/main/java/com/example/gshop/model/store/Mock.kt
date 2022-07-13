package com.example.gshop.model.store

fun mockState() = State(
    itemField = ItemField(text = "Ham", isOpened = true),
    shoppingList = listOf(
        mockItem("Milk"),
        mockItem("Bread"),
        mockItem("Eggs"),
        mockItem("Cheese"),
        mockItem("Bacon"),
    )
)

fun mockItem(name: String) = Item(
    name = name,
    isChecked = false,
    id = generateId(),
    category = "Fruit/Vegetables"
)