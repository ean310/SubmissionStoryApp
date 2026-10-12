package com.dicoding.storiin.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.storiin.user.UserModel
import com.dicoding.storiin.user.UserRepository
import com.dicoding.storiin.response.Story
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    val listUser = MutableLiveData<ArrayList<Story>>()


    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun story(token: String) :LiveData<PagingData<Story>> = repository.getPaging(token)

}