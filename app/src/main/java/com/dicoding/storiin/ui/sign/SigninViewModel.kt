package com.dicoding.storiin.ui.sign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storiin.user.UserModel
import com.dicoding.storiin.user.UserRepository

import kotlinx.coroutines.launch

class SigninViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun login(email: String, password: String) = repository.login(email, password)
}