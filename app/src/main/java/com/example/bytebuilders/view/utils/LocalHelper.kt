package com.example.bytebuilders.view.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.Locale

object LocalHelper {
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        savePreferredLanguage(context, languageCode)

        return context.createConfigurationContext(config)
    }

    private fun savePreferredLanguage(context: Context, languageCode: String) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString("preferred_language", languageCode).apply()
    }

    fun applyPreferredLanguage(context: Context): Context {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val languageCode = prefs.getString("preferred_language", Locale.getDefault().language) ?: Locale.getDefault().language
        return setLocale(context, languageCode)
    }

    fun getPreferredLanguage(context: Context): String {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString("preferred_language", Locale.getDefault().language) ?: Locale.getDefault().language
    }
}
