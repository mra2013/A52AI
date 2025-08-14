package com.example.a52ai
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
object SecurePrefs {
  private const val FILE="secure_prefs"; private const val KEY_BASE="baseUrl"; private const val KEY_API="apiKey"; private const val KEY_MODEL="model"
  fun load(ctx: Context): Settings {
    val mk=MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val p=EncryptedSharedPreferences.create(FILE,mk,ctx,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    val base=p.getString(KEY_BASE,"https://api.openai.com/")?:""; val api=p.getString(KEY_API,"")?:""; val model=p.getString(KEY_MODEL,"gpt-4o-mini")?:""
    return Settings(base,api,model)
  }
  fun save(ctx: Context,s: Settings){ val mk=MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val p=EncryptedSharedPreferences.create(FILE,mk,ctx,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    p.edit().putString(KEY_BASE,s.baseUrl).putString(KEY_API,s.apiKey).putString(KEY_MODEL,s.model).apply()
  }
}