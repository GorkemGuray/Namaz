package co.gorkem.namaz.wear.complication

import androidx.wear.watchface.complications.data.ComplicationType

data class ComplicationPreviewModel(
    val type: ComplicationType,
    val text: String,
    val title: String,
    val isStaticText: Boolean = true,
    val value: Float = 0f,
    val min: Float = 0f,
    val max: Float = 6f
)
