package com.example.grocerystorestaff.view

import com.example.grocerystorestaff.model.response.bill.BillResponse

interface IHomeActivity {

    fun bindBillDetailFragment(billResponse: BillResponse)

    fun notifyBillListChanged(billResponseId: Int)
}