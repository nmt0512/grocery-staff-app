package com.example.grocerystorestaff.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.grocerystorestaff.enums.BillStatus
import com.example.grocerystorestaff.model.request.bill.UpdateBillStatusRequest
import com.example.grocerystorestaff.model.response.BaseResponse
import com.example.grocerystorestaff.model.response.bill.BillResponse
import com.example.grocerystorestaff.model.response.bill.GetAllBillResponse
import com.example.grocerystorestaff.model.response.bill.UpdateBillStatusResponse
import com.example.grocerystorestaff.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BillViewModel(context: Context) : ViewModel() {

    private val apiService = RetrofitClient.getInstance(context)

    fun getAllBill(
        billStatusList: List<BillStatus>,
        pageNumber: Int,
        pageSize: Int
    ): MutableLiveData<List<BillResponse>> {
        val billResponseListLiveData = MutableLiveData<List<BillResponse>>()
        apiService
            .getAllBill(
                billStatusList.map { it.toString() },
                pageNumber,
                pageSize
            )
            .enqueue(object : Callback<BaseResponse<GetAllBillResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<GetAllBillResponse>>,
                    response: Response<BaseResponse<GetAllBillResponse>>
                ) {
                    if (response.body() != null) {
                        billResponseListLiveData.value = response.body()!!.data?.billResponseList
                    } else {
                        billResponseListLiveData.value = mutableListOf()
                    }
                }

                override fun onFailure(call: Call<BaseResponse<GetAllBillResponse>>, t: Throwable) {
                    billResponseListLiveData.value = mutableListOf()
                }

            })
        return billResponseListLiveData
    }

    fun updateBillStatus(updateBillStatusRequest: UpdateBillStatusRequest): MutableLiveData<BillResponse> {
        val billResponseLiveData = MutableLiveData<BillResponse>()
        apiService
            .updateBillStatus(updateBillStatusRequest)
            .enqueue(object : Callback<BaseResponse<UpdateBillStatusResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<UpdateBillStatusResponse>>,
                    response: Response<BaseResponse<UpdateBillStatusResponse>>
                ) {
                    if (response.body() != null) {
                        billResponseLiveData.value = response.body()!!.data?.billResponse
                    } else {
                        billResponseLiveData.value = BillResponse()
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse<UpdateBillStatusResponse>>,
                    t: Throwable
                ) {
                    billResponseLiveData.value = BillResponse()
                }

            })
        return billResponseLiveData
    }

}