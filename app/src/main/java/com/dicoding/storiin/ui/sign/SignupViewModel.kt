package com.dicoding.storiin.ui.sign

import androidx.lifecycle.ViewModel
import com.dicoding.storiin.user.UserRepository

class SignupViewModel(private val repository: UserRepository): ViewModel() {
    fun register(name: String, email: String, password: String) = repository.signup(name, email, password)
}