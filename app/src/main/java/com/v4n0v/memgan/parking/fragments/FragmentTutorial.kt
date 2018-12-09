package com.v4n0v.memgan.parking.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import com.v4n0v.memgan.parking.R
import com.v4n0v.memgan.parking.components.ImageModel
import com.v4n0v.memgan.parking.components.OnSlideChanged
import com.v4n0v.memgan.parking.components.SlidingImageAdapter
import com.v4n0v.memgan.parking.mvp.views.MainView
import com.v4n0v.memgan.parking.utils.Animator.Companion.changeColorAnimation
import kotlinx.android.synthetic.main.fragment_tutorial.*
import timber.log.Timber


class FragmentTutorial : BaseFragment() {
    private var imageModelArrayList = mutableListOf<ImageModel>()
    private val myImageList = intArrayOf(R.drawable.slide_1, R.drawable.slide_2, R.drawable.slide_3, R.drawable.slide_4)
    private var previousPos: Int? = null
    lateinit var activity: MainView
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as MainView
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageModelArrayList.clear()
        imageModelArrayList.addAll(populateList())
        btnSkip.setOnClickListener {
            activity.showSwitch()
            activity.markTutorialViewed()
            activity.checkState()
        }

        val images = mutableListOf<ImageView>()
        val lp = LinearLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.size_5dp), resources.getDimensionPixelSize(R.dimen.size_5dp))
        lp.marginStart = 5
        lp.marginEnd = 5
        lp.weight = 1f

        val root = LinearLayout(activity as Context)
        root.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        root.orientation = HORIZONTAL
        root.gravity = Gravity.CENTER

        for (i in 0 until imageModelArrayList.size) {
            val iv = ImageView(activity as Context)
            iv.setBackgroundColor(ContextCompat.getColor(activity as Context, R.color.colorPrimary))
            iv.layoutParams = lp
            root.addView(iv)
            images.add(iv)

        }
        couterContainer.addView(root)
        val containerPrams = LinearLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.size_5dp), resources.getDimensionPixelSize(R.dimen.size_5dp))
        containerPrams.gravity = Gravity.CENTER
        // couterContainer.layoutParams= containerPrams

        couterContainer.gravity = Gravity.CENTER
        imagePager.adapter = SlidingImageAdapter(activity as Context, imageModelArrayList)
        imagePager.addOnPageChangeListener(object : OnSlideChanged() {
            override fun onChange(pos: Int) {
                if (previousPos != null)
                    images[previousPos!!].setBackgroundColor(ContextCompat.getColor(activity as Context, R.color.colorPrimary))
                images[pos].setBackgroundColor(ContextCompat.getColor(activity as Context, R.color.colorAccent))
                previousPos = pos
            }
        })
    }

    private fun populateList(): List<ImageModel> {
        val list = mutableListOf<ImageModel>()
        for (i in 0 until myImageList.size) {
            val imageModel = ImageModel()
            imageModel.imageDrawable = myImageList[i]
            list.add(imageModel)
        }

        return list
    }

}