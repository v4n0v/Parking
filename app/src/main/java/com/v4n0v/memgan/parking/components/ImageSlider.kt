package com.v4n0v.memgan.parking.components

import android.content.Context
import android.os.Parcelable
import android.view.ViewGroup
import java.nio.file.Files.size
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.v4n0v.memgan.parking.R


class ImageModel {
    var imageDrawable: Int = 0
}
abstract class  OnSlideChanged: ViewPager.OnPageChangeListener{

    abstract fun onChange(pos:Int)
    override fun onPageScrollStateChanged(p0: Int) {}

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        onChange(p0)

    }

    override fun onPageSelected(p0: Int) {}
}

class SlidingImageAdapter(context: Context, private val imageModelArrayList: List<ImageModel>) : PagerAdapter() {
    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return imageModelArrayList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.layout_sliding_image, view, false)!!

        val text =
                when (position) {
                    0 -> "Для включения сервиса, в меню выбора, вам необходимо найти ${view.context.getString(R.string.accessibility_service_label)}"
                    1 -> "Переключатель должен быть в положении \"включено\""
                    2 -> "Необходимо дать сервису разрешение на доступ к экрану смартфона"
                    3 -> "Если сервис включен, то можно вернуть обратно в приложение  ${view.context.getString(R.string.app_name)}"
                    else -> ""
                }

        val tv = imageLayout
                .findViewById(R.id.tvImageDescription) as TextView

        if (text.isEmpty())
            tv.visibility = View.GONE
        else
            tv.text = text

        tv.gravity = Gravity.CENTER
        val imageView = imageLayout
                .findViewById(R.id.image) as ImageView
        imageView.setImageResource(imageModelArrayList[position].imageDrawable)

        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}