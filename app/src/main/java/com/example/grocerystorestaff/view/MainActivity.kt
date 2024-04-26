package com.example.grocerystorestaff.view

import android.content.Intent
import android.widget.Toast
import com.example.grocerystorestaff.R
import com.example.grocerystorestaff.base.BaseActivity
import com.example.grocerystorestaff.databinding.ActivityMainBinding
import com.example.grocerystorestaff.viewmodel.LoginViewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var loginViewModel: LoginViewModel

    override fun getContentLayout(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        loginViewModel = LoginViewModel(this)
    }

    override fun initListener() {
        binding.btnLogin.setOnClickListener {
            loadingDialog?.show()
            loginViewModel.login(
                binding.txtUsername.text.toString(),
                binding.txtPassword.text.toString()
            )
        }
    }

    override fun observeData() {
        loginViewModel.isSuccessfullyLoggedIn.observe(this) {
            loadingDialog?.dismiss()
            if (it) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            } else {
                Toast.makeText(
                    this,
                    "Tên đăng nhập hoặc mật khẩu không chính xác",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}