package com.saleem.gpacalc.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.saleem.gpacalc.R
import dagger.hilt.android.AndroidEntryPoint
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


@AndroidEntryPoint
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val linkedInElement = Element()
        linkedInElement.apply {
            setTitle(getString(R.string.saleem))
            setIconDrawable(R.drawable.ic_linkedin_logo)
            setOnClickListener {
                val linkedinIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.linkedin_page))
                )
                startActivity(linkedinIntent)
            }
        }

        val versionElement = Element()
        versionElement.setTitle(getString(R.string.version));


        return AboutPage(context)
            .isRTL(false)
            .setDescription(getString(R.string.about_text))
            .addGroup(getString(R.string.contact))
            .addItem(linkedInElement)
            .addTwitter("S6AVII", getString(R.string.twitter_name))
            .addGitHub("S6AVI", getString(R.string.github_name))
            .addGroup(getString(R.string.app_version))
            .addItem(versionElement)
            .create()
    }
}