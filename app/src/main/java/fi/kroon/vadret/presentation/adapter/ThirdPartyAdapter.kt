package fi.kroon.vadret.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.ThirdParty
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.presentation.adapter.listener.AboutAdapterOnRowClickInterface
import kotlinx.android.synthetic.main.third_party_list_row.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

@VadretApplicationScope
class ThirdPartyAdapter @Inject constructor() : RecyclerView.Adapter<ThirdPartyAdapter.ViewHolder>() {

    internal var collection: List<ThirdParty> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var clickListener: AboutAdapterOnRowClickInterface? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(thirdParty: ThirdParty, clickListener: AboutAdapterOnRowClickInterface?) {
            itemView.title.text = thirdParty.title
            itemView.description.text = thirdParty.description

            itemView.projectUrl.setOnClickListener { clickListener?.onProjectClick(thirdParty.page) }
            itemView.licenseUrl.setOnClickListener { clickListener?.onLicenseClick(thirdParty.license) }
            itemView.sourceUrl.setOnClickListener { clickListener?.onSourceClick(thirdParty.source) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.third_party_list_row, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(collection[position], clickListener)

    override fun getItemCount(): Int = collection.size

    fun setListener(listener: AboutAdapterOnRowClickInterface) {
        this.clickListener = listener
    }

    fun unregisterListener() {
        this.clickListener = null
    }
}