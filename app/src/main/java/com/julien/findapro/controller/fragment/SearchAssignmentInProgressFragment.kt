package com.julien.findapro.controller.fragment


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.julien.findapro.R
import com.julien.findapro.utils.Communicator
import kotlinx.android.synthetic.main.fragment_search_assignment_in_progress.view.*


class SearchAssignmentInProgressFragment : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var comm: Communicator

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_search_assignment_in_progress, null)

            comm = activity as Communicator

            //start search and update recycler view
            view.fragment_search_assignment_inprogress_button.setOnClickListener {
                comm.passDataAssignmentInProgressList(
                    view.fragment_search_assignment_inprogress_spinner.selectedItem.toString()
                )
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }

            builder.setView(view)



            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")


    }


}
