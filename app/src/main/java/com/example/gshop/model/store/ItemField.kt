package com.example.gshop.model.store

import com.example.gshop.redux.Action
import com.example.gshop.redux.Thunk
import kotlinx.serialization.Serializable

@Serializable
data class ItemField(
    val text: String = "",
    val category: Category = DEFAULT_CATEGORY,
    val isOpened: Boolean = false,
)

sealed interface ItemFieldAction : Action {
    object Open : ItemFieldAction
    object Close : ItemFieldAction
    data class SetText(val text: String) : ItemFieldAction
    data class SetCategory(val category: Category) : ItemFieldAction
}

fun doSubmitItemField(closeOnSubmit: Boolean = false) = Thunk { state, dispatch ->
    val newItem = state.itemField.toItem()
    dispatch(doAddItem(newItem))
    dispatch(ItemFieldAction.SetText(""))
    if (closeOnSubmit) dispatch(ItemFieldAction.Close)
}

fun ItemField.trimmedText(): String = text.trim()

fun ItemField.toItem() =
    Item(name = trimmedText(), isChecked = false, id = generateId(), category = category)

fun itemFieldReducer(state: State, action: ItemFieldAction): ItemField = with(state.itemField) {
    when (action) {
        is ItemFieldAction.Open -> copy(isOpened = true)
        is ItemFieldAction.Close -> copy(isOpened = false)
        is ItemFieldAction.SetText -> copy(
            text = action.text,
            category = guessCategory(action.text, state.itemCategoryAssociations)
        )
        is ItemFieldAction.SetCategory -> copy(category = action.category)
    }
}
