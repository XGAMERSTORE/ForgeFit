package com.xgamerstore.forgefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ForgeFitApp() }
    }
}

private val ForgeColors: DarkColorScheme
    @Composable get() = darkColorScheme(
        background = Color(0xFF0B0D10),
        surface = Color(0xFF141820),
        surfaceVariant = Color(0xFF1D232D),
        primary = Color(0xFFFF5A3C),
        secondary = Color(0xFFFFB248),
        onBackground = Color(0xFFF4F0F5),
        onSurface = Color(0xFFF4F0F5),
        onSurfaceVariant = Color(0xFFB7BCC7),
        onPrimary = Color(0xFF24110C)
    )

private enum class Screen { Home, Exercises, Progress, Profile, Settings, Premium, Workout }

private data class Exercise(
    val name: String,
    val area: String,
    val prescription: String,
    val reps: String,
    val cue: String
)

private val exercises = listOf(
    Exercise("Pike push-up", "Ramena • triceps", "3 × 10", "10 opakování", "Boky drž vysoko do obráceného V. Hlava míří mezi dlaně."),
    Exercise("Plank shoulder tap", "Core • ramena", "3 × 16", "16 dotyků", "Drž pánev stabilní a střídavě se dotýkej protilehlého ramene."),
    Exercise("Plank", "Core • ramena", "3 × 35 s", "35 sekund", "Tělo drž v jedné linii. Nepropadej se v bedrech."),
    Exercise("Side plank", "Šikmé břišní svaly • ramena", "3 × 30 s", "30 sekund", "Rameno drž nad loktem a boky vytlač vzhůru.")
)

@Composable
private fun ForgeFitApp() {
    MaterialTheme(colorScheme = ForgeColors) {
        var screen by rememberSaveable { mutableStateOf(Screen.Home) }
        var selectedExercise by rememberSaveable { mutableIntStateOf(0) }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (screen) {
                Screen.Settings -> SettingsScreen(
                    onBack = { screen = Screen.Profile },
                    onPremium = { screen = Screen.Premium }
                )
                Screen.Premium -> PremiumScreen(onBack = { screen = Screen.Settings })
                Screen.Workout -> WorkoutScreen(
                    exercise = exercises[selectedExercise],
                    index = selectedExercise,
                    total = exercises.size,
                    onClose = { screen = Screen.Home }
                )
                else -> MainShell(
                    screen = screen,
                    onScreen = { screen = it },
                    onSettings = { screen = Screen.Settings },
                    onPremium = { screen = Screen.Premium },
                    onExercise = { index ->
                        selectedExercise = index
                        screen = Screen.Workout
                    }
                )
            }
        }
    }
}

@Composable
private fun MainShell(
    screen: Screen,
    onScreen: (Screen) -> Unit,
    onSettings: () -> Unit,
    onPremium: () -> Unit,
    onExercise: (Int) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = Color(0xFF141820)
            ) {
                val items = listOf(
                    Screen.Home to Pair(Icons.Default.Home, "Dnes"),
                    Screen.Exercises to Pair(Icons.Default.FitnessCenter, "Cviky"),
                    Screen.Progress to Pair(Icons.Default.BarChart, "Přehled"),
                    Screen.Profile to Pair(Icons.Default.Person, "Profil")
                )
                items.forEach { (target, data) ->
                    NavigationBarItem(
                        selected = screen == target,
                        onClick = { onScreen(target) },
                        icon = { Icon(data.first, contentDescription = data.second) },
                        label = { Text(data.second) }
                    )
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).fillMaxSize().statusBarsPadding()
        ) {
            when (screen) {
                Screen.Home -> HomeScreen(onExercise)
                Screen.Exercises -> ExerciseListScreen(onExercise)
                Screen.Progress -> ProgressScreen()
                Screen.Profile -> ProfileScreen(onSettings, onPremium)
                else -> Unit
            }
        }
    }
}

@Composable
private fun Header(title: String, subtitle: String? = null, action: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 30.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp)
            }
        }
        action?.invoke()
    }
}

@Composable
private fun HomeScreen(onExercise: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Header("ForgeFit", "Dnešní trénink bez zbytečností")

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF171B23))
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("DNEŠNÍ FORGE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text("20 minut • 4 cviky", fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("12 pracovních sérií", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 17.sp)
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = { onExercise(0) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("ZAČÍT TRÉNINK", fontWeight = FontWeight.Black, fontSize = 17.sp)
                }
            }
        }

        Text("Tento týden", modifier = Modifier.padding(start = 22.dp, top = 22.dp, bottom = 8.dp), fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("1/3", "tréninky", Modifier.weight(1f))
            StatCard("20", "minut", Modifier.weight(1f))
            StatCard("12", "sérií", Modifier.weight(1f))
        }

        Text("Dnešní cviky", modifier = Modifier.padding(start = 22.dp, top = 24.dp, bottom = 8.dp), fontSize = 25.sp, fontWeight = FontWeight.Bold)
        exercises.forEachIndexed { index, exercise ->
            ExerciseCard(exercise = exercise, onClick = { onExercise(index) })
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF171B23))) {
        Column(Modifier.padding(18.dp)) {
            Text(value, color = MaterialTheme.colorScheme.secondary, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171B23))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(70.dp).background(Color(0xFF202731), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) { MiniExerciseIcon() }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(exercise.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(exercise.area, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text(exercise.prescription, color = MaterialTheme.colorScheme.secondary, fontSize = 16.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MiniExerciseIcon() {
    Canvas(Modifier.size(48.dp)) {
        val c = Color(0xFFF2F4F8)
        val accent = Color(0xFFFFB248)
        drawCircle(accent, radius = size.minDimension * .11f, center = Offset(size.width * .5f, size.height * .20f))
        drawLine(c, Offset(size.width*.5f,size.height*.32f), Offset(size.width*.5f,size.height*.58f), 4f, StrokeCap.Round)
        drawLine(c, Offset(size.width*.5f,size.height*.40f), Offset(size.width*.28f,size.height*.50f), 4f, StrokeCap.Round)
        drawLine(c, Offset(size.width*.5f,size.height*.40f), Offset(size.width*.72f,size.height*.50f), 4f, StrokeCap.Round)
        drawLine(c, Offset(size.width*.5f,size.height*.58f), Offset(size.width*.33f,size.height*.84f), 4f, StrokeCap.Round)
        drawLine(c, Offset(size.width*.5f,size.height*.58f), Offset(size.width*.67f,size.height*.84f), 4f, StrokeCap.Round)
    }
}

@Composable
private fun ExerciseListScreen(onExercise: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Header("Cviky", "Jasná technika, žádné hádání")
        exercises.forEachIndexed { index, exercise -> ExerciseCard(exercise) { onExercise(index) } }
    }
}

@Composable
private fun ProgressScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Header("Přehled", "Tvůj týden na jednom místě")
        Card(
            Modifier.fillMaxWidth().padding(22.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF171B23))
        ) {
            Column(Modifier.padding(24.dp)) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(42.dp))
                Spacer(Modifier.height(12.dp))
                Text("1 trénink dokončen", fontSize = 26.sp, fontWeight = FontWeight.Black)
                Text("20 minut • 12 sérií", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 17.sp)
                Spacer(Modifier.height(18.dp))
                Text("Týdenní cíl", fontWeight = FontWeight.Bold)
                Text("1 z 3 tréninků", color = MaterialTheme.colorScheme.secondary, fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun ProfileScreen(onSettings: () -> Unit, onPremium: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Header("Profil", "Vše důležité konečně na jednom místě") {
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Nastavení", tint = MaterialTheme.colorScheme.onBackground)
            }
        }

        Card(
            Modifier.fillMaxWidth().padding(horizontal = 22.dp).clickable(onClick = onPremium),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF24202A))
        ) {
            Row(Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(52.dp).background(MaterialTheme.colorScheme.secondary, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF21160A))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("ForgeFit Premium", fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text("Plány, historie, pokročilé statistiky", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        Spacer(Modifier.height(14.dp))
        SettingRow(Icons.Default.Settings, "Nastavení", "Vzhled, jednotky, připomínky", onSettings)
        SettingRow(Icons.Default.FitnessCenter, "Tréninkové preference", "Délka, obtížnost, odpočinek") {}
        SettingRow(Icons.Default.Person, "Osobní údaje", "Cíle a vstupní údaje") {}
    }
}

@Composable
private fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 22.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsScreen(onBack: () -> Unit, onPremium: () -> Unit) {
    var notifications by rememberSaveable { mutableStateOf(true) }
    var darkMode by rememberSaveable { mutableStateOf(true) }

    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(bottom = 28.dp)) {
        TopBackBar("Nastavení", onBack)

        Text("PREMIUM", modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
        Card(
            Modifier.fillMaxWidth().padding(horizontal = 22.dp).clickable(onClick = onPremium),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF211E27))
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(34.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("ForgeFit Premium", fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("Správa Premium a výhod", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        Text("APLIKACE", modifier = Modifier.padding(start = 22.dp, top = 28.dp, bottom = 8.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
        ToggleRow("Tmavý vzhled", "Výchozí vzhled ForgeFit", darkMode) { darkMode = it }
        ToggleRow("Připomínky", "Upozornění na naplánovaný trénink", notifications) { notifications = it }
        PlainSetting("Jednotky", "Metrické • kg • cm")
        PlainSetting("Délka pauzy", "60 sekund")

        Text("ÚČET", modifier = Modifier.padding(start = 22.dp, top = 28.dp, bottom = 8.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
        PlainSetting("Osobní údaje", "Upravit cíle a vstupní údaje")
        PlainSetting("Soukromí a data", "Export a odstranění dat")
    }
}

@Composable
private fun PremiumScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
        TopBackBar("ForgeFit Premium", onBack)
        Card(
            Modifier.fillMaxWidth().padding(22.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF211E27))
        ) {
            Column(Modifier.padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(72.dp).background(MaterialTheme.colorScheme.secondary, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF21160A), modifier = Modifier.size(38.dp))
                }
                Spacer(Modifier.height(18.dp))
                Text("Premium bez schovávání", fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Text(
                    "Tahle sekce je dostupná z Profilu i z Nastavení. Už ji nemusíš hledat.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(Modifier.height(22.dp))
                PremiumBenefit("Pokročilé týdenní a měsíční statistiky")
                PremiumBenefit("Neomezené vlastní tréninkové plány")
                PremiumBenefit("Delší historie a porovnání výkonu")
                PremiumBenefit("Pokročilé cíle a doporučení")
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("NÁKUP PROPOJIT S GOOGLE PLAY", fontWeight = FontWeight.Bold)
                }
                Text(
                    "Tlačítko zůstává vypnuté, dokud nebude v Google Play Console nastavený produkt Premium.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumBenefit(text: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("✓", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Black, fontSize = 19.sp)
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 16.sp)
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, value: Boolean, onValue: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Switch(checked = value, onCheckedChange = onValue)
    }
}

@Composable
private fun PlainSetting(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TopBackBar(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
            Text("‹", fontSize = 42.sp, lineHeight = 42.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Text(title, fontSize = 27.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun WorkoutScreen(exercise: Exercise, index: Int, total: Int, onClose: () -> Unit) {
    var paused by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Zavřít") }
            Text("${index + 1}/$total", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.size(48.dp))
        }

        Box(Modifier.fillMaxWidth().height(5.dp).background(Color(0xFF252A32), RoundedCornerShape(99.dp))) {
            Box(Modifier.fillMaxWidth((index + 1f) / total.toFloat()).height(5.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(99.dp)))
        }

        Text(exercise.name, fontSize = 31.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 24.dp))
        Text(exercise.area, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)

        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151A22))
        ) {
            Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("UKÁZKA POHYBU", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                PikePushupAnimation(modifier = Modifier.fillMaxWidth().height(260.dp), animate = !paused)
                Text(exercise.reps.uppercase(), fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text(exercise.cue, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 7.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF202630))
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (paused) "POZASTAVENO" else "PAUZA", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp)
                Text("60 s", color = MaterialTheme.colorScheme.secondary, fontSize = 42.sp, fontWeight = FontWeight.Black)
                Button(
                    onClick = { paused = !paused },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(if (paused) "POKRAČOVAT" else "POZASTAVIT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PikePushupAnimation(modifier: Modifier = Modifier, animate: Boolean = true) {
    val transition = rememberInfiniteTransition(label = "exerciseMotion")
    val raw by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1300), repeatMode = RepeatMode.Reverse),
        label = "phase"
    )
    val phase = if (animate) raw else 0f

    Canvas(modifier) {
        val body = Color(0xFFF4F0F5)
        val accent = Color(0xFFFFB248)
        val guide = Color(0xFF49505D)
        val stroke = size.minDimension * .028f

        val floorY = size.height * .84f
        drawLine(guide, Offset(size.width*.10f, floorY), Offset(size.width*.90f, floorY), strokeWidth = 3f)

        val handL = Offset(size.width*.27f, floorY)
        val handR = Offset(size.width*.40f, floorY)
        val foot = Offset(size.width*.78f, floorY)
        val hip = Offset(size.width*.58f, size.height*.29f)

        val shoulder = Offset(size.width*.38f, size.height*(.48f + .12f*phase))
        val head = Offset(size.width*.31f, size.height*(.37f + .14f*phase))

        drawLine(body, hip, shoulder, stroke, StrokeCap.Round)
        drawLine(body, hip, foot, stroke, StrokeCap.Round)
        drawLine(body, shoulder, handL, stroke, StrokeCap.Round)
        drawLine(body, shoulder, handR, stroke, StrokeCap.Round)

        drawCircle(accent, radius = size.minDimension*.075f, center = head)
        drawCircle(body, radius = size.minDimension*.032f, center = hip, style = Stroke(width = stroke*.55f))
        drawCircle(body, radius = size.minDimension*.025f, center = shoulder, style = Stroke(width = stroke*.5f))

        val arrowX = size.width*.15f
        val top = size.height*.36f
        val bottom = size.height*.62f
        drawLine(accent, Offset(arrowX, top), Offset(arrowX, bottom), strokeWidth = 5f, cap = StrokeCap.Round)
        drawLine(accent, Offset(arrowX, bottom), Offset(arrowX-12f, bottom-14f), strokeWidth = 5f, cap = StrokeCap.Round)
        drawLine(accent, Offset(arrowX, bottom), Offset(arrowX+12f, bottom-14f), strokeWidth = 5f, cap = StrokeCap.Round)
    }
}
