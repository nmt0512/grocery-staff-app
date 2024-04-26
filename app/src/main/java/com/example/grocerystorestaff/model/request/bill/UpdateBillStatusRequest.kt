package com.example.grocerystorestaff.model.request.bill

import com.example.grocerystorestaff.enums.BillStatus

data class UpdateBillStatusRequest(
    private val billId: Int,
    private val billStatus: BillStatus
)
