package com.example.datn_mobile.data.network.interceptor

import com.example.datn_mobile.data.local.PreferenceDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val prefs: PreferenceDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()

        val invocation = request.tag(Invocation::class.java)
        val method = invocation?.method()
        val hasNoAuth = method?.isAnnotationPresent(NoAuth::class.java) ?:false
        if (hasNoAuth) {
            return chain.proceed(request)
        }

        val token = runBlocking {  prefs.tokenFlow.first() }
        if (!token.isNullOrEmpty()) {
            val authRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(authRequest)
        }
        return chain.proceed(request)
    }
}