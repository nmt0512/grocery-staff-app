package com.example.grocerystorestaff.view

import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerystorestaff.R
import com.example.grocerystorestaff.adapter.RecyclerViewBillDetailAdapter
import com.example.grocerystorestaff.base.BaseFragment
import com.example.grocerystorestaff.databinding.FragmentBillDetailBinding
import com.example.grocerystorestaff.enums.BillStatus
import com.example.grocerystorestaff.model.request.bill.UpdateBillStatusRequest
import com.example.grocerystorestaff.model.response.bill.BillResponse
import com.example.grocerystorestaff.utils.NumberConverterUtil
import com.example.grocerystorestaff.viewmodel.BillViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BillDetailFragment(
    private val homeActivity: IHomeActivity,
    private val billResponse: BillResponse
) :
    BaseFragment<FragmentBillDetailBinding>() {

    private lateinit var billViewModel: BillViewModel

    override fun getContentLayout(): Int {
        return R.layout.fragment_bill_detail
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        billViewModel = BillViewModel(this.requireContext())
        binding.rvBillItem.layoutManager =
            LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)

        val billItemResponseList = billResponse.billItems
        billItemResponseList?.let {
            binding.rvBillItem.adapter = RecyclerViewBillDetailAdapter(it)
        }

        binding.txtTotalPrice.text =
            "Tổng tiền: ${NumberConverterUtil.convertNumberToStringWithDot(billResponse.totalPrice!!)} Đ"
        binding.txtOrderTime.text = "Đặt hàng lúc: ${billResponse.createdDate}"

        billResponse.status?.let {
            if (it != BillStatus.COMPLETED) {
                val dateFormat = "dd/MM/yyyy"
                val pickUpDate = billResponse.pickUpTime?.substringBefore(" ")
                val pickUpLocalDate =
                    LocalDate.parse(pickUpDate, DateTimeFormatter.ofPattern(dateFormat))
                when (pickUpLocalDate) {
                    LocalDate.now() -> {
                        binding.txtPickUpTime.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.pick_up_red)
                        )
                    }

                    LocalDate.now().plusDays(1) -> {
                        binding.txtPickUpTime.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.pick_up_orange)
                        )
                    }

                    LocalDate.now().plusDays(2) -> {
                        binding.txtPickUpTime.setTextColor(
                            ContextCompat.getColor(binding.root.context, R.color.pick_up_yellow)
                        )
                    }
                }
            }
            binding.txtPickUpTime.text = "${billResponse.pickUpTime}"

            bindOnBillStatus(it)
        }
        binding.txtCustomerId.text = "ID khách hàng: ${billResponse.customerId}"
    }

    override fun initListener() {
        binding.btnPrepare.setOnClickListener {
            loadingDialog?.show()
            updateStatusOnClickListener(BillStatus.PREPARED)
        }
        binding.btnComplete.setOnClickListener {
            loadingDialog?.show()
            updateStatusOnClickListener(BillStatus.COMPLETED)
        }
        binding.btnCancel.setOnClickListener {
            loadingDialog?.show()
            updateStatusOnClickListener(BillStatus.CANCELLED)
        }
    }

    override fun observeLiveData() {

    }

    private fun updateStatusOnClickListener(billStatus: BillStatus) {
        val updateBillStatusRequest = UpdateBillStatusRequest(billResponse.id!!, billStatus)
        billViewModel.updateBillStatus(updateBillStatusRequest).observe(this) {
            loadingDialog?.dismiss()
            if (it != BillResponse()) {
                bindOnBillStatus(it.status!!)
                Toast.makeText(this.requireContext(), it.status.description, Toast.LENGTH_SHORT)
                    .show()
                homeActivity.notifyBillListChanged(it.id!!)
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "Có lỗi xảy ra khi cập nhật trạng thái đơn hàng",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun bindOnBillStatus(billStatus: BillStatus) {
        when (billStatus) {
            BillStatus.PAID -> {
                binding.btnComplete.isEnabled = false
                binding.btnComplete.alpha = 0.5f
            }

            BillStatus.PREPARED -> {
                binding.btnPrepare.isEnabled = false
                binding.btnPrepare.alpha = 0.5f

                binding.btnComplete.isEnabled = true
                binding.btnComplete.alpha = 1f
            }

            BillStatus.COMPLETED, BillStatus.CANCELLED -> {
                binding.layoutBtn.visibility = View.GONE
            }
        }
        binding.txtBillStatus.text = billStatus.description
    }
}