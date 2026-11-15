package com.carft.app

object Routes {
    const val START_POINT = "start/{point}"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DETAIL = "detail/{optionId}"
    const val EDITOR = "editor"
    const val SIMULATION = "simulation/{optionId}"
    const val PRIORITY = "priority"
    const val CONFLICT = "conflict"
    const val RECOMMEND = "recommend"
    const val HISTORY = "history"
    const val SETTINGS = "settings"

    fun startPoint(point: String) = "start/$point"
    fun detail(optionId: String) = "detail/$optionId"
    fun simulation(optionId: String) = "simulation/$optionId"
}
