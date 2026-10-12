package com.dicoding.storiin.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storiin.response.StoryResponse
import com.dicoding.storiin.user.Hasil
import com.dicoding.storiin.user.UserRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) :ViewModel() {

    private val _location = MutableLiveData<Hasil<StoryResponse>>()
    val location: LiveData<Hasil<StoryResponse>> = _location

    fun getLoc(token: String) {
        viewModelScope.launch {
            repository.getStoriesWithLocation(token).asFlow().collect{
                _location.value = it
            }
        }
    }

    fun getSession() = repository.getSession().asLiveData()
}