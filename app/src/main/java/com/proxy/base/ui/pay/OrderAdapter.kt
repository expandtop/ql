package com.proxy.base.ui.pay

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proxy.base.R
import com.proxy.base.data.PayOrder
import com.proxy.base.databinding.ItemOrderBinding
import ex.ss.lib.base.adapter.BaseItemAdapter
import ex.ss.lib.tools.extension.formatCent
import ex.ss.lib.tools.extension.formatSecondDate

/**
 * 2024/9/9
 */
val PayOrderDiffCallback = object : DiffUtil.ItemCallback<PayOrder>() {
    override fun areItemsTheSame(oldItem: PayOrder, newItem: PayOrder): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PayOrder, newItem: PayOrder): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }
}

interface OnOrderAction {
    fun onOrderPay(item: PayOrder)
    fun onOrderClose(item: PayOrder)
}

class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

class OrderAdapter : PagingDataAdapter<PayOrder, OrderViewHolder>(PayOrderDiffCallback) {

    private var orderAction: OnOrderAction? = null

    fun setOnOrderAction(action: OnOrderAction) {
        this.orderAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val data = getItem(position) ?: return
        val binding = holder.binding
        binding.tvTitle.text = data.plan.name
        binding.tvPriceValue.text = data.total_amount.formatCent() + "¥"
        binding.tvOrderIdValue.text = data.trade_no
        binding.tvTimeValue.text = data.created_at.formatSecondDate()
        binding.tvPay.setOnClickListener {}
        binding.tvPay.isVisible = false
        binding.tvCancel.isVisible = false
        //0待支付1开通中2已取消3已完成4已折抵
        when (data.status) {
            0 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_wait)
                binding.tvOrderStatue.text = "待支付"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FFFFC737"))
                binding.tvPay.isVisible = true
                binding.tvCancel.isVisible = true
                binding.tvPay.setOnClickListener {
                    orderAction?.onOrderPay(data)
                }
                binding.tvCancel.setOnClickListener {
                    orderAction?.onOrderClose(data)
                }
            }

            1 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_open)
                binding.tvOrderStatue.text = "开通中"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FF00C1FE"))
            }

            2 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_cancel)
                binding.tvOrderStatue.text = "已取消"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FFFF6864"))
            }

            3 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_done)
                binding.tvOrderStatue.text = "已完成"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FF00FE91"))
            }

            4 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_coupon)
                binding.tvOrderStatue.text = "已折抵"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FF6A6A6A"))
            }
        }
    }
}

class OrderAdapter1 : BaseItemAdapter<PayOrder, ItemOrderBinding>() {

    private var orderAction: OnOrderAction? = null

    fun setOnOrderAction(action: OnOrderAction) {
        this.orderAction = action
    }

    override fun onBindViewHolder(binding: ItemOrderBinding, position: Int) {
        val data = getItem(position)
        binding.tvTitle.text = "${data.plan.name}- ￥${data.total_amount.formatCent()}"
        binding.tvTimeValue.text = data.created_at.formatSecondDate()
        binding.tvPay.setOnClickListener {}
        binding.tvPay.isVisible = false
        binding.tvCancel.isVisible = false
        //0待支付1开通中2已取消3已完成4已折抵
        when (data.status) {
            0 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_wait)
                binding.tvOrderStatue.text = "待支付"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FFFFC737"))
                binding.tvPay.isVisible = true
                binding.tvCancel.isVisible = true
                binding.tvPay.setOnClickListener {
                    orderAction?.onOrderPay(data)
                }
                binding.tvCancel.setOnClickListener {
                    orderAction?.onOrderClose(data)
                }
            }

            1 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_open)
                binding.tvOrderStatue.text = "开通中"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FF00C1FE"))
            }

            2 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_cancel)
                binding.tvOrderStatue.text = "已取消"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FFFF6864"))
            }

            3 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_done)
                binding.tvOrderStatue.text = "已完成"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FF00FE91"))
            }

            4 -> {
                binding.ivOrderStatue.setImageResource(R.drawable.order_coupon)
                binding.tvOrderStatue.text = "已折抵"
                binding.tvOrderStatue.setTextColor(Color.parseColor("#FF6A6A6A"))
            }
        }
    }

    override fun viewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemOrderBinding {
        return ItemOrderBinding.inflate(inflater, parent, false)
    }
}
