package com.example.grocerystorestaff.network

import com.example.grocerystorestaff.model.request.auth.LoginRequest
import com.example.grocerystorestaff.model.request.bill.UpdateBillStatusRequest
import com.example.grocerystorestaff.model.response.BaseResponse
import com.example.grocerystorestaff.model.response.auth.LoginResponse
import com.example.grocerystorestaff.model.response.bill.GetAllBillResponse
import com.example.grocerystorestaff.model.response.bill.UpdateBillStatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    @POST("auth/staffLogin")
    fun login(@Body loginRequest: LoginRequest): Call<BaseResponse<LoginResponse>>

    @GET("bill/all")
    fun getAllBill(
        @Query("status") billStatusList: List<String>,
        @Query("page") pageNumber: Int,
        @Query("size") pageSize: Int
    ): Call<BaseResponse<GetAllBillResponse>>

    @PUT("staff/bill/status")
    fun updateBillStatus(@Body updateBillStatusRequest: UpdateBillStatusRequest): Call<BaseResponse<UpdateBillStatusResponse>>
}