package com.xgamerstore.forgefit

import android.os.Bundle
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
import kotlin.math.sin

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
            MaterialTheme(colorScheme=darkColorScheme(primary=Orange,secondary=Gold,background=Bg,surface=Panel)) {
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
    if(training) { WorkoutScreen(profile,store){training=false}; return }
    Scaffold(containerColor=Bg,bottomBar={
        NavigationBar(containerColor=Panel) {
            listOf("Dnes","Cviky","Přehled","Profil").forEachIndexed { i,t ->
                val icon=when(i){0->Icons.Default.Home;1->Icons.Default.FitnessCenter;2->Icons.Default.BarChart;else->Icons.Default.Person}
                NavigationBarItem(tab==i,{tab=i},{Icon(icon,null)},{Text(t)})
            }
        }
    }) { p ->
        Box(Modifier.padding(p)) {
            when(tab){0->Home(profile,store){training=true};1->Library();2->Stats(profile,store);else->Profile(profile,save)}
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
            Demo(Modifier.size(62.dp))
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
    var selected by remember { mutableStateOf<Exercise?>(null) }
    if(selected!=null) { Detail(selected!!){selected=null};return }
    LazyColumn(Modifier.fillMaxSize().background(Bg),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
        item {Spacer(Modifier.height(8.dp));Text("Knihovna cviků",fontSize=28.sp,fontWeight=FontWeight.Black);Text("Technika, chyby a ukázka pohybu přímo v aplikaci.",color=Muted)}
        items(exerciseLibrary){e->ExerciseCard(e){selected=e}}
    }
}
@Composable
fun Detail(e:Exercise,back:()->Unit) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        IconButton(back){Icon(Icons.Default.ArrowBack,null)}
        Text(e.name,fontSize=30.sp,fontWeight=FontWeight.Black);Text(e.muscle,color=Gold);Spacer(Modifier.height(14.dp))
        Surface(color=Panel,shape=RoundedCornerShape(22.dp),modifier=Modifier.fillMaxWidth().height(240.dp)) {
            Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                Demo(Modifier.size(190.dp));Text("Offline animovaná ukázka",color=Muted,fontSize=12.sp)
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
    var i by remember{mutableIntStateOf(0)};var set by remember{mutableIntStateOf(1)}
    var rest by remember{mutableIntStateOf(0)};var totalSets by remember{mutableIntStateOf(0)};var totalReps by remember{mutableIntStateOf(0)}
    var done by remember{mutableStateOf(false)}
    val e=plan[i.coerceAtMost(plan.lastIndex)]
    LaunchedEffect(rest){if(rest>0){delay(1000);rest--}}
    if(done) {
        Column(Modifier.fillMaxSize().background(Bg).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
            Icon(Icons.Default.EmojiEvents,null,Modifier.size(80.dp),tint=Gold);Text("Forge dokončen.",fontSize=30.sp,fontWeight=FontWeight.Black)
            Text(totalSets.toString()+" sérií • "+totalReps+" opakování",color=Muted);Spacer(Modifier.height(22.dp))
            Button({store.addWorkout(profile.minutes,totalSets,totalReps);close()},Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Orange)){Text("ULOŽIT TRÉNINK")}
        };return
    }
    Column(Modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically){IconButton(close){Icon(Icons.Default.Close,null)};Text((i+1).toString()+"/"+plan.size,color=Muted);Spacer(Modifier.weight(1f));Text("Série "+set+"/"+e.sets)}
        LinearProgressIndicator(progress={ (i+(set.toFloat()/e.sets))/plan.size },modifier=Modifier.fillMaxWidth(),color=Orange,trackColor=Panel2)
        Spacer(Modifier.height(18.dp));Text(e.name,fontSize=32.sp,fontWeight=FontWeight.Black);Text(e.muscle,color=Gold);Spacer(Modifier.height(12.dp))
        Surface(color=Panel,shape=RoundedCornerShape(24.dp),modifier=Modifier.fillMaxWidth().weight(1f)) {
            Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
                Demo(Modifier.size(260.dp));Spacer(Modifier.height(12.dp))
                Text(if(e.seconds>0)e.seconds.toString()+" SEKUND" else e.reps.toString()+" OPAKOVÁNÍ",fontSize=24.sp,fontWeight=FontWeight.Black)
                Text(e.technique,Modifier.padding(horizontal=22.dp),textAlign=TextAlign.Center,color=Muted)
            }
        }
        Spacer(Modifier.height(12.dp))
        if(rest>0) {
            Surface(color=Panel2,shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("PAUZA",color=Muted);Text(rest.toString()+" s",fontSize=38.sp,fontWeight=FontWeight.Black,color=Gold);TextButton({rest=0}){Text("Přeskočit")}}
            }
        } else Button({
            totalSets++;if(e.seconds==0)totalReps+=e.reps
            if(set<e.sets){set++;rest=e.rest}else if(i<plan.lastIndex){i++;set=1;rest=e.rest}else done=true
        },Modifier.fillMaxWidth().height(58.dp),colors=ButtonDefaults.buttonColors(containerColor=Orange)){Icon(Icons.Default.Check,null);Spacer(Modifier.width(6.dp));Text("SÉRIE HOTOVÁ",fontWeight=FontWeight.Black)}
    }
}

@Composable
fun Stats(profile:UserProfile,store:FitStore) {
    val s=store.stats();val progress=(s[0].toFloat()/profile.days).coerceIn(0f,1f)
    LazyColumn(Modifier.fillMaxSize().background(Bg),contentPadding=PaddingValues(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item {Spacer(Modifier.height(8.dp));Text("Týdenní přehled",fontSize=28.sp,fontWeight=FontWeight.Black);Text("Co jsi opravdu odmakal.",color=Muted)}
        item {Surface(color=Panel,shape=RoundedCornerShape(20.dp)){Column(Modifier.padding(18.dp)){Text((progress*100).toInt().toString()+" %",fontSize=34.sp,fontWeight=FontWeight.Black,color=Orange);LinearProgressIndicator(progress={progress},Modifier.fillMaxWidth().height(8.dp),color=Orange,trackColor=Panel2)}}}
        item {Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SmallStat(s[0].toString()+"/"+profile.days,"tréninky",Modifier.weight(1f));SmallStat(s[1].toString(),"minut",Modifier.weight(1f))}}
        item {Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SmallStat(s[2].toString(),"sérií",Modifier.weight(1f));SmallStat(s[3].toString(),"opakování",Modifier.weight(1f))}}
        item {SmallStat(s[4].toString(),"tréninků celkem",Modifier.fillMaxWidth())}
    }
}

@Composable
fun Profile(profile:UserProfile,save:(UserProfile)->Unit) {
    var u by remember{mutableStateOf(profile)}
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Spacer(Modifier.height(8.dp));Text("Profil",fontSize=28.sp,fontWeight=FontWeight.Black);Text("Uprav plán, kdykoliv chceš.",color=Muted);Spacer(Modifier.height(16.dp))
        Stepper("Tréninky týdně",u.days,"×",1,7){u=u.copy(days=it)};Spacer(Modifier.height(10.dp))
        Stepper("Délka",u.minutes,"min",10,120,5){u=u.copy(minutes=it)};Spacer(Modifier.height(12.dp))
        Chips("Cíl",listOf("Kondice","Síla","Nabrat svaly","Zhubnout","Pravidelný pohyb"),u.goal){u=u.copy(goal=it)};Spacer(Modifier.height(12.dp))
        Chips("Úroveň",listOf("Začátečník","Mírně pokročilý","Pokročilý"),u.experience){u=u.copy(experience=it)};Spacer(Modifier.height(18.dp))
        Button({save(u)},Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Orange)){Text("Uložit změny",fontWeight=FontWeight.Bold)}
        Spacer(Modifier.height(18.dp));Note("ForgeFit v této verzi ukládá profil a statistiky lokálně. Bez účtu a bez cloudu.")
    }
}
