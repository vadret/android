package fi.kroon.vadret.presentation.aboutapp.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.library.local.LibraryEntity
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppScope
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_library_item.view.*
import javax.inject.Inject
import javax.inject.Named

@AboutAppScope
class AboutAppLibraryAdapter @Inject constructor(
    @Named("projectUrl")
    private val onOnProjectUrlClickSubject: PublishSubject<LibraryEntity>,
    @Named("sourceUrl")
    private val onSourceUrlClickSubject: PublishSubject<LibraryEntity>,
    @Named("licenseUrl")
    private val onLicenseUrlClickSubject: PublishSubject<LibraryEntity>
) : RecyclerView.Adapter<AboutAppLibraryAdapter.ViewHolder>() {

    private val list = mutableListOf<LibraryEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.about_app_library_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

            with(itemView) {
                aboutAppLibraryItemProjectUrlButton.setOnClickListener {
                    onOnProjectUrlClickSubject.onNext(list[adapterPosition])
                }

                aboutAppLibraryItemLicenseUrlButton.setOnClickListener {
                    onLicenseUrlClickSubject.onNext(list[adapterPosition])
                }

                aboutAppLibraryItemUrlButton.setOnClickListener {
                    onSourceUrlClickSubject.onNext(list[adapterPosition])
                }
            }
        }

        fun bind(libraryEntity: LibraryEntity) {
            itemView.aboutAppLibraryItemTitleTextView.text = libraryEntity.title
            itemView.aboutAppLibraryItemDescriptionTextView.text = libraryEntity.description
        }
    }

    fun updateList(itemList: List<LibraryEntity>) {
        list.clear()
        list.addAll(itemList)
        notifyDataSetChanged()
    }
}