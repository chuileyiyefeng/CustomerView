package com.example.rico.customerview.activity

import android.annotation.SuppressLint
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import com.example.rico.customerview.R
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import kotlinx.android.synthetic.main.activity_badge_drawable.*


/**
 * @Description:
 * @Author: pan yi
 * @Date: 2022/6/6
 */
class BadgeDrawableTestActivity : BaseActivity() {
    override fun bindLayout(): Int {
        return R.layout.activity_badge_drawable
    }

    @SuppressLint("RestrictedApi")
    override fun doBusiness() {
        tvBadge.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val badgeDrawable = BadgeDrawable.create(this@BadgeDrawableTestActivity)
                badgeDrawable.number = 32
                badgeDrawable.badgeGravity = BadgeDrawable.TOP_END
                badgeDrawable.backgroundColor =
                    ContextCompat.getColor(this@BadgeDrawableTestActivity, R.color.red)
                BadgeUtils.attachBadgeDrawable(badgeDrawable, tvBadge, fl_content)
                tvBadge.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }
}