package fi.kroon.vadret.presentation.aboutapp.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import fi.kroon.vadret.utils.extensions.toGone
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_about_item.view.*
import javax.inject.Inject

@AboutAppFeatureScope
class AboutAppAboutAdapter @Inject constructor(
    private val onAboutAppAboutInfoItemClickSubject: PublishSubject<AboutInfo>
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
            itemView.setOnClickListener {
                onAboutAppAboutInfoItemClickSubject
                    .onNext(list[adapterPosition])
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

    fun updateList(list: List<AboutInfo>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}