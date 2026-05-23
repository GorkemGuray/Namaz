package co.gorkem.namaz.service

import android.content.Context

object NamazWidgetPrefs {
    private const val PREFS_NAME = "namaz_widget_prefs"
    private const val KEY_WIDGET_THEME_PREFIX = "widget_theme_"
    private const val KEY_WIDGET_LAYOUT_PREFIX = "widget_layout_"

    // Theme options: 0 = App Theme, 1 = Glassmorphism, 2 = Dynamic Colors
    fun getWidgetTheme(context: Context, appWidgetId: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt("$KEY_WIDGET_THEME_PREFIX$appWidgetId", 0)
    }

    fun setWidgetTheme(context: Context, appWidgetId: Int, theme: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt("$KEY_WIDGET_THEME_PREFIX$appWidgetId", theme).apply()
    }

    // Layout options: 0 = Detailed (Countdown + List), 1 = Next Prayer Only
    fun getWidgetLayout(context: Context, appWidgetId: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt("$KEY_WIDGET_LAYOUT_PREFIX$appWidgetId", 0)
    }

    fun setWidgetLayout(context: Context, appWidgetId: Int, layout: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt("$KEY_WIDGET_LAYOUT_PREFIX$appWidgetId", layout).apply()
    }

    fun clearWidgetPrefs(context: Context, appWidgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove("$KEY_WIDGET_THEME_PREFIX$appWidgetId")
            .remove("$KEY_WIDGET_LAYOUT_PREFIX$appWidgetId")
            .apply()
    }
}
