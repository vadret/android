package fi.kroon.vadret.presentation.aboutapp.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.databinding.AboutAppLibraryItemBinding
import fi.kroon.vadret.presentation.aboutapp.extension.onClickThrottled

class AboutAppLibraryAdapter constructor(
    private val onProjectUrlClicked: (Library) -> Unit,
    private val onSourceUrlClicked: (Library) -> Unit,
    private val onLicenseUrlClicked: (Library) -> Unit
) : RecyclerView.Adapter<AboutAppLibraryAdapter.AboutAppLibraryViewHolder>() {

    private val list: MutableList<Library> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutAppLibraryViewHolder =
        AboutAppLibraryViewHolder(
            AboutAppLibraryItemBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: AboutAppLibraryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class AboutAppLibraryViewHolder(private val itemBinding: AboutAppLibraryItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemBinding.apply {
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
            itemBinding.apply {
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