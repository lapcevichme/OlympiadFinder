package com.lapcevichme.olympiadfinder.domain.model

enum class AppFont(val key: String) {
    DEFAULT("default"),
    SERIF("serif"),
    MONOSPACE("monospace");

    // Вспомогательная функция для получения enum по ключу (строке из хранилища)
    companion object {
        fun fromKey(key: String?): AppFont =
            entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}
