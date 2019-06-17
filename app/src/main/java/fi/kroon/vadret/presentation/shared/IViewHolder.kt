package fi.kroon.vadret.presentation.shared

import fi.kroon.vadret.presentation.warning.filter.model.IFilterable

interface IViewHolder {
    fun bind(entity: IFilterable)
}