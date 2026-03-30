package com.example.datn_mobile.domain.usecase

import com.example.datn_mobile.data.local.PreferenceDataSource
import javax.inject.Inject

class SetFirstLaunchCompleteUseCase @Inject constructor(
    private val prefs: PreferenceDataSource
){
    suspend operator fun invoke() {
        prefs.setFirstLaunch(false)
    }
}