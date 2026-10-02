package com.xgamerstore.forgefit

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

object ForgeLang {
    val codes = arrayOf(
        "cs","en","de","sk","pl","fr","es","it","pt","nl","sv","no","da","fi","hu","ro","bg","hr","sl",
        "uk","ru","tr","el","ar","he","hi","bn","id","ms","vi","th","ja","ko","zh",
        "af","sq","be","ca","eo","et","gl","ka","gu","ht","is","ga","kn","lv","lt","mk","mt","mr","fa","sw","tl","ta","te","ur","cy"
    )

    val names = arrayOf(
        "Čeština","English","Deutsch","Slovenčina","Polski","Français","Español","Italiano","Português","Nederlands","Svenska","Norsk","Dansk","Suomi","Magyar","Română","Български","Hrvatski","Slovenščina",
        "Українська","Русский","Türkçe","Ελληνικά","العربية","עברית","हिन्दी","বাংলা","Bahasa Indonesia","Bahasa Melayu","Tiếng Việt","ไทย","日本語","한국어","中文",
        "Afrikaans","Shqip","Беларуская","Català","Esperanto","Eesti","Galego","ქართული","ગુજરાતી","Kreyòl ayisyen","Íslenska","Gaeilge","ಕನ್ನಡ","Latviešu","Lietuvių","Македонски","Malti","मराठी","فارسی","Kiswahili","Filipino","தமிழ்","తెలుగు","اردو","Cymraeg"
    )

    private var app: Context? = null
    var current by mutableStateOf("cs")
        private set

    private val translations = ConcurrentHashMap<String, String>()
    private val translators = ConcurrentHashMap<String, Translator>()

    fun init(context: Context) {
        app = context.applicationContext
        current = normalize(context.getSharedPreferences("forgefit", Context.MODE_PRIVATE).getString("language", "cs"))
    }

    fun normalize(code: String?): String = if (code != null && codes.contains(code)) code else "cs"

    fun displayName(code: String = current): String {
        val i = codes.indexOf(normalize(code)).coerceAtLeast(0)
        return names[i]
    }

    fun setLanguage(code: String) {
        val normalized = normalize(code)
        current = normalized
        translations.clear()
        app?.getSharedPreferences("forgefit", Context.MODE_PRIVATE)?.edit()?.putString("language", normalized)?.apply()
    }

    private fun translatorFor(code: String): Translator? {
        if (code == "cs") return null
        translators[code]?.let { return it }
        val target = TranslateLanguage.fromLanguageTag(code) ?: return null
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.CZECH)
            .setTargetLanguage(target)
            .build()
        return Translation.getClient(options).also { translators[code] = it }
    }

    suspend fun translate(text: String, language: String = current): String {
        val code = normalize(language)
        if (code == "cs" || text.isBlank()) return text
        val key = "$code\u0000$text"
        translations[key]?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val translator = translatorFor(code) ?: return@withContext text
                Tasks.await(translator.downloadModelIfNeeded())
                val result = Tasks.await(translator.translate(text)).ifBlank { text }
                translations[key] = result
                result
            } catch (_: Throwable) {
                text
            }
        }
    }
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val language = ForgeLang.current
    val shown by produceState(initialValue = text, text, language) {
        value = ForgeLang.translate(text, language)
    }
    androidx.compose.material3.Text(
        text = shown,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

@Composable
fun LanguageSettingRow() {
    var open by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { open = true }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Jazyk", fontWeight = FontWeight.Bold, color = Color.White)
            androidx.compose.material3.Text(ForgeLang.displayName(), color = Color(0xFF9AA4AF), fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF9AA4AF))
    }

    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            title = { Text("Vyber jazyk") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 460.dp)) {
                    items(ForgeLang.codes.indices.toList()) { i ->
                        val code = ForgeLang.codes[i]
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    ForgeLang.setLanguage(code)
                                    open = false
                                }
                                .padding(vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Text(ForgeLang.names[i], Modifier.weight(1f))
                            if (ForgeLang.current == code) {
                                Icon(Icons.Default.Check, null, tint = Color(0xFFFF5A36))
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { open = false }) { Text("Zavřít") } }
        )
    }
}
