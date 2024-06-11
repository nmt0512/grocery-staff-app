package com.example.grocerystorestaff.view

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerystorestaff.R
import com.example.grocerystorestaff.adapter.RecyclerViewBillListAdapter
import com.example.grocerystorestaff.base.BaseActivity
import com.example.grocerystorestaff.databinding.ActivityHomeBinding
import com.example.grocerystorestaff.enums.BillStatus
import com.example.grocerystorestaff.model.response.bill.BillResponse
import com.example.grocerystorestaff.network.RetrofitClient
import com.example.grocerystorestaff.utils.ApplicationPreference
import com.example.grocerystorestaff.viewmodel.BillViewModel
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

class HomeActivity : BaseActivity<ActivityHomeBinding>(), IHomeActivity {

    private lateinit var logoutConfirmDialog: AlertDialog

    private var billDetailFragment: BillDetailFragment? = null

    private var billStatus = BillStatus.PAID

    private lateinit var billViewModel: BillViewModel

    private val pageSize = 8
    private var pageNumber = 1

    private val billResponseList = mutableListOf<BillResponse>()

    private var isAbleToFetchMore = true

    private lateinit var socketIOClient: Socket

    private var newPendingBill = 0

    override fun getContentLayout(): Int {
        return R.layout.activity_home
    }

    override fun initView() {
        loadingDialog?.dismiss()

        val logoutConfirmDialogListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    ApplicationPreference.getInstance(this)?.logout()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }
        logoutConfirmDialog = AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất ?")
            .setPositiveButton("OK", logoutConfirmDialogListener)
            .setNegativeButton("Cancel", logoutConfirmDialogListener)
            .create()

        billViewModel = BillViewModel(this)
        binding.rvBillList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        binding.rvBillList.adapter =
//            RecyclerViewBillListAdapter(this, billResponseList)

        val eightBlankSpace = "     "
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(eightBlankSpace + "Đang chờ" + eightBlankSpace),
            true
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(eightBlankSpace + "Đã chuẩn bị" + eightBlankSpace)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(eightBlankSpace + "Đã hoàn thành" + eightBlankSpace)
        )

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(eightBlankSpace + "Đã hủy" + eightBlankSpace)
        )

        socketIOClient = IO.socket(RetrofitClient.BASE_URI)
        val onTodayNewBill = Emitter.Listener { args ->
            this.runOnUiThread {
                newPendingBill++
                binding.tabLayout.getTabAt(0)?.orCreateBadge?.text = newPendingBill.toString()
            }
        }
        socketIOClient.on("todayNewBill", onTodayNewBill)
        socketIOClient.connect()
    }

    override fun initListener() {
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                loadingDialog?.show()
                billResponseList.clear()
                pageNumber = 1
                isAbleToFetchMore = true

                removeBillDetailFragment()

                val position = tab?.position
                if (position == 0) {
                    billStatus = BillStatus.PAID
                    if (newPendingBill > 0) {
                        newPendingBill = 0
                        tab.removeBadge()
                    }

                } else if (position == 1) {
                    billStatus = BillStatus.PREPARED
                } else if (position == 2) {
                    billStatus = BillStatus.COMPLETED
                } else if (position == 3) {
                    billStatus = BillStatus.CANCELLED
                }
                observeData()
            }

            override fun onTabUnselected(tab: Tab?) {

            }

            override fun onTabReselected(tab: Tab?) {
                if (tab?.position == 0 && newPendingBill > 0) {
                    loadingDialog?.show()
                    billResponseList.clear()
                    pageNumber = 1
                    isAbleToFetchMore = true
                    newPendingBill = 0
                    tab.removeBadge()
                    observeData()
                }
            }

        })

        binding.nsvRvBill.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (isAbleToFetchMore && scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                isAbleToFetchMore = false
                pageNumber++
                binding.pbLoading.visibility = View.VISIBLE
                observeData()
            }
        })

        binding.btnLogout.setOnClickListener {
            logoutConfirmDialog.show()
        }
    }

    override fun observeData() {
        billViewModel.getAllBill(
            mutableListOf(billStatus),
            pageNumber,
            pageSize
        ).observe(this) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    billResponseList.addAll(it)
                    if (it.size < pageSize) {
                        disableFetchMore()
                    } else {
                        isAbleToFetchMore = true
                    }
                } else {
                    disableFetchMore()
                }
            }

//            binding.rvBillList.adapter?.notifyDataSetChanged()
            binding.rvBillList.adapter =
                RecyclerViewBillListAdapter(this, billResponseList)

            loadingDialog?.dismiss()
        }
    }

    override fun bindBillDetailFragment(billResponse: BillResponse) {
        billDetailFragment = BillDetailFragment(this, billResponse)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, billDetailFragment!!)
            commit()
        }
    }

    override fun notifyBillListChanged(billResponseId: Int) {

        billResponseList.removeIf { it.id == billResponseId }
//        binding.rvBillList.adapter?.notifyDataSetChanged()
        binding.rvBillList.adapter =
            RecyclerViewBillListAdapter(this, billResponseList)
    }

    private fun removeBillDetailFragment() {
        billDetailFragment?.let {
            supportFragmentManager.beginTransaction().apply {
                remove(billDetailFragment!!)
                commit()
            }
        }
        billDetailFragment = null
    }

    private fun disableFetchMore() {
        isAbleToFetchMore = false
        binding.pbLoading.visibility = View.GONE
    }

}