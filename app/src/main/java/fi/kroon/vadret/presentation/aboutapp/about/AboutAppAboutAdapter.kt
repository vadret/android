package fi.kroon.vadret.presentation.aboutapp.about

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.databinding.AboutAppAboutItemBinding
import fi.kroon.vadret.presentation.aboutapp.extension.onClickThrottled
import fi.kroon.vadret.util.extension.toGone

class AboutAppAboutAdapter constructor(
    private val onAboutAppAboutInfoItemClicked: (AboutInfo) -> Unit
) : RecyclerView.Adapter<AboutAppAboutAdapter.ViewHolder>() {

    private val list: MutableList<AboutInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AboutAppAboutItemBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    inner class ViewHolder(
        private val itemBinding: AboutAppAboutItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root.onClickThrottled {
                onAboutAppAboutInfoItemClicked(list[adapterPosition])
            }
        }

        fun bind(entity: AboutInfo) {
            itemBinding.apply {
                with(entity) {
                    iconResourceId?.let { id ->
                        aboutAppAboutInfoItemIconImageView.setImageResource(id)
                    }

                    setTextOrMakeGone(itemBinding.aboutAppLibraryItemTitleTextView, titleResourceId)
                    setTextOrMakeGone(itemBinding.aboutAppAboutInfoItemHintTextView, hintResourceId)
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