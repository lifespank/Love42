package com.mylittleproject.love42.model

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSource(context: Context) : DataSource.LocalDataSource {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        ACCESS_TOKEN,
        mainKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun fetchAccessToken(): Result<String?> = runCatching {
        withContext(Dispatchers.IO) {
            return@withContext sharedPreferences.getString(KEY, null)
        }
    }

    override suspend fun saveAccessToken(accessToken: String) = runCatching {
        withContext(Dispatchers.IO) {
            with(sharedPreferences.edit()) {
                putString(KEY, accessToken)
                apply()
            }
        }
    }

    companion object {
        private const val ACCESS_TOKEN = "Access Token"
        private const val KEY = "Access Token Key"
    }
}