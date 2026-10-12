package com.dicoding.storiin.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storiin.user.Hasil
import com.dicoding.storiin.user.UserRepository
import com.dicoding.storiin.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _detailResult = MutableLiveData<Hasil<Story>>()
    val detailResult: LiveData<Hasil<Story>> = _detailResult

    fun getSession(){
        repository.getSession().asLiveData()
    }
    fun getDetail(token: String, id: String) {
        viewModelScope.launch {
            repository.detail(token, id).collect{ result ->
                _detailResult.value = result
            }
        }
    }
}