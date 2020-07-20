package com.tekmindz.covidhealthcare.ui.help

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.application.App
import com.tekmindz.covidhealthcare.constants.Constants
import com.tekmindz.covidhealthcare.constants.UserTypes


class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val youTubePlayerView: YouTubePlayerView = requireActivity().findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady( youTubePlayer: YouTubePlayer) {
                val videoId ="qWCrnwzk9kA"//"S0Q4gqBUs7c"
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item: MenuItem = menu.findItem(R.id.sos)
        item.isVisible = getUserType() == UserTypes.PATIENT.toString()
    }
    fun getUserType(): String {
        return App.mSharedPrefrenceManager.getValueString(Constants.PREF_USER_TYPE)?: UserTypes.HEALTH_WORKER.toString()
    }
 }