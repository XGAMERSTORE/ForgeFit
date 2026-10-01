package com.xgamerstore.forgefit

import android.content.Context
import java.time.LocalDate

data class UserProfile(
    val name: String = "",
    val age: Int = 25,
    val sex: String = "Nechci uvést",
    val height: Int = 175,
    val weight: Int = 75,
    val goal: String = "Kondice",
    val experience: String = "Začátečník",
    val days: Int = 3,
    val minutes: Int = 35,
    val place: String = "Doma",
    val equipment: Set<String> = setOf("Vlastní váha"),
    val focus: Set<String> = setOf("Celé tělo")
)

data class Exercise(
    val name: String,
    val muscle: String,
    val difficulty: String,
    val sets: Int,
    val reps: Int,
    val seconds: Int,
    val rest: Int,
    val technique: String,
    val mistake: String
)

val exerciseLibrary = listOf(
    Exercise("Dřep","Nohy • hýždě","Začátečník",3,12,0,50,
        "Chodidla na šířku ramen, kolena sledují špičky a hrudník drž vysoko.",
        "Nehrb záda a nenech kolena padat dovnitř."),
    Exercise("Klik","Hrudník • triceps • core","Střední",3,10,0,60,
        "Tělo drž v jedné linii. Hrudník spouštěj kontrolovaně k podlaze.",
        "Neprohýbej bedra a nezkracuj rozsah."),
    Exercise("Plank","Core • ramena","Začátečník",3,0,35,40,
        "Lokty dej pod ramena. Zpevni břicho a hýždě a drž rovnou linii.",
        "Nenech boky propadnout ani vyjet příliš vysoko."),
    Exercise("Výpad vzad","Nohy • hýždě","Začátečník",3,10,0,45,
        "Krokni vzad, přední chodidlo nech celé na zemi a klesej kolmo dolů.",
        "Přední koleno netlač dovnitř."),
    Exercise("Jumping jack","Kondice • celé tělo","Začátečník",3,0,40,30,
        "Skákej lehce, současně roznož a zvedni ruce nad hlavu.",
        "Nedopadej tvrdě na propnutá kolena."),
    Exercise("Glute bridge","Hýždě • zadní stehna","Začátečník",3,15,0,40,
        "Lehni na záda a vytlač pánev vzhůru přes paty.",
        "Nevytahuj pohyb z beder; nahoře zatni hýždě."),
    Exercise("Zkracovačky","Břicho","Začátečník",3,15,0,35,
        "Bedra nech na podložce a zvedej lopatky silou břicha.",
        "Netahej hlavu rukama."),
    Exercise("Mountain climber","Core • kondice","Střední",3,0,35,35,
        "Ve vysokém prkně střídavě přitahuj kolena k hrudníku.",
        "Drž ramena nad dlaněmi a boky stabilní.")
)

fun workoutFor(profile: UserProfile): List<Exercise> {
    val count = when {
        profile.minutes <= 20 -> 4
        profile.minutes <= 40 -> 6
        else -> 8
    }
    val ordered = when(profile.goal) {
        "Kondice", "Zhubnout" -> listOf(
            exerciseLibrary[4], exerciseLibrary[7], exerciseLibrary[0], exerciseLibrary[2],
            exerciseLibrary[3], exerciseLibrary[6], exerciseLibrary[1], exerciseLibrary[5]
        )
        "Síla", "Nabrat svaly" -> listOf(
            exerciseLibrary[0], exerciseLibrary[1], exerciseLibrary[3], exerciseLibrary[5],
            exerciseLibrary[2], exerciseLibrary[6], exerciseLibrary[7], exerciseLibrary[4]
        )
        else -> exerciseLibrary
    }
    return ordered.take(count)
}

class FitStore(context: Context) {
    private val p = context.getSharedPreferences("forgefit", Context.MODE_PRIVATE)

    fun hasProfile() = p.getBoolean("done", false)

    fun loadProfile() = UserProfile(
        name = p.getString("name","") ?: "",
        age = p.getInt("age",25),
        sex = p.getString("sex","Nechci uvést") ?: "Nechci uvést",
        height = p.getInt("height",175),
        weight = p.getInt("weight",75),
        goal = p.getString("goal","Kondice") ?: "Kondice",
        experience = p.getString("experience","Začátečník") ?: "Začátečník",
        days = p.getInt("days",3),
        minutes = p.getInt("minutes",35),
        place = p.getString("place","Doma") ?: "Doma",
        equipment = p.getStringSet("equipment",setOf("Vlastní váha")) ?: setOf("Vlastní váha"),
        focus = p.getStringSet("focus",setOf("Celé tělo")) ?: setOf("Celé tělo")
    )

    fun saveProfile(u: UserProfile) {
        p.edit().putBoolean("done",true).putString("name",u.name).putInt("age",u.age)
            .putString("sex",u.sex).putInt("height",u.height).putInt("weight",u.weight)
            .putString("goal",u.goal).putString("experience",u.experience).putInt("days",u.days)
            .putInt("minutes",u.minutes).putString("place",u.place)
            .putStringSet("equipment",u.equipment).putStringSet("focus",u.focus).apply()
    }

    fun addWorkout(minutes:Int, sets:Int, reps:Int) {
        ensureWeek()
        p.edit().putInt("workouts",p.getInt("workouts",0)+1)
            .putInt("minutes_sum",p.getInt("minutes_sum",0)+minutes)
            .putInt("sets_sum",p.getInt("sets_sum",0)+sets)
            .putInt("reps_sum",p.getInt("reps_sum",0)+reps)
            .putInt("total",p.getInt("total",0)+1).apply()
    }

    fun stats(): List<Int> {
        ensureWeek()
        return listOf(
            p.getInt("workouts",0), p.getInt("minutes_sum",0),
            p.getInt("sets_sum",0), p.getInt("reps_sum",0), p.getInt("total",0)
        )
    }

    private fun ensureWeek() {
        val d = LocalDate.now()
        val key = d.minusDays((d.dayOfWeek.value-1).toLong()).toString()
        if(p.getString("week","") != key) {
            p.edit().putString("week",key).putInt("workouts",0).putInt("minutes_sum",0)
                .putInt("sets_sum",0).putInt("reps_sum",0).apply()
        }
    }
}
