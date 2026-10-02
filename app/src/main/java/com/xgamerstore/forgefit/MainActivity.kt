@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.xgamerstore.forgefit

import android.os.Bundle
import android.content.Context
import java.time.LocalDate
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale


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

data class RemoteExercise(
    val name: String,
    val level: String,
    val category: String,
    val equipment: String,
    val muscles: String,
    val instructions: String,
    val imageUrl: String?
)

val exerciseLibrary = listOf(
    Exercise("Dřep","Nohy • hýždě","Začátečník",3,12,0,50,
        "Chodidla na šířku ramen, kolena sledují špičky a hrudník drž vysoko.",
        "Nehrb záda a nenech kolena padat dovnitř."),
    Exercise("Klik","Hrudník • triceps • střed těla","Střední",3,10,0,60,
        "Tělo drž v jedné linii. Hrudník spouštěj kontrolovaně k podlaze.",
        "Neprohýbej bedra a nezkracuj rozsah."),
    Exercise("Prkno","Střed těla • ramena","Začátečník",3,0,35,40,
        "Lokty dej pod ramena. Zpevni břicho a hýždě a drž rovnou linii.",
        "Nenech boky propadnout ani vyjet příliš vysoko."),
    Exercise("Výpad vzad","Nohy • hýždě","Začátečník",3,10,0,45,
        "Krokni vzad, přední chodidlo nech celé na zemi a klesej kolmo dolů.",
        "Přední koleno netlač dovnitř."),
    Exercise("Panák","Kondice • celé tělo","Začátečník",3,0,40,30,
        "Skákej lehce, současně roznož a zvedni ruce nad hlavu.",
        "Nedopadej tvrdě na propnutá kolena."),
    Exercise("Most na hýždě","Hýždě • zadní stehna","Začátečník",3,15,0,40,
        "Lehni na záda a vytlač pánev vzhůru přes paty.",
        "Nevytahuj pohyb z beder; nahoře zatni hýždě."),
    Exercise("Zkracovačky","Břicho","Začátečník",3,15,0,35,
        "Bedra nech na podložce a zvedej lopatky silou břicha.",
        "Netahej hlavu rukama."),
    Exercise("Horolezec","Střed těla • kondice","Střední",3,0,35,35,
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
    Exercise("Sed u zdi","Nohy • kvadricepsy","Začátečník",3,0,40,40,
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
    Exercise("Klik ve střeše","Ramena • triceps","Střední",3,10,0,60,
        "Boky dej vysoko do tvaru obráceného V a hlavu spouštěj mezi dlaně.",
        "Neztrácej pevný střed těla."),
    Exercise("Zvedání paží a nohou vleže","Záda • hýždě","Začátečník",3,12,0,40,
        "Lehni na břicho a současně zvedni paže i nohy jen do příjemného rozsahu.",
        "Nepřeháněj záklon v bedrech."),
    Exercise("Vzpažení a zanožení na čtyřech","Střed těla • záda","Začátečník",3,10,0,35,
        "Na čtyřech natahuj opačnou paži a nohu a drž pánev stabilní.",
        "Nevytáčej boky do strany."),
    Exercise("Mrtvý brouk","Střed těla","Začátečník",3,10,0,35,
        "Bedra přitlač k podložce a střídavě spouštěj opačnou ruku a nohu.",
        "Jakmile se bedra odlepí, zkrať rozsah."),
    Exercise("Boční prkno","Šikmé břišní svaly • ramena","Střední",3,0,30,40,
        "Loket drž pod ramenem a tělo v jedné linii od hlavy k patám.",
        "Nenech boky propadnout k zemi."),
    Exercise("Ruské otáčení","Břicho • šikmé břišní svaly","Střední",3,16,0,40,
        "Sedni si, lehce zakloň trup a otáčej hrudník ze strany na stranu.",
        "Neotáčej pouze pažemi bez pohybu trupu."),
    Exercise("Běh s vysokými koleny","Kondice • střed těla","Střední",3,0,40,30,
        "Běž na místě a zvedej kolena svižně vzhůru při aktivní práci paží.",
        "Nezakláněj trup a nedupej."),
    Exercise("Angličák","Celé tělo • kondice","Pokročilý",3,10,0,70,
        "Z postoje přejdi do opory, vrať nohy vpřed a zakonči výskokem.",
        "Nepropadej se v bedrech při přechodu do prkna."),
    Exercise("Medvědí chůze","Střed těla • ramena • celé tělo","Střední",3,0,35,45,
        "Na čtyřech zvedni kolena těsně nad zem a postupuj protilehlou rukou a nohou.",
        "Nehoupej boky ze strany na stranu."),
    Exercise("Zanožování na čtyřech","Hýždě","Začátečník",3,14,0,35,
        "Na čtyřech tlač patu vzhůru a drž pánev rovně.",
        "Nevytáčej kyčel a neprohýbej bedra."),
    Exercise("Unožování na čtyřech","Hýždě • boky","Začátečník",3,14,0,35,
        "Na čtyřech zvedej pokrčené koleno do strany bez rotace trupu.",
        "Nepřenášej váhu prudce na druhou stranu."),
    Exercise("Předklon v kyčlích","Zadní stehna • hýždě • záda","Začátečník",3,12,0,45,
        "Posouvej boky dozadu s rovnými zády, jako bys zavíral dveře hýžděmi.",
        "Nedělej z pohybu dřep a nekulať bedra."),
    Exercise("Obrácené zkracovačky","Spodní část břicha","Střední",3,12,0,40,
        "Přitahuj kolena k hrudníku a lehce zvedni pánev z podložky.",
        "Nehoupej nohama a nepoužívej setrvačnost."),
    Exercise("Dotyky ramen v prkně","Střed těla • ramena","Střední",3,16,0,40,
        "Ve vysokém prkně střídavě dotýkej opačného ramene a drž pánev bez rotace.",
        "Neroztáčej boky při každém doteku."),
    Exercise("Bruslařské přeskoky","Kondice • nohy","Střední",3,0,40,35,
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
            "Břicho" in profile.focus -> e.muscle.contains("Břicho") || e.muscle.contains("Střed těla")
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

    fun saveRpe(value:Int) {
        val count = p.getInt("rpe_count",0)
        val sum = p.getInt("rpe_sum",0)
        p.edit().putInt("rpe_count",count+1).putInt("rpe_sum",sum+value).apply()
    }

    fun averageRpe(): Float {
        val count = p.getInt("rpe_count",0)
        return if(count==0) 0f else p.getInt("rpe_sum",0).toFloat()/count
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


private val Bg = Color(0xFF0B0D10)
private val Panel = Color(0xFF15191F)
private val Panel2 = Color(0xFF20262E)
private val Orange = Color(0xFFFF5A36)
private val Gold = Color(0xFFFFB347)
private val Muted = Color(0xFF9AA4AF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme=darkColorScheme(primary=Orange,onPrimary=Color.Black,secondary=Gold,onSecondary=Color.Black,background=Bg,onBackground=Color(0xFFF4F1F7),surface=Panel,onSurface=Color(0xFFF4F1F7),surfaceVariant=Panel2,onSurfaceVariant=Muted)) {
                val store = remember { FitStore(this) }
                App(store)
            }
        }
    }
}

@Composable
fun App(store: FitStore) {
    var profile by remember { mutableStateOf(store.loadProfile()) }
    var ready by remember { mutableStateOf(store.hasProfile()) }
    if(!ready) {
        Onboarding(profile) {
            profile=it
            store.saveProfile(it)
            ready=true
        }
    } else {
        MainArea(profile,store) {
            profile=it
            store.saveProfile(it)
        }
    }
}

@Composable
fun Onboarding(start:UserProfile, finish:(UserProfile)->Unit) {
    var step by remember { mutableIntStateOf(0) }
    var u by remember { mutableStateOf(start) }
    Column(Modifier.fillMaxSize().background(Bg).padding(20.dp)) {
        Spacer(Modifier.height(26.dp))
        Text("FORGEFIT",color=Orange,fontWeight=FontWeight.Black)
        Text("Nejdřív tě potřebuju poznat.",fontSize=28.sp,fontWeight=FontWeight.Black)
        Text("Jo, tohle je ta otravná část. Vyplníš ji jednou a pak už jen cvičíš.",color=Muted)
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(progress={ (step+1)/6f },modifier=Modifier.fillMaxWidth().height(6.dp),color=Orange,trackColor=Panel2)
        Spacer(Modifier.height(18.dp))
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when(step) {
                0 -> Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Title("1. O tobě")
                    OutlinedTextField(u.name,{u=u.copy(name=it)},label={Text("Jméno / přezdívka")},modifier=Modifier.fillMaxWidth())
                    Stepper("Věk",u.age,"let",13,100){u=u.copy(age=it)}
                    Chips("Pohlaví",listOf("Muž","Žena","Nechci uvést"),u.sex){u=u.copy(sex=it)}
                }
                1 -> Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Title("2. Tvoje tělo")
                    Stepper("Výška",u.height,"cm",120,230){u=u.copy(height=it)}
                    Stepper("Hmotnost",u.weight,"kg",35,250){u=u.copy(weight=it)}
                    Note("Údaje zůstávají lokálně v telefonu. Používají se pro profil a plán.")
                }
                2 -> Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    Title("3. Co chceš zlepšit?")
                    listOf("Kondice","Síla","Nabrat svaly","Zhubnout","Pravidelný pohyb").forEach { x ->
                        Pick(x,u.goal==x){u=u.copy(goal=x)}
                    }
                    Chips("Zkušenosti",listOf("Začátečník","Mírně pokročilý","Pokročilý"),u.experience){u=u.copy(experience=it)}
                }
                3 -> Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Title("4. Jak chceš cvičit?")
                    Stepper("Tréninky týdně",u.days,"×",1,7){u=u.copy(days=it)}
                    Stepper("Délka tréninku",u.minutes,"min",10,120,5){u=u.copy(minutes=it)}
                    Chips("Místo",listOf("Doma","Posilovna","Venku","Různě"),u.place){u=u.copy(place=it)}
                }
                4 -> Column(Modifier.verticalScroll(rememberScrollState())) {
                    Title("5. Jaké máš vybavení?")
                    Multi(listOf("Vlastní váha","Jednoručky","Velká činka","Lavice","Hrazda","Gumy","Kettlebell","Stroje"),u.equipment){u=u.copy(equipment=it)}
                }
                else -> Column(Modifier.verticalScroll(rememberScrollState())) {
                    Title("6. Na co se zaměřit?")
                    Multi(listOf("Celé tělo","Nohy","Hýždě","Břicho","Hrudník","Záda","Ramena","Paže","Kondice"),u.focus){u=u.copy(focus=it)}
                    Spacer(Modifier.height(14.dp))
                    Note("Při bolesti nebo zdravotním omezení automatický plán nenahrazuje doporučení zdravotníka.")
                }
            }
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)) {
            if(step>0) OutlinedButton({step--},Modifier.weight(1f)){Text("Zpět")}
            Button({if(step<5) step++ else finish(u)},Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=Orange)) {
                Text(if(step<5) "Pokračovat" else "Vytvořit plán",fontWeight=FontWeight.Bold)
            }
        }
    }
}

@Composable fun Title(t:String){Text(t,fontSize=22.sp,fontWeight=FontWeight.Bold)}
@Composable
fun Stepper(label:String,value:Int,suffix:String,min:Int,max:Int,step:Int=1,set:(Int)->Unit) {
    Surface(color=Panel,shape=RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f)){Text(label,color=Muted,fontSize=12.sp);Text(value.toString()+" "+suffix,fontSize=21.sp,fontWeight=FontWeight.Bold)}
            FilledIconButton({set((value-step).coerceAtLeast(min))},colors=IconButtonDefaults.filledIconButtonColors(containerColor=Panel2)){Icon(Icons.Default.Remove,null)}
            Spacer(Modifier.width(8.dp))
            FilledIconButton({set((value+step).coerceAtMost(max))},colors=IconButtonDefaults.filledIconButtonColors(containerColor=Orange)){Icon(Icons.Default.Add,null)}
        }
    }
}
@Composable
fun Chips(label:String,options:List<String>,selected:String,set:(String)->Unit) {
    Column { Text(label,color=Muted,fontSize=12.sp);Spacer(Modifier.height(5.dp))
        FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            options.forEach { x -> FilterChip(selected==x,{set(x)},{Text(x)}) }
        }
    }
}
@Composable
fun Multi(options:List<String>,selected:Set<String>,set:(Set<String>)->Unit) {
    FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
        options.forEach { x -> FilterChip(x in selected,{
            val n=selected.toMutableSet(); if(x in n)n.remove(x) else n.add(x); if(n.isNotEmpty())set(n)
        },{Text(x)}) }
    }
}
@Composable
fun Pick(t:String,selected:Boolean,click:()->Unit) {
    Surface(color=if(selected)Orange.copy(alpha=.18f) else Panel,shape=RoundedCornerShape(14.dp),
        modifier=Modifier.fillMaxWidth().clickable{click()}) {
        Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically) {
            Text(t,Modifier.weight(1f),fontWeight=FontWeight.Bold)
            if(selected)Icon(Icons.Default.CheckCircle,null,tint=Orange)
        }
    }
}
@Composable
fun Note(t:String){Surface(color=Color(0xFF13212A),shape=RoundedCornerShape(14.dp)){Text(t,Modifier.padding(14.dp),color=Color(0xFFD7EEFF),fontSize=13.sp)}}

@Composable
fun MainArea(profile:UserProfile,store:FitStore,save:(UserProfile)->Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var training by remember { mutableStateOf(false) }
    var extra by remember { mutableStateOf<String?>(null) }
    if(training) { WorkoutScreen(profile,store){training=false}; return }
    when(extra) {
        "settings" -> { SettingsPage({extra=null},{extra="premium"}); return }
        "premium" -> { PremiumPage{extra="settings"}; return }
        "tools" -> { SmartToolsPage{extra=null}; return }
        "profileEdit" -> { Onboarding(profile){ save(it); extra=null }; return }
    }
    Scaffold(containerColor=Bg,bottomBar={
        NavigationBar(containerColor=Panel) {
            listOf("Dnes","Cviky","Přehled","Profil").forEachIndexed { i,t ->
                val icon=when(i){0->Icons.Default.Home;1->Icons.Default.FitnessCenter;2->Icons.Default.BarChart;else->Icons.Default.Person}
                NavigationBarItem(selected=tab==i,onClick={tab=i},icon={Icon(icon,null)},label={Text(t)})
            }
        }
    }) { p ->
        Box(Modifier.padding(p)) {
            when(tab){
                0->Home(profile,store){training=true}
                1->Library()
                2->Stats(profile,store)
                else->Profile(profile,save,
                    openSettings={extra="settings"},
                    openPremium={extra="premium"},
                    openTools={extra="tools"},
                    editProfile={extra="profileEdit"})
            }
        }
    }
}

@Composable
fun Home(profile:UserProfile,store:FitStore,start:()->Unit) {
    val stats=store.stats(); val plan=workoutFor(profile)
    LazyColumn(Modifier.fillMaxSize().background(Bg),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Spacer(Modifier.height(8.dp))
            Text(if(profile.name.isBlank())"Dnes makáme." else "Ahoj, "+profile.name+".",fontSize=28.sp,fontWeight=FontWeight.Black)
            Text("Cíl: "+profile.goal+" • "+profile.days+"× týdně",color=Muted)
        }
        item {
            Surface(color=Panel,shape=RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("DNEŠNÍ FORGE",color=Orange,fontWeight=FontWeight.Black)
                    Text(profile.minutes.toString()+" minut • "+plan.size+" cviků",fontSize=23.sp,fontWeight=FontWeight.Bold)
                    Text(plan.sumOf{it.sets}.toString()+" pracovních sérií",color=Muted)
                    Spacer(Modifier.height(15.dp))
                    Button(start,Modifier.fillMaxWidth().height(54.dp),colors=ButtonDefaults.buttonColors(containerColor=Orange)) {
                        Icon(Icons.Default.PlayArrow,null);Spacer(Modifier.width(6.dp));Text("ZAČÍT TRÉNINK",fontWeight=FontWeight.Black)
                    }
                }
            }
        }
        item {
            Text("Tento týden",fontSize=20.sp,fontWeight=FontWeight.Bold)
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                SmallStat(stats[0].toString()+"/"+profile.days,"tréninky",Modifier.weight(1f))
                SmallStat(stats[1].toString(),"minut",Modifier.weight(1f))
                SmallStat(stats[2].toString(),"sérií",Modifier.weight(1f))
            }
        }
        item { Text("Dnešní cviky",fontSize=20.sp,fontWeight=FontWeight.Bold) }
        items(plan){ExerciseCard(it)}
    }
}

@Composable
fun SmallStat(v:String,l:String,m:Modifier=Modifier){Surface(color=Panel,shape=RoundedCornerShape(15.dp),modifier=m){Column(Modifier.padding(12.dp)){Text(v,fontSize=20.sp,fontWeight=FontWeight.Black,color=Gold);Text(l,color=Muted,fontSize=11.sp)}}}

@Composable
fun ExerciseCard(e:Exercise,click:(()->Unit)?=null) {
    Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().then(if(click==null)Modifier else Modifier.clickable{click()})) {
        Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
            ExerciseVisual(e.name,Modifier.size(62.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(e.name,fontWeight=FontWeight.Bold);Text(e.muscle,color=Muted,fontSize=12.sp)
                Text(if(e.seconds>0)e.sets.toString()+" × "+e.seconds+" s" else e.sets.toString()+" × "+e.reps,color=Gold,fontSize=13.sp)
            }
            Icon(Icons.Default.ChevronRight,null,tint=Muted)
        }
    }
}

@Composable
fun Demo(modifier:Modifier) {
    val tr=rememberInfiniteTransition(label="move")
    val x by tr.animateFloat(0f,1f,infiniteRepeatable(tween(900),RepeatMode.Reverse),label="x")
    Canvas(modifier.background(Panel2,RoundedCornerShape(12.dp))) {
        val c=Offset(size.width/2,size.height/2+10f*x)
        drawCircle(Gold,size.width*.11f,Offset(c.x,c.y-size.height*.24f))
        drawLine(Color.White,Offset(c.x,c.y-size.height*.14f),Offset(c.x,c.y+size.height*.12f),5f,cap=StrokeCap.Round)
        drawLine(Color.White,Offset(c.x,c.y-size.height*.05f),Offset(c.x-size.width*.22f,c.y+size.height*.02f),5f,cap=StrokeCap.Round)
        drawLine(Color.White,Offset(c.x,c.y-size.height*.05f),Offset(c.x+size.width*.22f,c.y+size.height*.02f),5f,cap=StrokeCap.Round)
        drawLine(Color.White,Offset(c.x,c.y+size.height*.12f),Offset(c.x-size.width*.16f,c.y+size.height*.34f),5f,cap=StrokeCap.Round)
        drawLine(Color.White,Offset(c.x,c.y+size.height*.12f),Offset(c.x+size.width*.16f,c.y+size.height*.34f),5f,cap=StrokeCap.Round)
    }
}

@Composable
fun Library() {
    var localSelected by remember { mutableStateOf<Exercise?>(null) }
    var remoteSelected by remember { mutableStateOf<RemoteExercise?>(null) }
    if(localSelected!=null) { Detail(localSelected!!){localSelected=null};return }
    if(remoteSelected!=null) { RemoteDetail(remoteSelected!!){remoteSelected=null};return }

    var query by remember { mutableStateOf("") }
    var online by remember { mutableStateOf<List<RemoteExercise>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            online = loadRemoteExercises()
        } catch(t:Throwable) {
            error = t.message ?: "Online databázi se nepodařilo načíst."
        } finally { loading=false }
    }

    val filteredOnline = remember(query,online) {
        if(query.isBlank()) online else online.filter {
            it.name.contains(query,true) || it.muscles.contains(query,true) ||
            it.equipment.contains(query,true) || it.category.contains(query,true)
        }
    }

    LazyColumn(Modifier.fillMaxSize().background(Bg),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Knihovna cviků",fontSize=28.sp,fontWeight=FontWeight.Black)
            Text("Offline základ + online databáze s obrázky a stovkami cviků.",color=Muted)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value=query,onValueChange={query=it},
                modifier=Modifier.fillMaxWidth(),
                singleLine=true,
                leadingIcon={Icon(Icons.Default.Search,null)},
                label={Text("Hledat cvik, sval nebo vybavení")}
            )
        }
        if(loading) item {
            Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){
                    CircularProgressIndicator(Modifier.size(22.dp),strokeWidth=3.dp)
                    Spacer(Modifier.width(12.dp));Text("Načítám online katalog…",color=Muted)
                }
            }
        }
        if(error!=null) item { Note("Online katalog není dostupný. Offline cviky fungují dál.") }

        if(filteredOnline.isNotEmpty()) {
            item { Text("Internetový katalog • "+filteredOnline.size+" výsledků",fontSize=18.sp,fontWeight=FontWeight.Bold) }
            items(filteredOnline.take(250)){e->RemoteExerciseCard(e){remoteSelected=e}}
        } else {
            val local = if(query.isBlank()) exerciseLibrary else exerciseLibrary.filter {
                it.name.contains(query,true) || it.muscle.contains(query,true)
            }
            item { Text("Místní knihovna • "+local.size+" cviků",fontSize=18.sp,fontWeight=FontWeight.Bold) }
            items(local){e->ExerciseCard(e){localSelected=e}}
        }
    }
}

suspend fun loadRemoteExercises(): List<RemoteExercise> = withContext(Dispatchers.IO) {
    val text = URL("https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/dist/exercises.json").readText()
    val arr = JSONArray(text)
    val result = ArrayList<RemoteExercise>(arr.length())
    for(i in 0 until arr.length()) {
        val o = arr.getJSONObject(i)
        val images = o.optJSONArray("images")
        val img = if(images!=null && images.length()>0)
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/"+images.getString(0)
        else null
        val primary = o.optJSONArray("primaryMuscles")
        val muscles = buildList {
            if(primary!=null) for(j in 0 until primary.length()) add(cleanRemoteText(primary.optString(j)))
        }.filter { it.isNotBlank() }.joinToString(", ")
        val inst = o.optJSONArray("instructions")
        val instructions = buildString {
            if(inst!=null) for(j in 0 until inst.length()) {
                val line=cleanRemoteText(inst.optString(j))
                if(line.isNotBlank()) {
                    if(isNotEmpty()) append("\n\n")
                    append(line)
                }
            }
        }
        result.add(localizedRemoteExercise(RemoteExercise(
            name=cleanRemoteText(o.optString("name"),"Cvik"),
            level=cleanRemoteText(o.optString("level")),
            category=cleanRemoteText(o.optString("category")),
            equipment=cleanRemoteText(o.optString("equipment"),"Bez vybavení"),
            muscles=muscles,
            instructions=instructions,
            imageUrl=img
        )))
    }
    result
}

@Composable
fun RemoteExerciseCard(e:RemoteExercise,click:()->Unit) {
    Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().clickable{click()}) {
        Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
            Surface(color=Panel2,shape=RoundedCornerShape(12.dp),modifier=Modifier.size(72.dp)) {
                if(e.imageUrl!=null) AsyncImage(model=e.imageUrl,contentDescription=e.name,contentScale=ContentScale.Crop)
                else Box(contentAlignment=Alignment.Center){Icon(Icons.Default.FitnessCenter,null,tint=Gold)}
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(e.name,fontWeight=FontWeight.Bold,maxLines=2)
                Text(listOf(e.muscles,e.equipment,e.category).filter{it.isNotBlank()}.joinToString(" • "),color=Muted,fontSize=12.sp,maxLines=2)
                if(e.level.isNotBlank()) Text(e.level,color=Gold,fontSize=12.sp)
            }
            Icon(Icons.Default.ChevronRight,null,tint=Muted)
        }
    }
}

@Composable
fun RemoteDetail(e:RemoteExercise,back:()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        IconButton(back){Icon(Icons.Default.ArrowBack,null)}
        Text(e.name,fontSize=30.sp,fontWeight=FontWeight.Black)
        Text(listOf(e.muscles,e.equipment,e.category,e.level).filter{it.isNotBlank()}.joinToString(" • "),color=Gold)
        Spacer(Modifier.height(14.dp))
        Surface(color=Panel,shape=RoundedCornerShape(22.dp),modifier=Modifier.fillMaxWidth().height(280.dp)) {
            if(e.imageUrl!=null) AsyncImage(model=e.imageUrl,contentDescription=e.name,contentScale=ContentScale.Fit)
            else Box(contentAlignment=Alignment.Center){Icon(Icons.Default.FitnessCenter,null,Modifier.size(80.dp),tint=Gold)}
        }
        Spacer(Modifier.height(18.dp))
        Text("Technika",fontSize=19.sp,fontWeight=FontWeight.Bold)
        Text(if(e.instructions.isBlank()) "Instrukce nejsou u tohoto záznamu dostupné." else e.instructions,color=Muted)
        Spacer(Modifier.height(18.dp))
        Note("Název, obtížnost, svaly a vybavení jsou lokalizované do češtiny. Podrobné pokyny online databáze mohou být u některých cviků stále v původním jazyce.")
    }
}

@Composable
fun Detail(e:Exercise,back:()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        IconButton(back){Icon(Icons.Default.ArrowBack,null)}
        Text(e.name,fontSize=30.sp,fontWeight=FontWeight.Black);Text(e.muscle,color=Gold);Spacer(Modifier.height(14.dp))
        Surface(color=Panel,shape=RoundedCornerShape(22.dp),modifier=Modifier.fillMaxWidth().height(240.dp)) {
            Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                ExerciseVisual(e.name,Modifier.size(190.dp));Text("Animovaná ukázka pohybu",color=Muted,fontSize=12.sp)
            }
        }
        Spacer(Modifier.height(18.dp));Text("Technika",fontSize=19.sp,fontWeight=FontWeight.Bold);Text(e.technique)
        Spacer(Modifier.height(14.dp));Text("Častá chyba",fontSize=19.sp,fontWeight=FontWeight.Bold,color=Color(0xFFFF9B86));Text(e.mistake,color=Muted)
        Spacer(Modifier.height(14.dp));Text("Plán",fontSize=19.sp,fontWeight=FontWeight.Bold)
        Text(if(e.seconds>0)e.sets.toString()+" série × "+e.seconds+" sekund • pauza "+e.rest+" s" else e.sets.toString()+" série × "+e.reps+" opakování • pauza "+e.rest+" s",color=Muted)
    }
}

@Composable
fun WorkoutScreen(profile:UserProfile,store:FitStore,close:()->Unit) {
    val plan=remember(profile){workoutFor(profile)}
    var i by remember{mutableIntStateOf(0)}
    var set by remember{mutableIntStateOf(1)}
    var rest by remember{mutableIntStateOf(0)}
    var totalSets by remember{mutableIntStateOf(0)}
    var totalReps by remember{mutableIntStateOf(0)}
    var done by remember{mutableStateOf(false)}
    var rpe by remember{mutableIntStateOf(8)}
    val e=plan[i.coerceAtMost(plan.lastIndex)]

    LaunchedEffect(rest) {
        if(rest>0) {
            delay(1000)
            rest--
        }
    }

    if(done) {
        Column(
            Modifier.fillMaxSize().background(Bg).padding(24.dp),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement=Arrangement.Center
        ) {
            Icon(Icons.Default.EmojiEvents,null,Modifier.size(80.dp),tint=Gold)
            Text("Trénink dokončen",fontSize=30.sp,fontWeight=FontWeight.Black,color=Color.White)
            Text(totalSets.toString()+" sérií • "+totalReps+" opakování",color=Muted)
            Spacer(Modifier.height(22.dp))
            Button({
                store.addWorkout(profile.minutes,totalSets,totalReps)
                close()
            },Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Orange)) {
                Text("ULOŽIT TRÉNINK")
            }
        }
        return
    }

    Column(Modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            IconButton(close){Icon(Icons.Default.Close,null,tint=Color.White)}
            Text((i+1).toString()+"/"+plan.size,color=Muted)
            Spacer(Modifier.weight(1f))
            Text("Série "+set+"/"+e.sets,color=Color.White)
        }
        LinearProgressIndicator(
            progress={ (i+(set.toFloat()/e.sets))/plan.size },
            modifier=Modifier.fillMaxWidth(),
            color=Orange,
            trackColor=Panel2
        )
        Spacer(Modifier.height(14.dp))
        Text(e.name,fontSize=30.sp,fontWeight=FontWeight.Black,color=Color.White)
        Text(e.muscle,color=Gold)
        Spacer(Modifier.height(10.dp))

        Surface(
            color=Panel,
            shape=RoundedCornerShape(24.dp),
            modifier=Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(
                horizontalAlignment=Alignment.CenterHorizontally,
                verticalArrangement=Arrangement.Center,
                modifier=Modifier.padding(vertical=10.dp)
            ) {
                ExerciseVisual(e.name,Modifier.size(220.dp))
                Spacer(Modifier.height(10.dp))
                Text(
                    if(e.seconds>0)e.seconds.toString()+" SEKUND" else e.reps.toString()+" OPAKOVÁNÍ",
                    fontSize=24.sp,
                    fontWeight=FontWeight.Black,
                    color=Color.White
                )
                Text(e.technique,Modifier.padding(horizontal=22.dp),textAlign=TextAlign.Center,color=Muted)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Jak těžká byla série? RPE "+rpe,color=Muted,fontSize=12.sp)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            (6..10).forEach { x ->
                FilterChip(selected=rpe==x,onClick={rpe=x},label={Text(x.toString())},modifier=Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(6.dp))

        // Stejná výška při cvičení i pauze: horní část obrazovky už neposkakuje.
        Box(
            Modifier.fillMaxWidth().height(126.dp),
            contentAlignment=Alignment.Center
        ) {
            if(rest>0) {
                Surface(color=Panel2,shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxSize()) {
                    Column(
                        Modifier.padding(10.dp),
                        horizontalAlignment=Alignment.CenterHorizontally,
                        verticalArrangement=Arrangement.Center
                    ) {
                        Text("PAUZA",color=Muted,fontSize=13.sp)
                        Text(rest.toString()+" s",fontSize=34.sp,fontWeight=FontWeight.Black,color=Gold)
                        TextButton({rest=0}){Text("Přeskočit")}
                    }
                }
            } else {
                Button({
                    store.saveRpe(rpe)
                    totalSets++
                    if(e.seconds==0) totalReps+=e.reps
                    if(set<e.sets) {
                        set++
                        rest=e.rest
                    } else if(i<plan.lastIndex) {
                        i++
                        set=1
                        rest=e.rest
                    } else done=true
                },Modifier.fillMaxWidth().height(58.dp),colors=ButtonDefaults.buttonColors(containerColor=Orange)) {
                    Icon(Icons.Default.Check,null)
                    Spacer(Modifier.width(6.dp))
                    Text("SÉRIE HOTOVÁ",fontWeight=FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun Stats(profile:UserProfile,store:FitStore) {
    val s=store.stats()
    val progress=(s[0].toFloat()/profile.days).coerceIn(0f,1f)
    val rpe=store.averageRpe()
    val plan=workoutFor(profile)
    val groups=listOf("Nohy","Hýždě","Břicho","Hrudník","Záda","Ramena","Kondice")
    val heat=groups.associateWith { g -> plan.count{it.muscle.contains(g,true)} }

    LazyColumn(Modifier.fillMaxSize().background(Bg),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Týdenní přehled",fontSize=28.sp,fontWeight=FontWeight.Black)
            Text("Co jsi opravdu odmakal.",color=Muted)
        }
        item {
            Surface(color=Panel,shape=RoundedCornerShape(20.dp)){
                Column(Modifier.padding(18.dp)){
                    Text((progress*100).toInt().toString()+" %",fontSize=34.sp,fontWeight=FontWeight.Black,color=Orange)
                    LinearProgressIndicator(progress={progress},Modifier.fillMaxWidth().height(8.dp),color=Orange,trackColor=Panel2)
                }
            }
        }
        item {Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SmallStat(s[0].toString()+"/"+profile.days,"tréninky",Modifier.weight(1f));SmallStat(s[1].toString(),"minut",Modifier.weight(1f))}}
        item {Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SmallStat(s[2].toString(),"sérií",Modifier.weight(1f));SmallStat(s[3].toString(),"opakování",Modifier.weight(1f))}}
        item {SmallStat(if(rpe==0f)"—" else String.format("%.1f",rpe),"průměrné RPE",Modifier.fillMaxWidth())}
        item {
            Surface(color=Panel,shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Svalové zatížení plánu",fontSize=19.sp,fontWeight=FontWeight.Bold)
                    Text("Kde má dnešní plán největší důraz.",color=Muted,fontSize=12.sp)
                    Spacer(Modifier.height(12.dp))
                    val max=(heat.values.maxOrNull()?:1).coerceAtLeast(1)
                    heat.forEach { (name,value) ->
                        Row(Modifier.fillMaxWidth().padding(vertical=5.dp),verticalAlignment=Alignment.CenterVertically) {
                            Text(name,Modifier.width(78.dp),fontSize=12.sp,color=Muted)
                            LinearProgressIndicator(progress={value.toFloat()/max},modifier=Modifier.weight(1f).height(8.dp),color=if(value>0)Orange else Panel2,trackColor=Panel2)
                            Text(value.toString(),Modifier.width(28.dp),textAlign=TextAlign.End,fontSize=12.sp)
                        }
                    }
                }
            }
        }
        item {SmallStat(s[4].toString(),"tréninků celkem",Modifier.fillMaxWidth())}
    }
}

@Composable
fun Profile(
    profile:UserProfile,
    save:(UserProfile)->Unit,
    openSettings:()->Unit,
    openPremium:()->Unit,
    openTools:()->Unit,
    editProfile:()->Unit
) {
    var u by remember{mutableStateOf(profile)}
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Profil",fontSize=28.sp,fontWeight=FontWeight.Black)
                Text("Plán, nástroje a nastavení na jednom místě.",color=Muted)
            }
            IconButton(openSettings){Icon(Icons.Default.Settings,null)}
        }
        Spacer(Modifier.height(16.dp))
        Surface(color=Color(0xFF251F2A),shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth().clickable{openPremium()}) {
            Row(Modifier.padding(18.dp),verticalAlignment=Alignment.CenterVertically) {
                Icon(Icons.Default.Star,null,tint=Gold,modifier=Modifier.size(34.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)){Text("ForgeFit Premium",fontWeight=FontWeight.Black,fontSize=19.sp);Text("Pokročilé statistiky, plány a historie",color=Muted,fontSize=12.sp)}
                Icon(Icons.Default.ChevronRight,null,tint=Muted)
            }
        }
        Spacer(Modifier.height(12.dp))
        ProfileAction(Icons.Default.Person,"Upravit vstupní test","Věk, tělo, cíl, vybavení a zaměření",editProfile)
        ProfileAction(Icons.Default.Calculate,"Chytré nástroje","1RM, kotouče a zahřívací série",openTools)
        ProfileAction(Icons.Default.Settings,"Nastavení","Vzhled, jednotky a připomínky",openSettings)
        Spacer(Modifier.height(18.dp))
        Text("Rychlé úpravy plánu",fontSize=18.sp,fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Stepper("Tréninky týdně",u.days,"×",1,7){u=u.copy(days=it)}
        Spacer(Modifier.height(10.dp))
        Stepper("Délka",u.minutes,"min",10,120,5){u=u.copy(minutes=it)}
        Spacer(Modifier.height(12.dp))
        Chips("Cíl",listOf("Kondice","Síla","Nabrat svaly","Zhubnout","Pravidelný pohyb"),u.goal){u=u.copy(goal=it)}
        Spacer(Modifier.height(12.dp))
        Chips("Úroveň",listOf("Začátečník","Mírně pokročilý","Pokročilý"),u.experience){u=u.copy(experience=it)}
        Spacer(Modifier.height(18.dp))
        Button({save(u)},Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Orange)){Text("Uložit změny",fontWeight=FontWeight.Bold)}
        Spacer(Modifier.height(18.dp))
        Note("Profil a statistiky zůstávají lokálně v telefonu. Online katalog cviků se načítá jen při otevření knihovny.")
    }
}

@Composable
fun ProfileAction(icon:androidx.compose.ui.graphics.vector.ImageVector,title:String,subtitle:String,click:()->Unit) {
    Surface(color=Panel,shape=RoundedCornerShape(16.dp),modifier=Modifier.fillMaxWidth().padding(vertical=5.dp).clickable{click()}) {
        Row(Modifier.padding(15.dp),verticalAlignment=Alignment.CenterVertically) {
            Icon(icon,null,tint=Gold)
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold);Text(subtitle,color=Muted,fontSize=12.sp)}
            Icon(Icons.Default.ChevronRight,null,tint=Muted)
        }
    }
}

@Composable
fun SettingsPage(back:()->Unit,premium:()->Unit) {
    var reminders by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(back){Icon(Icons.Default.ArrowBack,null,tint=Color.White)};Text("Nastavení",fontSize=28.sp,fontWeight=FontWeight.Black,color=Color.White)}
        Surface(color=Color(0xFF251F2A),shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth().clickable{premium()}) {
            Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Star,null,tint=Gold);Spacer(Modifier.width(12.dp));Text("ForgeFit Premium",Modifier.weight(1f),fontWeight=FontWeight.Bold,color=Color.White);Icon(Icons.Default.ChevronRight,null,tint=Color.White)}
        }
        Spacer(Modifier.height(18.dp))
        Text("APLIKACE",color=Orange,fontWeight=FontWeight.Black)
        SettingToggle("Připomínky","Upozornění na naplánovaný trénink",reminders){reminders=it}
        SettingInfo("Jednotky","Metrické • kg • cm")
        SettingInfo("Výchozí pauza","60 sekund")
        SettingInfo("Jazyk","Čeština")
        Spacer(Modifier.height(18.dp))
        Text("DATA",color=Orange,fontWeight=FontWeight.Black)
        SettingInfo("Místní profil","Zapnuto")
        SettingInfo("Internetová knihovna","Free Exercise DB")
    }
}

@Composable
fun SettingToggle(title:String,sub:String,value:Boolean,set:(Boolean)->Unit){
    Row(Modifier.fillMaxWidth().padding(vertical=12.dp),verticalAlignment=Alignment.CenterVertically){
        Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold,color=Color.White);Text(sub,color=Muted,fontSize=12.sp)}
        Switch(checked=value,onCheckedChange=set)
    }
}
@Composable
fun SettingInfo(title:String,sub:String){
    Row(Modifier.fillMaxWidth().padding(vertical=12.dp),verticalAlignment=Alignment.CenterVertically){
        Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.Bold,color=Color.White);Text(sub,color=Muted,fontSize=12.sp)}
        Icon(Icons.Default.ChevronRight,null,tint=Muted)
    }
}

@Composable
fun PremiumPage(back:()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(back){Icon(Icons.Default.ArrowBack,null)};Text("ForgeFit Premium",fontSize=28.sp,fontWeight=FontWeight.Black)}
        Surface(color=Color(0xFF251F2A),shape=RoundedCornerShape(24.dp),modifier=Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                Icon(Icons.Default.Star,null,tint=Gold,modifier=Modifier.size(58.dp))
                Spacer(Modifier.height(12.dp))
                Text("Více dat. Méně ruční práce.",fontSize=25.sp,fontWeight=FontWeight.Black,textAlign=TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                listOf("Pokročilé dlouhodobé statistiky","Více vlastních plánů","Rozšířená historie výkonu","Pokročilé cíle a doporučení").forEach {
                    Row(Modifier.fillMaxWidth().padding(vertical=6.dp)){Text("✓",color=Gold,fontWeight=FontWeight.Black);Spacer(Modifier.width(10.dp));Text(it)}
                }
                Spacer(Modifier.height(18.dp))
                Button({},enabled=false,modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Orange)){Text("PŘIPOJIT PLATBY GOOGLE PLAY")}
                Text("Nákup se zpřístupní až po založení produktu v Google Play Console.",color=Muted,fontSize=11.sp,textAlign=TextAlign.Center,modifier=Modifier.padding(top=8.dp))
            }
        }
    }
}

@Composable
fun SmartToolsPage(back:()->Unit) {
    var weight by remember { mutableIntStateOf(60) }
    var reps by remember { mutableIntStateOf(8) }
    var target by remember { mutableIntStateOf(100) }
    val oneRm=(weight*(1f+reps/30f)).toInt()
    val perSide=((target-20).coerceAtLeast(0)/2f)
    var rem=perSide
    val parts=mutableListOf<String>()
    listOf(25,20,15,10,5,2,1).forEach { p -> val n=(rem/p).toInt(); if(n>0){parts.add(n.toString()+"× "+p+" kg");rem-=n*p} }
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(back){Icon(Icons.Default.ArrowBack,null)};Text("Chytré nástroje",fontSize=28.sp,fontWeight=FontWeight.Black)}
        ToolCard("Odhad 1RM","Epleyho vzorec") {
            ToolStepper("Váha",weight,"kg",1,300){weight=it}
            ToolStepper("Opakování",reps,"×",1,20){reps=it}
            Text("≈ "+oneRm+" kg",fontSize=34.sp,fontWeight=FontWeight.Black,color=Gold)
        }
        Spacer(Modifier.height(12.dp))
        ToolCard("Kalkulačka kotoučů","Počítá s 20kg osou") {
            ToolStepper("Cílová váha",target,"kg",20,300,5){target=it}
            Text("Na každou stranu",color=Muted)
            Text(if(parts.isEmpty())"bez kotoučů" else parts.joinToString(" + "),fontSize=20.sp,fontWeight=FontWeight.Bold,color=Gold)
        }
        Spacer(Modifier.height(12.dp))
        ToolCard("Zahřívací série","Automatický návrh") {
            listOf(40,60,75,90).forEachIndexed { i,pct ->
                Row(Modifier.fillMaxWidth().padding(vertical=6.dp)){Text("Série "+(i+1),Modifier.weight(1f),color=Muted);Text((target*pct/100f).toInt().toString()+" kg",fontWeight=FontWeight.Bold)}
            }
        }
    }
}

@Composable
fun ToolCard(title:String,sub:String,content:@Composable ColumnScope.()->Unit) {
    Surface(color=Panel,shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) { Text(title,fontSize=20.sp,fontWeight=FontWeight.Black);Text(sub,color=Muted,fontSize=12.sp);Spacer(Modifier.height(12.dp));content() }
    }
}
@Composable
fun ToolStepper(label:String,value:Int,suffix:String,min:Int,max:Int,step:Int=1,set:(Int)->Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical=5.dp),verticalAlignment=Alignment.CenterVertically){
        Text(label,Modifier.weight(1f),color=Muted)
        IconButton({set((value-step).coerceAtLeast(min))}){Icon(Icons.Default.Remove,null)}
        Text(value.toString()+" "+suffix,Modifier.width(86.dp),textAlign=TextAlign.Center,fontWeight=FontWeight.Bold)
        IconButton({set((value+step).coerceAtMost(max))}){Icon(Icons.Default.Add,null)}
    }
}

