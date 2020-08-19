package com.tekmindz.covidhealthcare.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.repository.responseModel.UserInfoBody
import com.tekmindz.covidhealthcare.utills.Utills
import kotlinx.android.synthetic.main.fragment_analytics_tab.*


class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val youTubePlayerView: YouTubePlayerView =
            requireActivity().findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)
        /* val youTubePlayerFragment =
             supportFragmentManager.findFragmentById(R.id.youtubeFragment) as YouTubePlayerSupportFragment?
         youTubePlayerFragment?.initialize("YOUR_API_KEY", this)*/
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "qWCrnwzk9kA"//"S0Q4gqBUs7c"
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
        //  webview_player_view.settings.javaScriptEnabled = true
        //webview_player_view.loadUrl("https://www.youtube.com/embed/qWCrnwzk9kA")

        if (Utills.isPatient(App.mSharedPrefrenceManager.get<UserInfoBody>(Constants.PREF_USER_INFO))) {

            bt_sos.visibility = View.VISIBLE
        } else {
            bt_sos.visibility = View.GONE
        }

        bt_sos.setOnClickListener {
            Utills.callPhoneNumber(requireActivity())
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.updateProfile)
        if (item != null) item.isVisible = false
    }


}