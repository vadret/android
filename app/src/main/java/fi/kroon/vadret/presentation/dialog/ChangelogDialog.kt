package fi.kroon.vadret.presentation.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import fi.kroon.vadret.R

class ChangelogDialog : DialogFragment() {
    companion object {
        const val TAG = "changelog"
    }

    private var message: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder
            .setMessage(message)
            .setTitle(R.string.changelog_dialog_title)
            .setPositiveButton(R.string.ok) { d, _ ->
                d.dismiss()
            }

        return builder.create()
    }

    fun setMessage(message: String) = apply {
        this.message = message
    }
}