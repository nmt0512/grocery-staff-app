package com.example.grocerystorestaff.model.response.bill

import com.example.grocerystorestaff.model.response.product.ProductResponse

data class BillItemResponse(
    val productId: Int,
    val productResponse: ProductResponse,
    val quantity: Int,
    val price: Int
)