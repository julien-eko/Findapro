package com.julien.findapro.controller.fragment


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment

import com.julien.findapro.R
import com.julien.findapro.Utils.Communicator
import kotlinx.android.synthetic.main.fragment_search_assignmen.view.*
import kotlinx.android.synthetic.main.fragment_search_user.view.*

/**
 * A simple [Fragment] subclass.
 */
class SearchAssignmenFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var comm: Communicator

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_search_assignmen, null)

            comm = activity as Communicator


            //seekbar
            view.fragment_search_assignment_seekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seek: SeekBar,
                    progress: Int, fromUser: Boolean
                ) {
                    view.fragment_search_assignment_distance.text = seek.progress.toString() + " km"
                }

                override fun onStartTrackingTouch(seek: SeekBar) {
                }

                override fun onStopTrackingTouch(seek: SeekBar) {
                }
            })

            //ratingbar
            view.fragment_search_assignment_ratingbar.setOnRatingBarChangeListener { ratingBar, fl, b ->
                view.fragment_search_assignment_rating_textview.text = ratingBar.rating.toString() + "/5"
            }

            //start search and update recycler view
            view.fragment_search_assignment_button.setOnClickListener {
                comm.passDataAssignmentList(

                    view.fragment_search_assignment_seekBar.progress.toFloat(),
                    view.fragment_search_assignment_ratingbar.rating.toDouble()
                )
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }

            builder.setView(view)



            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")


    }


}
