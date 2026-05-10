package co.gorkem.namaz

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {

    private const val PREFS_NAME = "locale_prefs"
    private const val KEY_LANGUAGE = "language"
    private const val DEFAULT_LANGUAGE = "tr"

    fun setLanguage(context: Context, language: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun applyLocale(context: Context): Context {
        val language = getLanguage(context)
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
