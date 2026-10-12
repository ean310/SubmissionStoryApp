package com.dicoding.storiin.ui.sign

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storiin.databinding.ActivitySigninBinding
import com.dicoding.storiin.user.UserModel
import com.dicoding.storiin.user.Hasil
import com.dicoding.storiin.ui.ViewModelFactory
import com.dicoding.storiin.ui.main.MainActivity

class SigninActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel by viewModels<SigninViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
        playAnimation()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupActions() {
        binding.tvofSignin.setOnClickListener(this)
        binding.loginButton.setOnClickListener { handleLogin() }
    }

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(email, password).observe(this) { result ->
            when (result) {
                is Hasil.Loading -> showLoading(true)
                is Hasil.Success -> {
                    showLoading(false)
                    saveUserSession(result.data.loginResult.token!!, result.data.loginResult.name!!)
                    navigateToMain()
                }
                is Hasil.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal Login", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun saveUserSession(token: String, name: String) {
        viewModel.saveSession(UserModel(binding.emailEditText.text.toString(), token, name))
    }

    private fun navigateToMain() {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    private fun playAnimation() {
        val slideAnimation = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val fadeInAnimations = listOf(
            binding.tvSignin,
            binding.titleEmail,
            binding.edLoginEmail,
            binding.titlePassword,
            binding.edLoginPassword,
            binding.loginButton,
            binding.contSignup
        ).map { view -> ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(200) }

        AnimatorSet().apply {
            playSequentially(fadeInAnimations)
            startDelay = 100
            start()
        }

        slideAnimation.start()
    }

    override fun onClick(v: View?) {
        startActivity(Intent(this, SignupActivity::class.java))
    }
}
