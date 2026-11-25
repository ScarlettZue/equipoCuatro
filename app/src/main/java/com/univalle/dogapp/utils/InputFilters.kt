package com.univalle.dogapp.utils

import android.text.InputFilter

object InputFilters {
    /**
     * Deja escribir solo letras (con tildes y ñ) y espacios. El límite de
     * caracteres se pone aparte con android:maxLength en el layout.
     */
    val lettersOnly = InputFilter { source, start, end, _, _, _ ->
        val clean = StringBuilder()
        for (i in start until end) {
            val c = source[i]
            if (c.isLetter() || c == ' ') clean.append(c)
        }
        // null = aceptar el texto tal cual; si se quitó algo, se devuelve el texto limpio.
        if (clean.length == end - start) null else clean.toString()
    }
}
