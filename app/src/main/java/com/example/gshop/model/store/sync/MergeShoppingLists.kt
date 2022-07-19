package com.example.gshop.model.store.sync

import com.example.gshop.model.store.Item

/**
 * Merges the data that comes from the watch with the data from the phone.
 *
 * [originalList] and [watchList] must contain the same items, [watchList] may only
 * make changes to isChecked.
 *
 * [phoneList] may have made changes to isChecked, added items and removed items.
 *
 * Returns a new list with all the changes applied.
 */
fun mergeShoppingLists(originalList: List<Item>, watchList: List<Item>, phoneList: List<Item>): List<Item> {
    val originalMap = originalList.toMap()
    val newMap = phoneList.toMap().toMutableMap()

    for (watchItem in watchList) {
        val id = watchItem.id
        val originalItem = originalMap[id] ?: continue
        if (originalItem.isChecked != watchItem.isChecked) {
            newMap[id] = originalItem.copy(isChecked = watchItem.isChecked)
        }
    }
    return newMap.values.toList()
}

private fun List<Item>.toMap(): Map<Int, Item> = associate { it.id to it }