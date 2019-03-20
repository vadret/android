package fi.kroon.vadret.presentation.aboutapp.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_library_item.view.*
import javax.inject.Inject
import javax.inject.Named

@AboutAppFeatureScope
class AboutAppLibraryAdapter @Inject constructor(
    @Named("projectUrl")
    private val onOnProjectUrlClickSubject: PublishSubject<Library>,
    @Named("sourceUrl")
    private val onSourceUrlClickSubject: PublishSubject<Library>,
    @Named("licenseUrl")
    private val onLicenseUrlClickSubject: PublishSubject<Library>
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
                aboutAppLibraryItemProjectUrlButton.setOnClickListener {
                    onOnProjectUrlClickSubject
                        .onNext(
                            list[adapterPosition]
                        )
                }

                aboutAppLibraryItemLicenseUrlButton.setOnClickListener {
                    onLicenseUrlClickSubject
                        .onNext(
                            list[adapterPosition]
                        )
                }

                aboutAppLibraryItemUrlButton.setOnClickListener {
                    onSourceUrlClickSubject
                        .onNext(
                            list[adapterPosition]
                        )
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