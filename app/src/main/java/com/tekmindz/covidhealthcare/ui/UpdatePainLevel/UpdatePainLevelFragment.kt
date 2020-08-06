package com.tekmindz.covidhealthcare.ui.UpdatePainLevel

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.databinding.FragmentUpdatePainLevelBinding
import com.tekmindz.covidhealthcare.repository.requestModels.UpdatePainLevel
import com.tekmindz.covidhealthcare.repository.responseModel.EditProfileResponse
import com.tekmindz.covidhealthcare.utills.Resource
import com.tekmindz.covidhealthcare.utills.Utills
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.OnProgressChangedListenerAdapter
import kotlinx.android.synthetic.main.fragment_update_pain_level.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var mUpdatePainLevelViewModel: UpdatePainLevelViewModel
private var mProgressDialog: ProgressDialog? = null
private lateinit var patientId: String

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdatePainLevelFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePainLevelBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var patientProgress = 0
    var painLevel = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            patientId = it.getString(PATIENT_ID)!!
            val pain = it.getString("painLevel")
            if (pain != null) {
                painLevel = pain.toFloat()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_update_pain_level, container, false
        )
        val view: View = binding.root
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initializing progress dailog
        mProgressDialog =
            activity?.let { Utills.initializeProgressBar(it, R.style.AppTheme_WhiteAccent) }

        mUpdatePainLevelViewModel =
            ViewModelProviders.of(this).get(UpdatePainLevelViewModel::class.java)
        Log.e("painLevel", "$painLevel")
        if (painLevel != null) {
            seek_level_Of_pain.setProgress(painLevel.toFloat())
        }
        // seek_level_Of_pain.setProgress(5f)
        seek_level_Of_pain.onProgressChangedListener = object : OnProgressChangedListenerAdapter() {
            override fun onProgressChanged(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {
                patientProgress = progress
                //  Log.e("on progress changed", "$progress, $progressFloat")
                val color: Int
                color = if (progress <= 3) {
                    ContextCompat.getColor(requireActivity(), R.color.green)
                } else if (progress <= 8) {
                    ContextCompat.getColor(requireActivity(), R.color.amber)
                } else {
                    ContextCompat.getColor(requireActivity(), R.color.red)
                }
                bubbleSeekBar.setSecondTrackColor(color)
                bubbleSeekBar.setThumbColor(color)
                bubbleSeekBar.setBubbleColor(color)
            }

            override fun getProgressOnActionUp(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float
            ) {}

            override fun getProgressOnFinally(
                bubbleSeekBar: BubbleSeekBar,
                progress: Int,
                progressFloat: Float,
                fromUser: Boolean
            ) {}
        }

        mUpdatePainLevelViewModel.response().observe(requireActivity(), Observer {
            when (it) {
                is Resource<EditProfileResponse> -> {
                    handlePatientDetails(it)
                }
            }
        })
        bt_save.setOnClickListener {
            showProgressBar()
            mUpdatePainLevelViewModel.updateObservationType(
                UpdatePainLevel(
                    patientId,
                    Constants.OBSERVATION_TYPE_PAIN_LEVEL,
                    patientProgress.toString(),
                    Utills.getCurrentDate()
                )
            )
        }


    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.updateProfile)
        if (item != null) item.isVisible = false
    }

    private fun handlePatientDetails(it: Resource<EditProfileResponse>) {

        when (it.status) {
            Resource.Status.LOADING -> showProgressBar()
            Resource.Status.SUCCESS -> successUpdate(it.data!!)
            Resource.Status.ERROR -> showError(it.exception!!)

        }
    }

    private fun showProgressBar() {
        mProgressDialog?.show()
    }

    private fun successUpdate(editProfileResponse: EditProfileResponse) {

        hideProgressbar()
        if (editProfileResponse.statusCode ==200 ||editProfileResponse.statusCode ==201) {
            showMessage(editProfileResponse.message)
        }

    }

    private fun showError(error: String) {
        showMessage(error)
        hideProgressbar()
    }


    //hide the progressbar
    private fun hideProgressbar() {
        mProgressDialog?.hide()
    }



    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashFragment.
         */
        // TODO: Rename and change types and number of parameters

    }
}