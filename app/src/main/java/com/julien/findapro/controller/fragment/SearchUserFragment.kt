package com.julien.findapro.controller.fragment


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.julien.findapro.R
import com.julien.findapro.utils.Communicator
import kotlinx.android.synthetic.main.fragment_search_user.view.*


class SearchUserFragment : DialogFragment() {

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var comm: Communicator

        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_search_user, null)

            comm = activity as Communicator

            //seekbar
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

            //ratingbar
            view.fragment_search_user_ratingbar.setOnRatingBarChangeListener { ratingBar, _, _ ->
                view.fragment_search_user_rating_textview.text = ratingBar.rating.toString() + "/5"
            }

            //start search and update recycler view
            view.fragment_search_user_button.setOnClickListener {
                comm.passDataUserList(
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
