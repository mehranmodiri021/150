package com.example.ui.theme

enum class ArenaTheme(
    val code: String,
    val titleEn: String,
    val titleFa: String,
    val previewHex: Long
) {
    DEFAULT("default", "Cyber Arena", "میدان سایبر", 0xFF00E5FF),
    MIDNIGHT("midnight", "Midnight Navy", "شب تیره", 0xFF3D5AFE),
    OCEAN("ocean", "Oceanic Depths", "اعماق اقیانوس", 0xFF00B4D8),
    PURPLE("purple", "Neon Purple", "بنفش نئونی", 0xFFBD00FF),
    SUNSET("sunset", "Solar Sunset", "غروب خورشید", 0xFFFF6D00),
    EMERALD("emerald", "Emerald Jade", "زمرد یشم", 0xFF00E676);

    companion object {
        fun fromCode(code: String): ArenaTheme {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DEFAULT
        }
    }
}
