package ir.mehdiyari.krypt.ui.voice.record

data class RecordActionButtonsState(
    val stop: Pair<Boolean, (() -> Unit)?> = true to null,
    val resume: Pair<Boolean, (() -> Unit)?> = false to null,
    val save: Pair<Boolean, (() -> Unit)?> = true to null
)