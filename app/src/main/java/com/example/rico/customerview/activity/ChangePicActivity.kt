package com.example.rico.customerview.activity

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.rico.customerview.ItemTouchHelperCallback
import com.example.rico.customerview.R
import com.example.rico.customerview.adapter.ChangePhotoAdapter
import java.util.*


/**
 * @Description:
 * @Author: pan yi
 * @Date: 2023/10/11
 */
class ChangePicActivity : BaseActivity2() {
    var rvChangePic: RecyclerView? = null
    var adapter: ChangePhotoAdapter? = null

    override fun bindLayout(): Int {
        return R.layout.activity_change_pic
    }

    override fun initView() {
        rvChangePic = findViewById(R.id.rv_change_pic)
        rvChangePic?.let {
            it.post {
                it.layoutManager = GridLayoutManager(this, 3)
                adapter = ChangePhotoAdapter(this, (it.measuredWidth / 3 - 100))
                rvChangePic?.adapter = adapter
                val list = arrayListOf<String>()

                repeat(4) {
//                    list.add("http://head.expertol.cn/android/819011689303309876.jpg")
                    list.add("下标 $it 数据")
                }
                list.add("")
                adapter?.addItem(list)
                adapter?.run {
                    val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(list, object : ItemTouchHelperCallback.ItemMoveCallback {
                        override fun onItemMoved(sourcePosition: Int, targetPosition: Int) {
                            var newTargetPosition = targetPosition
                            if (newTargetPosition == 0) {
                                newTargetPosition = 1
                            }
                            if (data[newTargetPosition].isNullOrEmpty()
                                    && data.size > 2) {
                                newTargetPosition = data.size - 2
                            }
                            // 在这里更新数据源中的数据顺序
                            val sourceData = data[sourcePosition]
                            data.remove(sourceData)
                            data.add(newTargetPosition, sourceData)
                            // 刷新RecyclerView以反映数据变化
                            notifyItemMoved(sourcePosition, newTargetPosition)
                            Log.e(TAG, "initView: 移动位置为 源-$sourcePosition  去-$newTargetPosition")

                        }

                        override fun dragStart() {
                            it.setBackgroundColor(resources.getColor(R.color.translucent_black_90))
                            notifyItemChanged(0, true)
                            if (data.last().isNullOrEmpty()) {
                                notifyItemChanged(data.size-1, true)
                            }
                        }

                        override fun dragEnd() {
                            it.setBackgroundColor(resources.getColor(R.color.transparent))
                            notifyItemChanged(0, false)
                            if (data.last().isNullOrEmpty()) {
                                notifyItemChanged(data.size-1, false)
                            }
                        }

                    }))
                    itemTouchHelper.attachToRecyclerView(it)
                }


            }
        }
        findViewById<TextView>(R.id.tv_get_data).setOnClickListener {
            Log.e(TAG, "initView: " + adapter?.data.toString())
        }

    }
}