package com.example.gshop.model.utilities

/**
 * @param I: Type of items
 * @param C: Type of categories
 * @property trainingData: A map that assigns known items to their categories
 * @property predictionItem: The item for which a category should be predicted
 * @property k: The number of nearest neighbours to consider
 * @property distanceFunction: A function that calculates the distance between two items
 *
 * If k is greater than the number of items in the training data, all items will be considered
 * and the prediction will be the most common category. Otherwise it will use the majority
 * category of the k items closest to the prediction item.
 *
 * @return: the predicted category for the given item
 */
fun <I, C> classifyWithKNN(
    trainingData: Map<I, C>,
    predictionItem: I,
    k: Int,
    distanceFunction: (I, I) -> Double,
): C {
    require(k > 0) { "k must be greater than 0" }
    require(trainingData.isNotEmpty()) { "trainingData must not be empty" }

    val nearestNeighbours = trainingData.toList().sortedBy { (item, category) ->
        distanceFunction(predictionItem, item)
    }.take(k)

    val mostCommonCategory = nearestNeighbours.groupBy { (item, category) ->
        category
    }.toList().maxByOrNull { (category, items) ->
        items.size
    }!!.first

    return mostCommonCategory
}