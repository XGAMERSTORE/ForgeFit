package com.xgamerstore.forgefit

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap

private val VisualBg = Color(0xFF20262E)
private val VisualBody = Color(0xFFF4F1F7)
private val VisualAccent = Color(0xFFFFB347)
private val VisualFloor = Color(0xFF59636F)

fun cleanRemoteText(value: String?, fallback: String = ""): String {
    val v = value?.trim().orEmpty()
    return if (v.isBlank() || v.equals("null", true) || v.equals("none", true) || v.equals("n/a", true)) fallback else v
}

fun czLevel(value: String): String = when (cleanRemoteText(value).lowercase()) {
    "beginner" -> "Začátečník"
    "intermediate" -> "Středně pokročilý"
    "expert", "advanced" -> "Pokročilý"
    else -> cleanRemoteText(value)
}

fun czEquipment(value: String): String {
    val v = cleanRemoteText(value, "Bez vybavení").lowercase()
    return when (v) {
        "body only", "bodyweight", "none" -> "Vlastní váha"
        "dumbbell" -> "Jednoručky"
        "barbell" -> "Velká činka"
        "kettlebells", "kettlebell" -> "Kettlebell"
        "cable" -> "Kladka"
        "machine" -> "Stroj"
        "bands", "band" -> "Odporová guma"
        "exercise ball" -> "Gymnastický míč"
        "foam roll" -> "Pěnový válec"
        "e-z curl bar" -> "EZ osa"
        "other" -> "Jiné"
        else -> cleanRemoteText(value, "Bez vybavení")
    }
}

fun czMuscles(value: String): String {
    val map = mapOf(
        "abdominals" to "břicho",
        "abductors" to "odtahovače stehen",
        "adductors" to "přitahovače stehen",
        "biceps" to "biceps",
        "calves" to "lýtka",
        "chest" to "hrudník",
        "forearms" to "předloktí",
        "glutes" to "hýždě",
        "hamstrings" to "zadní stehna",
        "lats" to "široký sval zádový",
        "lower back" to "spodní záda",
        "middle back" to "střed zad",
        "neck" to "krk",
        "quadriceps" to "kvadricepsy",
        "shoulders" to "ramena",
        "traps" to "trapézy",
        "triceps" to "triceps"
    )
    return value.split(",").mapNotNull { raw ->
        val key = cleanRemoteText(raw).lowercase()
        if (key.isBlank()) null else map[key] ?: cleanRemoteText(raw)
    }.joinToString(", ")
}

fun czCategory(value: String): String = when (cleanRemoteText(value).lowercase()) {
    "strength" -> "Síla"
    "stretching" -> "Protažení"
    "plyometrics" -> "Plyometrie"
    "strongman" -> "Strongman"
    "powerlifting" -> "Powerlifting"
    "olympic weightlifting" -> "Vzpírání"
    "cardio" -> "Kondice"
    else -> cleanRemoteText(value)
}

fun czExerciseName(name: String): String {
    val exact = mapOf(
        "90/90 Hamstring" to "90/90 protažení zadních stehen",
        "Adductor/Groin" to "Přitahovače stehen / třísla",
        "Advanced Kettlebell Windmill" to "Pokročilý kettlebell windmill",
        "Air Bike" to "Šlapání ve vzduchu",
        "All Fours Quad Stretch" to "Protažení kvadricepsu na čtyřech",
        "Alternate Hammer Curl" to "Střídavý kladivový zdvih",
        "Alternate Heel Touchers" to "Střídavé dotyky pat",
        "Alternate Incline Dumbbell Curl" to "Střídavý zdvih jednoruček na šikmé lavici",
        "Plank" to "Prkno",
        "Pushups" to "Kliky",
        "Push-Ups" to "Kliky",
        "Bodyweight Squat" to "Dřep s vlastní vahou",
        "Jumping Jack" to "Panák",
        "Mountain Climbers" to "Horolezec",
        "Glute Bridge" to "Most na hýždě",
        "Burpee" to "Burpee",
        "High Knees" to "Vysoká kolena"
    )
    exact[name]?.let { return it }

    var result = name
    val replacements = listOf(
        "Hamstring" to "zadních stehen",
        "Dumbbell" to "jednoručkami",
        "Barbell" to "velkou činkou",
        "Kettlebell" to "kettlebellem",
        "Cable" to "na kladce",
        "Machine" to "na stroji",
        "Incline" to "na šikmé lavici",
        "Decline" to "na negativní lavici",
        "Seated" to "vsedě",
        "Standing" to "vestoje",
        "Lying" to "vleže",
        "Alternate" to "střídavý",
        "One-Arm" to "jednoruční",
        "Single-Arm" to "jednoruční",
        "Curl" to "zdvih",
        "Press" to "tlak",
        "Extension" to "extenze",
        "Raise" to "zdvih",
        "Row" to "přítah",
        "Squat" to "dřep",
        "Lunge" to "výpad",
        "Stretch" to "protažení"
    )
    replacements.forEach { (en, cs) -> result = result.replace(en, cs, ignoreCase = true) }
    return result.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

fun czRemoteInstructions(e: RemoteExercise): String {
    val originalName = cleanRemoteText(e.name).lowercase()
    val muscle = czMuscles(e.muscles).ifBlank { "cílovou svalovou skupinu" }
    val equipment = czEquipment(e.equipment)
    val category = czCategory(e.category)

    if ("90/90 hamstring" in originalName) {
        return "Lehni si na záda a jednu nohu nech nataženou na podložce.\n\nDruhou nohu pokrč v kyčli a koleni přibližně do pravého úhlu. Podle potřeby si stehno přidrž rukama.\n\nPomalu natahuj pokrčenou nohu vzhůru, dokud necítíš příjemné protažení zadní strany stehna. Na okamžik vydrž a vrať se zpět.\n\nOpakuj 10–20krát a potom vystřídej nohy."
    }

    return when (cleanRemoteText(e.category).lowercase()) {
        "stretching" -> "Zaujmi stabilní výchozí polohu a uvolni zbytečné napětí. Zaměř se na $muscle.\n\nPohyb prováděj pomalu a kontrolovaně jen do rozsahu, ve kterém cítíš příjemný tah, ne bolest.\n\nV krajní poloze krátce vydrž, klidně dýchej a potom se pomalu vrať. Proveď stejně i druhou stranu, pokud je cvik jednostranný."
        "cardio", "plyometrics" -> "Začni v pevné a stabilní poloze. Pohyb prováděj plynule a drž trup pod kontrolou.\n\nTempo zvyšuj postupně. Dopadej měkce a nepokračuj, pokud se rozpadá technika.\n\nCvik zatěžuje hlavně $muscle."
        "strength", "powerlifting", "olympic weightlifting", "strongman" -> "Připrav si $equipment a nastav pevnou výchozí pozici. Zpevni střed těla a drž klouby v přirozené ose.\n\nPohyb veď kontrolovaně v celém bezpečném rozsahu. Nevyužívej švih, pokud není součástí dané techniky.\n\nZaměř se hlavně na $muscle a ukonči sérii dřív, než začne výrazně klesat kvalita provedení."
        else -> "Zaujmi stabilní výchozí polohu. Pohyb prováděj pomalu a pod kontrolou.\n\nSoustřeď se na $muscle a drž plynulé dýchání. Rozsah přizpůsob tak, aby byl cvik pohodlný a bez ostré bolesti.\n\nTyp cviku: ${if (category.isBlank()) "obecné cvičení" else category}."
    }
}

fun localizedRemoteExercise(e: RemoteExercise): RemoteExercise = e.copy(
    name = czExerciseName(cleanRemoteText(e.name, "Cvik")),
    level = czLevel(e.level),
    category = czCategory(e.category),
    equipment = czEquipment(e.equipment),
    muscles = czMuscles(e.muscles),
    instructions = czRemoteInstructions(e)
)

@Composable
fun ExerciseVisual(exerciseName: String, modifier: Modifier = Modifier) {
    val motion = rememberInfiniteTransition(label = "exerciseMotion")
    val p by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "phase"
    )
    val name = exerciseName.lowercase()

    Canvas(modifier.background(VisualBg, RoundedCornerShape(12))) {
        val w = size.width
        val h = size.height
        val sw = (w * .025f).coerceAtLeast(3f)
        fun line(a: Offset, b: Offset, color: Color = VisualBody) = drawLine(color, a, b, sw, cap = StrokeCap.Round)
        fun head(c: Offset, r: Float = w * .065f) = drawCircle(VisualAccent, r, c)
        fun floor(y: Float = h * .83f) = drawLine(VisualFloor, Offset(w * .1f, y), Offset(w * .9f, y), sw * .45f, cap = StrokeCap.Round)

        when {
            "plank" in name || "prkno" in name || "mountain" in name || "horolezec" in name || "shoulder tap" in name -> {
                floor()
                val hipY = h * (.53f + .025f * p)
                val shoulder = Offset(w * .38f, h * .52f)
                val hip = Offset(w * .60f, hipY)
                val heel = Offset(w * .82f, h * .67f)
                head(Offset(w * .27f, h * .43f))
                line(shoulder, hip); line(hip, heel)
                line(shoulder, Offset(w * .31f, h * .78f)); line(shoulder, Offset(w * .43f, h * .78f))
                if ("mountain" in name || "horolezec" in name) {
                    val knee = Offset(w * (.63f - .12f * p), h * (.69f - .13f * p))
                    line(hip, knee); line(knee, Offset(w * .72f, h * .79f))
                    line(hip, heel)
                } else if ("shoulder tap" in name) {
                    line(shoulder, Offset(w * (.48f + .08f * p), h * (.43f - .08f * p)))
                    line(shoulder, Offset(w * .31f, h * .78f))
                } else {
                    line(hip, Offset(w * .73f, h * .78f)); line(hip, heel)
                }
            }
            "side plank" in name -> {
                floor()
                val shoulder = Offset(w * .36f, h * .48f)
                val hip = Offset(w * .58f, h * (.54f - .03f * p))
                val feet = Offset(w * .80f, h * .70f)
                head(Offset(w * .27f, h * .40f))
                line(shoulder, hip); line(hip, feet)
                line(shoulder, Offset(w * .38f, h * .78f))
                line(shoulder, Offset(w * .38f, h * .23f))
            }
            "klik" in name || "push" in name -> {
                floor()
                val y = h * (.48f + .08f * p)
                val shoulder = Offset(w * .36f, y)
                val hip = Offset(w * .60f, y + h * .04f)
                val feet = Offset(w * .83f, h * .74f)
                head(Offset(w * .25f, y - h * .05f))
                line(shoulder, hip); line(hip, feet)
                line(shoulder, Offset(w * .32f, h * .77f)); line(shoulder, Offset(w * .45f, h * .77f))
            }
            "dřep" in name || "squat" in name || "wall sit" in name -> {
                floor()
                val hipY = h * (.50f + .15f * p)
                head(Offset(w * .50f, hipY - h * .31f))
                val neck = Offset(w * .50f, hipY - h * .22f)
                val hip = Offset(w * .50f, hipY)
                line(neck, hip)
                line(Offset(w * .38f, hipY - h * .14f), Offset(w * .62f, hipY - h * .14f))
                val kneeL = Offset(w * .34f, hipY + h * .13f)
                val kneeR = Offset(w * .66f, hipY + h * .13f)
                line(hip, kneeL); line(kneeL, Offset(w * .29f, h * .82f))
                line(hip, kneeR); line(kneeR, Offset(w * .71f, h * .82f))
                if ("wall sit" in name) line(Offset(w * .72f, h * .18f), Offset(w * .72f, h * .83f), VisualFloor)
            }
            "výpad" in name || "lunge" in name || "bulhars" in name -> {
                floor()
                val hip = Offset(w * .50f, h * (.49f + .08f * p))
                head(Offset(w * .50f, hip.y - h * .27f)); line(Offset(w * .50f, hip.y - h * .19f), hip)
                line(Offset(w * .38f, hip.y - h * .12f), Offset(w * .62f, hip.y - h * .12f))
                val frontKnee = Offset(w * .68f, h * .66f)
                val backKnee = Offset(w * .34f, h * (.67f + .06f * p))
                line(hip, frontKnee); line(frontKnee, Offset(w * .75f, h * .82f))
                line(hip, backKnee); line(backKnee, Offset(w * .22f, h * .82f))
            }
            "bridge" in name || "most" in name -> {
                floor(h * .76f)
                val shoulder = Offset(w * .25f, h * .69f)
                val hip = Offset(w * .51f, h * (.62f - .13f * p))
                val knee = Offset(w * .70f, h * .58f)
                head(Offset(w * .16f, h * .66f)); line(shoulder, hip); line(hip, knee); line(knee, Offset(w * .80f, h * .76f))
            }
            "crunch" in name || "zkrac" in name || "dead bug" in name || "russian" in name -> {
                floor(h * .76f)
                val hip = Offset(w * .49f, h * .67f)
                val shoulder = Offset(w * (.34f + .05f * p), h * (.61f - .09f * p))
                head(Offset(w * (.27f + .05f * p), h * (.55f - .10f * p)))
                line(shoulder, hip)
                val knee = Offset(w * .67f, h * .55f); line(hip, knee); line(knee, Offset(w * .78f, h * .76f))
                line(shoulder, Offset(w * .57f, h * .48f))
            }
            "superman" in name -> {
                floor(h * .73f)
                val bodyY = h * (.62f - .05f * p)
                head(Offset(w * .30f, bodyY - h * .02f)); line(Offset(w * .36f, bodyY), Offset(w * .60f, bodyY))
                line(Offset(w * .38f, bodyY), Offset(w * .17f, bodyY - h * .16f))
                line(Offset(w * .60f, bodyY), Offset(w * .82f, bodyY - h * .13f))
            }
            "bird dog" in name || "bear crawl" in name || "donkey" in name || "hydrant" in name -> {
                floor()
                val shoulder = Offset(w * .38f, h * .48f); val hip = Offset(w * .59f, h * .53f)
                head(Offset(w * .28f, h * .43f)); line(shoulder, hip)
                line(shoulder, Offset(w * .33f, h * .80f))
                if ("bird dog" in name) {
                    line(shoulder, Offset(w * .18f, h * (.36f - .07f * p)))
                    line(hip, Offset(w * .83f, h * (.48f - .06f * p)))
                } else if ("donkey" in name) {
                    line(hip, Offset(w * .70f, h * (.58f - .16f * p))); line(Offset(w * .70f, h * (.58f - .16f * p)), Offset(w * .80f, h * (.48f - .15f * p)))
                } else {
                    line(hip, Offset(w * .72f, h * .72f)); line(Offset(w * .72f, h * .72f), Offset(w * .79f, h * .81f))
                }
            }
            "jump" in name || "high knees" in name || "skater" in name || "burpee" in name -> {
                floor()
                val y = h * (.47f - .06f * p)
                head(Offset(w * .50f, y - h * .23f)); val neck = Offset(w * .50f, y - h * .15f); val hip = Offset(w * .50f, y + h * .08f)
                line(neck, hip)
                val armSpread = w * (.17f + .13f * p); line(Offset(w*.50f,y-h*.08f), Offset(w*.50f-armSpread,y-h*(.08f+.22f*p))); line(Offset(w*.50f,y-h*.08f), Offset(w*.50f+armSpread,y-h*(.08f+.22f*p)))
                val legSpread = w * (.10f + .14f * p); line(hip, Offset(w*.50f-legSpread,h*.80f)); line(hip, Offset(w*.50f+legSpread,h*.80f))
            }
            "hinge" in name -> {
                floor()
                val hip = Offset(w * .52f, h * .54f); val shoulder = Offset(w * (.43f - .10f * p), h * (.36f + .12f * p))
                head(Offset(shoulder.x - w*.06f, shoulder.y - h*.08f)); line(shoulder, hip)
                line(hip, Offset(w*.42f,h*.81f)); line(hip, Offset(w*.63f,h*.81f)); line(shoulder, Offset(w*.61f,h*.61f))
            }
            else -> {
                floor()
                val y = h * (.46f + .03f * p)
                head(Offset(w*.50f,y-h*.24f)); line(Offset(w*.50f,y-h*.15f), Offset(w*.50f,y+h*.09f))
                line(Offset(w*.50f,y-h*.08f), Offset(w*.30f,y+h*.01f)); line(Offset(w*.50f,y-h*.08f), Offset(w*.70f,y+h*.01f))
                line(Offset(w*.50f,y+h*.09f), Offset(w*.38f,h*.81f)); line(Offset(w*.50f,y+h*.09f), Offset(w*.62f,h*.81f))
            }
        }
    }
}
