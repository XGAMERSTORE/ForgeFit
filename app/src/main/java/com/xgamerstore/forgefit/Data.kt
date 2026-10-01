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
        "Drž ramena nad dlaněmi a boky stabilní."),
    Exercise("Sumo dřep","Nohy • vnitřní stehna • hýždě","Začátečník",3,12,0,50,
        "Postav se široce, špičky lehce ven a klesej mezi kolena.",
        "Nekulati záda a netlač kolena dovnitř."),
    Exercise("Dřep s výskokem","Nohy • kondice","Střední",3,10,0,60,
        "Z dřepu se odraz vzhůru a dopadni měkce zpět do stabilního postoje.",
        "Nedopadej na propnutá kolena."),
    Exercise("Bulharský dřep","Nohy • hýždě","Střední",3,10,0,60,
        "Zadní nohu polož na vyvýšení a přední nohou kontrolovaně klesej.",
        "Nepadni trupem dopředu a nenech koleno utíkat dovnitř."),
    Exercise("Wall sit","Nohy • kvadricepsy","Začátečník",3,0,40,40,
        "Opři záda o stěnu a drž kolena přibližně v pravém úhlu.",
        "Nevytahuj boky výš, když začne cvik pálit."),
    Exercise("Výpony na lýtka","Lýtka","Začátečník",3,18,0,35,
        "Zvedej paty co nejvýš, nahoře krátce zastav a pomalu klesej.",
        "Nehoupej se a neodrážej pohybem celého těla."),
    Exercise("Klik na kolenou","Hrudník • triceps","Začátečník",3,12,0,50,
        "Kolena nech na zemi, tělo od kolen k hlavě drž v jedné přímce.",
        "Nevysazuj boky a nepadni hrudníkem bez kontroly."),
    Exercise("Diamantový klik","Triceps • hrudník","Pokročilý",3,8,0,70,
        "Dlaně dej blízko pod hrudník a lokty drž u těla.",
        "Neotevírej lokty příliš do stran."),
    Exercise("Pike push-up","Ramena • triceps","Střední",3,10,0,60,
        "Boky dej vysoko do tvaru obráceného V a hlavu spouštěj mezi dlaně.",
        "Neztrácej pevný střed těla."),
    Exercise("Superman","Záda • hýždě","Začátečník",3,12,0,40,
        "Lehni na břicho a současně zvedni paže i nohy jen do příjemného rozsahu.",
        "Nepřeháněj záklon v bedrech."),
    Exercise("Bird dog","Core • záda","Začátečník",3,10,0,35,
        "Na čtyřech natahuj opačnou paži a nohu a drž pánev stabilní.",
        "Nevytáčej boky do strany."),
    Exercise("Dead bug","Core","Začátečník",3,10,0,35,
        "Bedra přitlač k podložce a střídavě spouštěj opačnou ruku a nohu.",
        "Jakmile se bedra odlepí, zkrať rozsah."),
    Exercise("Side plank","Šikmé břišní svaly • ramena","Střední",3,0,30,40,
        "Loket drž pod ramenem a tělo v jedné linii od hlavy k patám.",
        "Nenech boky propadnout k zemi."),
    Exercise("Russian twist","Břicho • šikmé břišní svaly","Střední",3,16,0,40,
        "Sedni si, lehce zakloň trup a otáčej hrudník ze strany na stranu.",
        "Neotáčej pouze pažemi bez pohybu trupu."),
    Exercise("High knees","Kondice • core","Střední",3,0,40,30,
        "Běž na místě a zvedej kolena svižně vzhůru při aktivní práci paží.",
        "Nezakláněj trup a nedupej."),
    Exercise("Burpee","Celé tělo • kondice","Pokročilý",3,10,0,70,
        "Z postoje přejdi do opory, vrať nohy vpřed a zakonči výskokem.",
        "Nepropadej se v bedrech při přechodu do prkna."),
    Exercise("Bear crawl","Core • ramena • celé tělo","Střední",3,0,35,45,
        "Na čtyřech zvedni kolena těsně nad zem a postupuj protilehlou rukou a nohou.",
        "Nehoupej boky ze strany na stranu."),
    Exercise("Donkey kick","Hýždě","Začátečník",3,14,0,35,
        "Na čtyřech tlač patu vzhůru a drž pánev rovně.",
        "Nevytáčej kyčel a neprohýbej bedra."),
    Exercise("Fire hydrant","Hýždě • boky","Začátečník",3,14,0,35,
        "Na čtyřech zvedej pokrčené koleno do strany bez rotace trupu.",
        "Nepřenášej váhu prudce na druhou stranu."),
    Exercise("Hip hinge","Zadní stehna • hýždě • záda","Začátečník",3,12,0,45,
        "Posouvej boky dozadu s rovnými zády, jako bys zavíral dveře hýžděmi.",
        "Nedělej z pohybu dřep a nekulať bedra."),
    Exercise("Reverse crunch","Spodní část břicha","Střední",3,12,0,40,
        "Přitahuj kolena k hrudníku a lehce zvedni pánev z podložky.",
        "Nehoupej nohama a nepoužívej setrvačnost."),
    Exercise("Plank shoulder tap","Core • ramena","Střední",3,16,0,40,
        "Ve vysokém prkně střídavě dotýkej opačného ramene a drž pánev bez rotace.",
        "Neroztáčej boky při každém doteku."),
    Exercise("Skater","Kondice • nohy","Střední",3,0,40,35,
        "Přeskakuj do stran z jedné nohy na druhou a dopadej měkce.",
        "Nedopadej na ztuhlou nohu.")
)


fun workoutFor(profile: UserProfile): List<Exercise> {
    val count = when {
        profile.minutes <= 20 -> 4
        profile.minutes <= 40 -> 6
        else -> 8
    }
    val preferred = exerciseLibrary.filter { e ->
        when {
            "Nohy" in profile.focus -> e.muscle.contains("Nohy")
            "Hýždě" in profile.focus -> e.muscle.contains("Hýždě")
            "Břicho" in profile.focus -> e.muscle.contains("Břicho") || e.muscle.contains("Core")
            "Hrudník" in profile.focus -> e.muscle.contains("Hrudník")
            "Záda" in profile.focus -> e.muscle.contains("Záda")
            "Ramena" in profile.focus -> e.muscle.contains("ramena", ignoreCase = true) || e.muscle.contains("Ramena")
            "Kondice" in profile.focus -> e.muscle.contains("Kondice")
            else -> true
        }
    }
    val base = if (preferred.size >= count) preferred else exerciseLibrary
    val ordered = when(profile.goal) {
        "Kondice", "Zhubnout" -> base.sortedByDescending { it.seconds > 0 || it.muscle.contains("Kondice") }
        "Síla", "Nabrat svaly" -> base.sortedByDescending { it.seconds == 0 }
        else -> base
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
        p.edit()
            .putBoolean("done",true).putString("name",u.name).putInt("age",u.age)
            .putString("sex",u.sex).putInt("height",u.height).putInt("weight",u.weight)
            .putString("goal",u.goal).putString("experience",u.experience).putInt("days",u.days)
            .putInt("minutes",u.minutes).putString("place",u.place)
            .putStringSet("equipment",u.equipment).putStringSet("focus",u.focus)
            .apply()
    }

    fun addWorkout(minutes:Int, sets:Int, reps:Int) {
        ensureWeek()
        p.edit()
            .putInt("workouts",p.getInt("workouts",0)+1)
            .putInt("minutes_sum",p.getInt("minutes_sum",0)+minutes)
            .putInt("sets_sum",p.getInt("sets_sum",0)+sets)
            .putInt("reps_sum",p.getInt("reps_sum",0)+reps)
            .putInt("total",p.getInt("total",0)+1)
            .apply()
    }

    fun stats(): List<Int> {
        ensureWeek()
        return listOf(
            p.getInt("workouts",0),
            p.getInt("minutes_sum",0),
            p.getInt("sets_sum",0),
            p.getInt("reps_sum",0),
            p.getInt("total",0)
        )
    }

    private fun ensureWeek() {
        val d = LocalDate.now()
        val key = d.minusDays((d.dayOfWeek.value - 1).toLong()).toString()
        if (p.getString("week","") != key) {
            p.edit()
                .putString("week",key)
                .putInt("workouts",0)
                .putInt("minutes_sum",0)
                .putInt("sets_sum",0)
                .putInt("reps_sum",0)
                .apply()
        }
    }
}
