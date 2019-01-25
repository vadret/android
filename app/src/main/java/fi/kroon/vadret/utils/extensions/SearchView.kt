package fi.kroon.vadret.utils.extensions

import androidx.appcompat.widget.SearchView
import timber.log.Timber

fun SearchView.onQuerySubmit(query: (String) -> Unit) =
    setOnQueryTextListener(
        object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(q: String): Boolean {
                Timber.d("Not hey")
                return false
            }

            override fun onQueryTextSubmit(q: String): Boolean {
                Timber.d("I am SUBMIT")
                query(q)
                return true
            }
        }
    )