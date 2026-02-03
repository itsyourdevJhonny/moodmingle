package com.emc.moodmingle.utils.font

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.emc.moodmingle.R

object FontUtils {
    val Roboto = FontFamily(Font(R.font.roboto_regular))
    val Lato = FontFamily(Font(R.font.lato_regular))
    val Montserrat = FontFamily(Font(R.font.montserrat_regular))
    val OpenSans = FontFamily(Font(R.font.opensans_regular))
    val Poppins = FontFamily(Font(R.font.poppins_regular))
    val Nunito = FontFamily(Font(R.font.nunito_regular))
    val Inter = FontFamily(Font(R.font.inter_regular))
    val Tinos = FontFamily(Font(R.font.tinos_regular))
    val Playfair = FontFamily(Font(R.font.playfair_regular))
    val Merriweather = FontFamily(Font(R.font.merriweather_regular))
    val Bebasneue = FontFamily(Font(R.font.bebasneue_regular))
    val Oswald = FontFamily(Font(R.font.oswald_regular))

    fun getDefaultFonts(): List<FontOption> {
        return listOf(
            FontOption("Default", FontFamily.Default),
            FontOption("Sans Serif", FontFamily.SansSerif),
            FontOption("Serif", FontFamily.Serif),
            FontOption("Monospace", FontFamily.Monospace),
            FontOption("Cursive", FontFamily.Cursive),

            FontOption("Roboto", Roboto),
            FontOption("Lato", Lato),
            FontOption("Montserrat", Montserrat),
            FontOption("Open Sans", OpenSans),
            FontOption("Poppins", Poppins),
            FontOption("Nunito", Nunito),
            FontOption("Inter", Inter),
            FontOption("Tinos", Tinos),
            FontOption("Playfair", Playfair),
            FontOption("Merriweather", Merriweather),
            FontOption("Bebasneue", Bebasneue),
            FontOption("Oswald", Oswald)
        )
    }

    fun getFontName(fontFamily: FontFamily): String {
        return getDefaultFonts()
            .filter { (_, font) -> font == fontFamily }
            .map { it.name }[0]
    }

    fun getFontStyle(fontName: String): FontFamily {
        return getDefaultFonts()
            .filter { fontOption -> fontOption.name == fontName }
            .map { it.fontFamily }[0]
    }
}