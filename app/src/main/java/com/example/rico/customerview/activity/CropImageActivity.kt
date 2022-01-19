package com.example.rico.customerview.activity

import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rico.customerview.R
import com.example.rico.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_crop_image.*

/**
 * @Description: 裁剪的Activity
 * @Author: pan yi
 * @Date: 2022/1/18
 */
class CropImageActivity : BaseActivity() {
    override fun bindLayout(): Int {
        return R.layout.activity_crop_image
    }

    override fun doBusiness() {
        StatusBarUtil.setStatusBarColor(this,R.color.black)
        StatusBarUtil.setStatusTextDark(this,false)
        val path = "https://images.pexels.com/photos/36717/amazing-animal-beautiful-beautifull.jpg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260"
        Glide.with(this).asBitmap().load(path).listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                ivThumb.postDelayed({
                    resource?.run {
                        cropView.post {
                            val cropWidth = cropView.measuredWidth
                            val cropHeight = cropView.measuredHeight
                            ivThumb.setCropWandH(cropWidth, cropHeight)
                        }
                    }
                }, 0)
                return false
            }
        }).into(ivThumb)

    }
}