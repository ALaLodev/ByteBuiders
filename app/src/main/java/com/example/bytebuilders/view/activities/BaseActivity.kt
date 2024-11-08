package com.example.bytebuilders.view.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.bytebuilders.view.activities.utils.LocalHelper

open class BaseActivity: AppCompatActivity() {
    private var currentLanguageCode: String? = null

    override fun attachBaseContext(newBase: Context?) {
        val context = newBase?.let { LocalHelper.applyPreferredLanguage(it) }
        super.attachBaseContext(context)
    }

    override fun onResume() {
        super.onResume()

        // Detecta cambios de idioma y recrea la actividad si es necesario
        val preferredLanguageCode = LocalHelper.getPreferredLanguage(this)
        if (currentLanguageCode == null) {
            currentLanguageCode = preferredLanguageCode
        } else if (currentLanguageCode != preferredLanguageCode) {
            currentLanguageCode = preferredLanguageCode
            recreate() // Recrea la actividad para aplicar el nuevo idioma
        }
    }
}