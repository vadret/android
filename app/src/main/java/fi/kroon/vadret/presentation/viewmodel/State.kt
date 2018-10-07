package fi.kroon.vadret.presentation.viewmodel

import androidx.recyclerview.widget.DiffUtil

data class State(
    val currentFilteredlist: List<String> = listOf(),
    val newFilteredList: List<String> = listOf(),
    val diffResult: DiffUtil.DiffResult? = null
)