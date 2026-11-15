package com.carft.domain.model

enum class TriState {
    TRUE, FALSE, UNKNOWN;

    infix fun and(other: TriState): TriState = when {
        this == FALSE || other == FALSE -> FALSE
        this == UNKNOWN || other == UNKNOWN -> UNKNOWN
        else -> TRUE
    }

    infix fun or(other: TriState): TriState = when {
        this == TRUE || other == TRUE -> TRUE
        this == UNKNOWN || other == UNKNOWN -> UNKNOWN
        else -> FALSE
    }

    fun isKnown(): Boolean = this != UNKNOWN

    override fun toString(): String = when (this) {
        TRUE -> "✓"
        FALSE -> "✗"
        UNKNOWN -> "?"
    }
}
