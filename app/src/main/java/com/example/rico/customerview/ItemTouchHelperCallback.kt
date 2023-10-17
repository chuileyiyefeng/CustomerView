package com.example.rico.customerview

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class ItemTouchHelperCallback(private val list: ArrayList<String>, private val itemMoveCallback: ItemMoveCallback) :
        ItemTouchHelper.Callback() {

    override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder.adapterPosition == 0||list[viewHolder.adapterPosition].isEmpty()) {
            return makeMovementFlags(0, 0)
        }
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {
        if (recyclerView.layoutManager !is GridLayoutManager) {
            return false
        }
        // 如果拖拽的和目标是第一个item，不允许拖拽
        if (viewHolder.adapterPosition == 0 || target.adapterPosition == 0
                || list[viewHolder.adapterPosition].isEmpty()) {
            return false
        }
        // 计算行和列
        val spanCount = (recyclerView.layoutManager as GridLayoutManager).spanCount
        val sourcePosition = viewHolder.adapterPosition
        val targetPosition = target.adapterPosition
        val sourceRow = sourcePosition / spanCount
        val targetRow = targetPosition / spanCount
        var newTargetPosition = sourcePosition

        // 如果不在同一行，需要计算新位置
        if (sourceRow != targetRow) {
            // 计算新位置
            newTargetPosition = sourcePosition + (targetRow - sourceRow) * spanCount
            itemMoveCallback.onItemMoved(sourcePosition, newTargetPosition)
        } else {
            // 在同一行内，可以插入到目标位置
            val sourceColumn = sourcePosition % spanCount
            val targetColumn = targetPosition % spanCount

            // 计算新的位置
            val newSourcePosition = targetRow * spanCount + sourceColumn
            newTargetPosition = sourceRow * spanCount + targetColumn
            itemMoveCallback.onItemMoved(newSourcePosition, newTargetPosition)
        }

        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            // 拖拽操作结束，可以在这里处理拖拽操作结束的逻辑
            // 在这里可以执行拖拽操作结束后的处理，例如保存数据或执行其他操作
            itemMoveCallback.dragEnd()
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            // 拖拽操作开始，可以在这里处理拖拽操作开始的逻辑
            // 在这里可以开始一些视觉操作，比如让被拖拽的 item 变小，或者改变背景颜色
            itemMoveCallback.dragStart()
        }
    }


    override fun canDropOver(
            recyclerView: RecyclerView,
            current: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
    ): Boolean {
        // 如果目标位置是第一个item，不允许拖拽
        if (target.adapterPosition == 0 || list[target.adapterPosition].isEmpty()) {
            return false
        }

        // 其他情况下，可以进行拖拽操作
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 这里可以处理滑动事件，如果不需要滑动功能可以不实现
    }


    interface ItemMoveCallback {
        fun onItemMoved(sourcePosition: Int, targetPosition: Int) //交换数据是否在同一行
        fun dragStart()
        fun dragEnd()
    }
}