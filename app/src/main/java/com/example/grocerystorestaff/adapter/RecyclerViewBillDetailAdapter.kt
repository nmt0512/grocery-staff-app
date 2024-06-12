package com.example.grocerystorestaff.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerystorestaff.R
import com.example.grocerystorestaff.databinding.ItemBillItemBinding
import com.example.grocerystorestaff.model.response.bill.BillItemResponse
import com.example.grocerystorestaff.utils.NumberConverterUtil

class RecyclerViewBillDetailAdapter(private val billItemResponseList: List<BillItemResponse>) :
    RecyclerView.Adapter<RecyclerViewBillDetailAdapter.BillDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill_item, parent, false)
        val binding = ItemBillItemBinding.bind(view)
        return BillDetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return billItemResponseList.size
    }

    override fun onBindViewHolder(holder: BillDetailViewHolder, position: Int) {
        holder.bind(billItemResponseList[position])
    }

    inner class BillDetailViewHolder(val binding: ItemBillItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(billItem: BillItemResponse) {
            if (!billItem.productResponse.images.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(billItem.productResponse.images[0])
                    .centerCrop()
                    .into(binding.imageViewProduct)
            }
            binding.txtProductName.text = billItem.productResponse.name
            binding.txtQuantity.text = "x${billItem.quantity}"
            binding.txtPrice.text =
                "${NumberConverterUtil.convertNumberToStringWithDot(billItem.price)} Đ"
        }
    }
}