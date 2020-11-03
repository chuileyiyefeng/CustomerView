package com.example.rico.customerview.activity

import com.example.rico.customerview.R
import kotlinx.android.synthetic.main.activity_loading.*

/**
 * create by pan yi on 2020/10/21
 * desc :
 */
class LoadingActivity : BaseActivity() {
    override fun bindLayout(): Int {
        return R.layout.activity_loading
    }

    override fun doBusiness() {
        tv_start.setOnClickListener {
            loading_view.startLoading()
        }
        tv_stop.setOnClickListener {
            loading_view.stopLoading()
        }
    }
}