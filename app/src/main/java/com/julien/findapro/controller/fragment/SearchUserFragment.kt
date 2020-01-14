package com.julien.findapro.controller.fragment


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View.OnTouchListener
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.julien.findapro.R
import com.julien.findapro.Utils.Communicator
import kotlinx.android.synthetic.main.fragment_search_user.view.*


/**
 * A simple [Fragment] subclass.
 */
class SearchUserFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var comm: Communicator

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_search_user, null)

            comm = activity as Communicator

            //configureButtonDate(view)

            view.fragment_search_user_seekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seek: SeekBar,
                    progress: Int, fromUser: Boolean
                ) {
                    view.fragment_search_user_distance.text = seek.progress.toString() + " km"
                }

                override fun onStartTrackingTouch(seek: SeekBar) {
                    // write custom code for progress is started
                }

                override fun onStopTrackingTouch(seek: SeekBar) {
                }
            })

            view.fragment_search_user_ratingbar.setOnRatingBarChangeListener { ratingBar, fl, b ->
                view.fragment_search_user_rating_textview.text = ratingBar.rating.toString() + "/5"
            }

            view.fragment_search_user_button.setOnClickListener {
                comm.passData(
                    view.fragment__search_user_spinner.selectedItem.toString(),
                    view.fragment_search_user_seekBar.progress.toFloat(),
                    view.fragment_search_user_ratingbar.rating.toDouble()
                )
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }

            builder.setView(view)



            builder.create()


        } ?: throw IllegalStateException("Activity cannot be null")


    }




}
