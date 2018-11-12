package com.ghstudios.android.features.meta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghstudios.android.GenericActivity
import com.ghstudios.android.components.IconLabelTextCell
import com.ghstudios.android.components.TitleBarCell
import com.ghstudios.android.mhgendatabase.BuildConfig
import com.ghstudios.android.mhgendatabase.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutActivity : GenericActivity() {
    override fun getSelectedSection() = 0

    override fun createFragment(): Fragment {
        return AboutFragment()
    }
}

class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.about)
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set version string
        val versionName = BuildConfig.VERSION_NAME
        val titlebar = view.findViewById<TitleBarCell>(R.id.title)
        titlebar.setAltTitleText(getString(R.string.about_version, versionName))

        // Make the links clickable
        activateLinks(this.about_layout)
    }

    /**
     * Recursive function to find all IconLabelTextCell entries and activate the links
     * in tags or labels. Works recursively.
     */
    private fun activateLinks(viewGroup: ViewGroup) {
        for (i in 0..viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is IconLabelTextCell) {
                child.setOnClickListener {
                    val href = child.tag as? String
                    if (!href.isNullOrBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(href))
                        startActivity(intent)
                    }
                }
            } else if (child is ViewGroup) {
                activateLinks(child) // recursive call
            }
        }
    }
}