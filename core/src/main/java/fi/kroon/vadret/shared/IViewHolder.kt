package fi.kroon.vadret.shared

import fi.kroon.vadret.data.common.model.IFilterable

interface IViewHolder {
    fun bind(entity: IFilterable)
}