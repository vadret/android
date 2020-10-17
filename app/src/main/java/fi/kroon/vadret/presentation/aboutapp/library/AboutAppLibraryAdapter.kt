package fi.kroon.vadret.presentation.aboutapp.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.presentation.aboutapp.extension.onClickThrottled
import kotlinx.android.synthetic.main.about_app_library_item.view.*

class AboutAppLibraryAdapter constructor(
    private val onProjectUrlClicked: (Library) -> Unit,
    private val onSourceUrlClicked: (Library) -> Unit,
    private val onLicenseUrlClicked: (Library) -> Unit
) : RecyclerView.Adapter<AboutAppLibraryAdapter.ViewHolder>() {

    private val list: MutableList<Library> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.about_app_library_item, parent, false)
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                aboutAppLibraryItemProjectUrlButton
                    .onClickThrottled {
                        onProjectUrlClicked(list[adapterPosition])
                    }

                aboutAppLibraryItemLicenseUrlButton
                    .onClickThrottled {
                        onLicenseUrlClicked(list[adapterPosition])
                    }

                aboutAppLibraryItemUrlButton
                    .onClickThrottled {
                        onSourceUrlClicked(list[adapterPosition])
                    }
            }
        }

        fun bind(library: Library) {
            itemView.apply {
                aboutAppLibraryItemTitleTextView.text = library.title
                aboutAppLibraryItemDescriptionTextView.text = library.description
            }
        }
    }

    fun updateList(itemList: List<Library>) {
        list.clear()
        list.addAll(itemList)
        notifyDataSetChanged()
    }
}