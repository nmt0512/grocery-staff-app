package com.example.grocerystorestaff.enums

enum class BillStatus(val description: String) {
    PAID("Đã thanh toán"),
    COMPLETED("Đã hoàn thành"),
    PREPARED("Đã chuẩn bị hàng"),
    CANCELLED("Đã hủy")
}