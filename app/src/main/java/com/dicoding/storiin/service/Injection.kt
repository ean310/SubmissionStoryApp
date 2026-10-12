package com.dicoding.storiin.service

import android.content.Context
import com.dicoding.storiin.user.UserPreference
import com.dicoding.storiin.user.UserRepository
import com.dicoding.storiin.user.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService, pref)
    }
}