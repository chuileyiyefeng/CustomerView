package com.example.rico.customerview.activity

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rico.customerview.R
import com.example.rico.customerview.activity.JumpLoadActivity2
import com.example.rico.customerview.adapter.FirstAdapter
import com.example.rico.customerview.bean.ItemInfo
import com.example.rico.customerview.view.JumpLoadView
import com.example.rico.customerview.view.JumpLoadView.LoadListener
import com.example.rico.customerview.view.MyItemDecoration
import kotlinx.android.synthetic.main.activity_jump_load.*
import kotlinx.android.synthetic.main.activity_jump_load.jump_load
import kotlinx.android.synthetic.main.activity_jump_load.rv
import kotlinx.android.synthetic.main.activity_jump_load2.*
import java.lang.ref.WeakReference
import java.util.*

/**
 * create by pan yi on 2020/10/13
 * desc : 刷新控件 结合SwipeRefreshLayout
 */
class JumpLoadActivity2 : BaseActivity() {
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
        return R.layout.activity_jump_load2
    }

    override fun doBusiness() {
        rv.addItemDecoration(MyItemDecoration())
        jump_load.connect(rv)
        rv.layoutManager = manager
        rv.adapter = adapter
        for (i in 0..9) {
            adapter.addItem(ItemInfo("这是item$i", null))
        }
        jump_load.setLoadListener(object : LoadListener {
            override fun loadMore() {
                handler.sendEmptyMessageDelayed(1, 300)
                swipe_re.isEnabled=false
            }

            override fun refresh() {
            }
        })
        adapter.addItemClick { position: Int -> Toast.makeText(this@JumpLoadActivity2, "点击了 $position", Toast.LENGTH_SHORT).show() }
        swipe_re.setOnRefreshListener {
            jump_load.canMove=false
            handler.sendEmptyMessageDelayed(0, 300)
        }
        jump_load.setInTouchListener(object : JumpLoadView.InTouchListener {
            override fun move() {
                swipe_re.isEnabled = false
            }

            override fun up() {
                swipe_re.isEnabled = true
            }

        })
    }

    class MyHandler internal constructor(activity: JumpLoadActivity2) : Handler(Looper.getMainLooper()) {
        private var reference: WeakReference<JumpLoadActivity2> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
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
                    reference.get()?.swipe_re?.isRefreshing = false
                    reference.get()?.jump_load?.canMove = true
                }
                1 -> {
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
                    reference.get()?.swipe_re?.isEnabled=true
                }
            }
            reference.get()?.jump_load?.reductionScroll()
        }

    }

    companion object {
        private var upItem = 0
    }
}