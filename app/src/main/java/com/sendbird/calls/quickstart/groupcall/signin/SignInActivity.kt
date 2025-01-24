package com.sendbird.calls.quickstart.groupcall.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.quickstart.groupcall.BuildConfig
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.ActivitySignInBinding
import com.sendbird.calls.quickstart.groupcall.main.MainActivity
import com.sendbird.calls.quickstart.groupcall.util.*

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: AuthenticateViewModel = AuthenticateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        binding.editTextApplicationId.setText(SENDBIRD_APP_ID)
        binding.editTextUserId.setText(SharedPreferencesManager.userId)
        binding.editTextAccessToken.setText(SharedPreferencesManager.accessToken)
        binding.textViewVersion.text = getVersion()
        binding.buttonSignIn.setOnClickListener(this::onSignInButtonClicked)
        binding.root.setOnClickListener { hideKeyboard() }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.authenticationLiveData.observe(this) { resource ->
            Log.d("SignInActivity", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> {
                    goToMainActivity()
                }
                Status.ERROR -> Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSignInButtonClicked(v: View) {
        val appId = binding.editTextApplicationId.text.toString()
        if (appId.isEmpty()) {
            return
        }

        SendBirdCall.init(applicationContext, appId)
        val userId = binding.editTextUserId.text.toString()
        val accessToken = binding.editTextAccessToken.text.toString()

        viewModel.authenticate(userId, accessToken)
    }

    private fun getVersion() = "Quickstart ${BuildConfig.VERSION_NAME}   SDK ${SendBirdCall.VERSION}"
}
