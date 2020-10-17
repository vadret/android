package fi.kroon.vadret.presentation.aboutapp.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.presentation.aboutapp.extension.onClickThrottled
import fi.kroon.vadret.util.extension.toGone
import kotlinx.android.synthetic.main.about_app_about_item.view.*

class AboutAppAboutAdapter constructor(
    private val onAboutAppAboutInfoItemClicked: (AboutInfo) -> Unit
) : RecyclerView.Adapter<AboutAppAboutAdapter.ViewHolder>() {

    private val list: MutableList<AboutInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.about_app_about_item,
                    parent,
                    false
                )
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.onClickThrottled {
                onAboutAppAboutInfoItemClicked(list[adapterPosition])
            }
        }

        fun bind(entity: AboutInfo) {
            itemView.apply {
                with(entity) {
                    iconResourceId?.let { id ->
                        aboutAppAboutInfoItemIconImageView.setImageResource(id)
                    }

                    setTextOrMakeGone(itemView.aboutAppLibraryItemTitleTextView, titleResourceId)
                    setTextOrMakeGone(itemView.aboutAppAboutInfoItemHintTextView, hintResourceId)
                }
            }
        }

        private fun setTextOrMakeGone(textView: TextView, resId: Int?) =
            resId?.let { id: Int ->
                textView.setText(id)
            } ?: textView.toGone()
    }

    fun updateList(updatedList: List<AboutInfo>) {
        list.clear()
        list.addAll(updatedList)
        notifyDataSetChanged()
    }
}