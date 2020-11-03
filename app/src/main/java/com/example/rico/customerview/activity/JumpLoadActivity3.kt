package com.example.rico.customerview.activity

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rico.customerview.R
import com.example.rico.customerview.adapter.FirstAdapter
import com.example.rico.customerview.bean.ItemInfo
import com.example.rico.customerview.view.LoadRecyclerView
import com.example.rico.customerview.view.MyItemDecoration
import kotlinx.android.synthetic.main.activity_jump_load3.*
import java.lang.ref.WeakReference
import java.util.*

/**
 *  create by pan yi on 2020/10/17
 *  desc :  刷新控件，有显示状态
 */
class JumpLoadActivity3 : BaseActivity() {
    val adapter: FirstAdapter by lazy {
        FirstAdapter(this)
    }
    val handler: MyHandler by lazy {
        MyHandler(this)
    }
    private val manager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun bindLayout(): Int {
        return R.layout.activity_jump_load3
    }

    override fun doBusiness() {
        load_recycler.addItemDecoration(MyItemDecoration())
        load_recycler.setLayoutManager(manager)
        for (i in 0..9) {
            adapter.addItem(ItemInfo("这是item$i", null))
        }

        adapter.addItemClick { position: Int -> Toast.makeText(this, "点击了 $position", Toast.LENGTH_SHORT).show() }
        load_recycler.setAdapter(adapter)
        load_recycler.showData()
        load_recycler.setLoadDataListener(object : LoadRecyclerView.LoadDataListener {
            override fun loadMore() {
                handler.sendEmptyMessageDelayed(1, 300)
            }

            override fun refresh() {
                handler.sendEmptyMessageDelayed(0, 300)
            }

            override fun reload() {
                handler.sendEmptyMessageDelayed(2, 2000)
            }
        })
        tv_get_empty.setOnClickListener {
            handler.sendEmptyMessageDelayed(3, 300)
        }
    }

    class MyHandler internal constructor(activity: JumpLoadActivity3) : Handler(Looper.getMainLooper()) {
        private var reference: WeakReference<JumpLoadActivity3> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0, 2 -> {
                    reference.get()?.load_recycler?.showData()
                    // 下拉刷新
                    reference.get()?.adapter?.clearAllItem()
                    upItem = 0
                    var i = 0
                    while (i < 5) {
                        val time = System.currentTimeMillis()
                        val itemInfo = ItemInfo("这是刷新item 时间：$time", null)
                        reference.get()?.adapter?.addItem(itemInfo)
                        i++
                    }
                }
                1 -> {
                    reference.get()?.load_recycler?.showData()
                    // 上拉更多
                    if (upItem < 3) {
                        val list = ArrayList<ItemInfo>()
                        var i = 0
                        while (i < 5) {
                            val info = ItemInfo("这是上拉item$i", null)
                            list.add(info)
                            i++
                        }
                        reference.get()?.adapter?.addItem(list)
                        upItem++
                    } else {
                        Toast.makeText(reference.get(), "没有更多数据", Toast.LENGTH_SHORT).show()
                    }
                }
                3 -> {
                    reference.get()?.load_recycler?.showEmpty()
                }
            }
        }

    }

    companion object {
        private var upItem = 0
    }
}