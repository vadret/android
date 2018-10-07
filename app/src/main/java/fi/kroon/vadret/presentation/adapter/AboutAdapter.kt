package fi.kroon.vadret.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.ThirdParty
import fi.kroon.vadret.di.scope.VadretApplicationScope
import kotlinx.android.synthetic.main.about_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

@VadretApplicationScope
class AboutAdapter @Inject constructor() : RecyclerView.Adapter<AboutAdapter.ViewHolder>() {

    internal var collection: List<ThirdParty> by Delegates.observable(emptyList()) {
        _, _, _ -> notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(thirdParty: ThirdParty) {

            /*itemView.projectUrl.text = thirdParty.page*/
            // itemView.sourceUrl.text = thirdParty.SOURCE
            itemView.title.text = thirdParty.title
            itemView.description.text = thirdParty.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dependency = LayoutInflater.from(parent.context).inflate(R.layout.about_item, parent, false)
        return ViewHolder(dependency)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    override fun getItemCount(): Int = collection.size
}