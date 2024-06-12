package com.example.grocerystorestaff.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerystorestaff.R
import com.example.grocerystorestaff.databinding.ItemBillBinding
import com.example.grocerystorestaff.enums.BillStatus
import com.example.grocerystorestaff.model.response.bill.BillResponse
import com.example.grocerystorestaff.view.IHomeActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecyclerViewBillListAdapter(
    private val homeActivity: IHomeActivity,
    private val billResponseList: List<BillResponse>
) :
    RecyclerView.Adapter<RecyclerViewBillListAdapter.BillListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        val binding = ItemBillBinding.bind(view)
        return BillListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return billResponseList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: BillListViewHolder, position: Int) {
        holder.bind(billResponseList[position])
    }

    inner class BillListViewHolder(val binding: ItemBillBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(billResponse: BillResponse) {
            val billItemList = billResponse.billItems

            binding.txtBillId.text = billResponse.id.toString()

            if (!billItemList[0].productResponse.images.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(billItemList[0].productResponse.images?.get(0))
                    .centerCrop()
                    .into(binding.imageViewBill)
            }

            val firstBillItem = billItemList.first()
            binding.txtProduct1.text =
                "${firstBillItem.quantity}x ${firstBillItem.productResponse.name}"

            if (billItemList.size > 1) {
                val secondBillItem = billItemList[1]
                binding.txtProduct2.visibility = View.VISIBLE
                binding.txtProduct2.text =
                    "${secondBillItem.quantity}x ${secondBillItem.productResponse.name}"
                if (billItemList.size > 2) {
                    binding.txtOtherProductNumber.visibility = View.VISIBLE
                    binding.txtOtherProductNumber.text = "+ ${billItemList.size - 2}"
                }
            }

            billResponse.status?.let {
                var pickUpDate = billResponse.pickUpTime?.substringBefore(" ")
                val pickUpTime = billResponse.pickUpTime?.substringAfter(" ")
                if (it != BillStatus.COMPLETED) {
                    val dateFormat = "dd/MM/yyyy"

                    when (LocalDate.parse(pickUpDate, DateTimeFormatter.ofPattern(dateFormat))) {
                        LocalDate.now() -> {
                            pickUpDate = "Hôm nay"
                            binding.txtPickUpTime.setTextColor(
                                ContextCompat.getColor(binding.root.context, R.color.pick_up_red)
                            )
                        }

                        LocalDate.now().plusDays(1) -> {
                            pickUpDate = "Ngày mai"
                            binding.txtPickUpTime.setTextColor(
                                ContextCompat.getColor(binding.root.context, R.color.pick_up_orange)
                            )
                        }

                        LocalDate.now().plusDays(2) -> {
                            pickUpDate = "Ngày kia"
                            binding.txtPickUpTime.setTextColor(
                                ContextCompat.getColor(binding.root.context, R.color.pick_up_yellow)
                            )
                        }
                    }
                }
                binding.txtPickUpTime.text = "$pickUpDate $pickUpTime"

                binding.txtBillStatus.text = it.description
            }

            binding.root.setOnClickListener {
                homeActivity.bindBillDetailFragment(billResponse)
            }

        }
    }
}