package com.dicoding.storiin.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storiin.R
import com.dicoding.storiin.databinding.ActivityMainBinding
import com.dicoding.storiin.response.Story
import com.dicoding.storiin.ui.ViewModelFactory
import com.dicoding.storiin.ui.detail.DetailActivity
import com.dicoding.storiin.ui.maps.MapsActivity
import com.dicoding.storiin.ui.upload.UploadActivity
import com.dicoding.storiin.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeUserSession()

        setupView()

        adapter = StoryAdapter()
        adapter.setOnClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                navigateToDetailActivity(data)
            }
        })

        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter
        }
    }

    private fun observeUserSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateToWelcomeActivity()
            } else {
                initializeStories(user.token)
            }
        }
    }

    private fun navigateToWelcomeActivity() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    private fun navigateToDetailActivity(data: Story) {
        Intent(this@MainActivity, DetailActivity::class.java).also {
            it.putExtra(DetailActivity.NAME, data.name)
            it.putExtra(DetailActivity.DESC, data.description)
            it.putExtra(DetailActivity.URL, data.photoUrl)
            startActivity(it)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_logout -> {
                viewModel.logout()
                true
            }
            R.id.btn_add -> {
                startActivity(Intent(this@MainActivity, UploadActivity::class.java))
                true
            }
            R.id.btn_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun initializeStories(token: String) {
        viewModel.story(token).observe(this) { user ->
            if (user != null) {
                binding.progressBar.visibility = View.GONE
                val adapter = StoryAdapter()
                binding.rvStory.layoutManager = LinearLayoutManager(this)
                adapter.submitData(lifecycle, user)
                binding.rvStory.adapter = adapter
            }
        }
    }
}
