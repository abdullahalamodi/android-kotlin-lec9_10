package com.abdullahalamodi.criminalintent

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File
import java.util.*

private const val ARG_IMAGE = "image"

class CrimeImageDialogFragment : DialogFragment() {

    private lateinit var imageView:ImageView;
    private lateinit var photoFile: File;


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        photoFile = arguments?.getSerializable(ARG_IMAGE) as File
        val view = activity?.layoutInflater?.inflate(R.layout.crime_image_dialog,null)
        imageView = view?.findViewById(R.id.crime_image_view) as ImageView;
        updatePhotoView();
        return AlertDialog.Builder(requireContext(),R.style.ThemeOverlay_AppCompat_Dialog_Alert)
            .setView(view)
            .setTitle("crime image")
            .create()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageDrawable(null)
        }
    }


    companion object {
        fun newInstance(photoFile: File): CrimeImageDialogFragment {
            val args = Bundle().apply {
                putSerializable(ARG_IMAGE, photoFile)
            }
            return CrimeImageDialogFragment().apply {
                arguments = args
            }
        }
    }


}