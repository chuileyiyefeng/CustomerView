package com.example.rico.customerview.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rico.customerview.R
import com.example.rico.util.StatusBarUtil

abstract class BaseActivity2 : AppCompatActivity() {
     var TAG = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindLayout())
        initView()
        StatusBarUtil.setStatusBarColor(this, R.color.white)
        StatusBarUtil.setStatusTextDark(this, true)
    }

    protected abstract fun bindLayout(): Int
    protected abstract fun initView()


    /**
     * 启动Fragment
     */
    protected fun startFragment(id: Int, fragment: Fragment?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(id, fragment!!)
        fragmentTransaction.commit()
    }
}