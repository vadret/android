package fi.kroon.vadret.presentation.weatherforecast.autocomplete

import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem

class AutoCompleteDiffUtil(
    private val oldList: List<AutoCompleteItem>,
    private val newList: List<AutoCompleteItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }
}