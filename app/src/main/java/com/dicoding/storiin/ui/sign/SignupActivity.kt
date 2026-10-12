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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storiin.databinding.ActivitySignupBinding
import com.dicoding.storiin.user.Hasil
import com.dicoding.storiin.ui.ViewModelFactory

class SignupActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
        playAnimation()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupActions() {
        binding.tvLoginSignup.setOnClickListener(this)
        binding.btnSignup.setOnClickListener { handleSignup() }
    }

    private fun handleSignup() {
        val name = binding.nameSignup.text.toString().trim()
        val email = binding.emailSignup.text.toString().trim()
        val password = binding.passSignup.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.register(name, email, password).observe(this) { result ->
            when (result) {
                is Hasil.Loading -> showLoading(true)
                is Hasil.Success -> {
                    showLoading(false)
                    showSuccessDialog(email)
                }
                is Hasil.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Pendaftaran Gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarSignUp.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Selamat!")
            setMessage("Akun dengan email $email berhasil dibuat. Silakan login untuk melanjutkan.")
            setPositiveButton("Lanjut") { _, _ ->
                navigateToSignin()
            }
            create()
            show()
        }
    }

    private fun navigateToSignin() {
        Intent(this, SigninActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
            finish()
        }
    }

    private fun playAnimation() {
        val slideAnimation = ObjectAnimator.ofFloat(binding.logoAppText, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val fadeInAnimations = listOf(
            binding.tvSignup,
            binding.titleName,
            binding.nameSignup,
            binding.titleEmail,
            binding.emailSignup,
            binding.titlePassword,
            binding.passSignup,
            binding.btnSignup,
            binding.tvLoginSignup
        ).map { view -> ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(100) }

        AnimatorSet().apply {
            playSequentially(fadeInAnimations)
            startDelay = 100
            start()
        }

        slideAnimation.start()
    }

    override fun onClick(v: View?) {
        navigateToSignin()
    }
}
