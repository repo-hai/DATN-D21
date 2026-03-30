package com.example.datn_mobile.domain.usecase

import androidx.datastore.preferences.core.Preferences
import com.example.datn_mobile.data.local.PreferenceDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckFirstLaunchUseCase @Inject constructor(
    private val prefs: PreferenceDataSource
) {
    suspend operator fun invoke(): Boolean =
        prefs.isFirstLaunchFlow.first()
}