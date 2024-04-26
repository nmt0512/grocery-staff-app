package com.example.grocerystorestaff.model.response.bill

import com.example.grocerystorestaff.enums.BillStatus


data class BillResponse(
    val id: Int? = 0,
    val totalPrice: Int? = 0,
    val customerId: String? = "",
    val createdDate: String? = "",
    val status: BillStatus? = null,
    val pickUpTime: String? = "",
    val billItems: List<BillItemResponse>? = mutableListOf()
)