package com.example.gshop.model

import com.example.gshop.redux.Action
import kotlinx.serialization.Serializable

@Serializable
data class ItemField(
    val text: String = "",
    val category: Category = "",
    val isOpened: Boolean = false,
)

sealed interface ItemFieldAction : Action {
    object Open : ItemFieldAction
    object Close : ItemFieldAction
    object Submit : ItemFieldAction
    data class SetText(val text: String) : ItemFieldAction
}

fun ItemField.toItem() =
    Item(name = text, isChecked = false, id = generateId(), category = category)

fun itemFieldReducer(itemField: ItemField, action: ItemFieldAction): ItemField = when (action) {
    is ItemFieldAction.Open -> itemField.copy(isOpened = true)
    is ItemFieldAction.Close -> itemField.copy(isOpened = false)
    is ItemFieldAction.Submit -> itemField.copy(text = "")
    is ItemFieldAction.SetText -> itemField.copy(text = action.text)
}
