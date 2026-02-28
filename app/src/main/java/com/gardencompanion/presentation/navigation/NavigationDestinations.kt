package com.gardencompanion.presentation.navigation

object NavigationDestinations {
    const val Plans = "plans"
    const val PlanDetail = "plan/{planId}/{year}"
    const val SubPlotDetail = "subplot/{planId}/{year}/{subPlotId}"
    const val PlantSelection = "plantSelection/{planId}/{year}/{subPlotId}/{rowId}"
    const val Settings = "settings"

    fun planDetail(planId: String, year: Int): String = "plan/$planId/$year"

    fun subPlotDetail(planId: String, year: Int, subPlotId: String): String = "subplot/$planId/$year/$subPlotId"

    fun plantSelection(planId: String, year: Int, subPlotId: String, rowId: String): String =
        "plantSelection/$planId/$year/$subPlotId/$rowId"
}
