package com.example.gshop.model.utilities.ai

/**
 * Calculates a number between 0 and 1 which represents how similar the two strings are.
 *
 * dice coefficient = bigram overlap * 2 / (bigrams in s1 + bigrams in s2)
 */
fun calculateDiceCoefficient(s1: String, s2: String): Double {
    // Equal strings have a dice coefficient of 1
    if (s1 == s2) return 1.0

    val bigrams1 = s1.zipWithNext().toSet()
    val bigrams2 = s2.zipWithNext().toSet()
    val bigramsOverlap = bigrams1 intersect bigrams2

    // If both strings are so short that they have no bigrams, the dice coefficient is 0
    if (bigrams1.size + bigrams2.size == 0) return 0.0

    return bigramsOverlap.size.toDouble() * 2 / (bigrams1.size + bigrams2.size)
}