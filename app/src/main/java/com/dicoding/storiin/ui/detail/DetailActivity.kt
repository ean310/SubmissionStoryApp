package com.dicoding.storiin.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.storiin.databinding.ActivityDetailBinding
import com.dicoding.storiin.user.Hasil
import com.dicoding.storiin.ui.ViewModelFactory
import com.dicoding.storiin.ui.main.MainViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiveId = intent.getStringExtra("id_user").toString()

        // Mengambil sesi pengguna dan mendapatkan detail
        mainViewModel.getSession().observe(this) { user ->
            user?.token?.let { token ->
                viewModel.getDetail(token, receiveId)
            }
        }

        // Observasi hasil detail
        viewModel.detailResult.observe(this) { result ->
            when (result) {
                is Hasil.Loading -> {
                    // Tampilkan ProgressBar jika loading
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }
                is Hasil.Success -> {
                    // Tampilkan data pengguna
                    binding.apply {
                        userName.text = result.data.name
                        description.text = result.data.description
                        Glide.with(this@DetailActivity)
                            .load(result.data.photoUrl)
                            .into(avaUser)
                    }
                    // Sembunyikan ProgressBar setelah data dimuat
                    binding.progressBar.visibility = android.view.View.GONE
                }
                is Hasil.Error -> {
                    // Sembunyikan ProgressBar jika terjadi error
                    binding.progressBar.visibility = android.view.View.GONE
                    // Anda dapat menambahkan penanganan error di sini
                }
            }
        }
    }

    companion object {
        const val NAME = "name"
        const val DESC = "description"
        const val URL = "url"
    }
}
