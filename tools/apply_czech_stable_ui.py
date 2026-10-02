from pathlib import Path
import re

main_path = Path('app/src/main/java/com/xgamerstore/forgefit/MainActivity.kt')
visual_path = Path('app/src/main/java/com/xgamerstore/forgefit/ExercisePresentation.kt')
main = main_path.read_text(encoding='utf-8')
visual = visual_path.read_text(encoding='utf-8')

# Local/offline exercise names and remaining English body labels.
replacements = {
    'Exercise("Plank","Core • ramena"': 'Exercise("Prkno","Střed těla • ramena"',
    'Exercise("Jumping jack","Kondice • celé tělo"': 'Exercise("Panák","Kondice • celé tělo"',
    'Exercise("Glute bridge","Hýždě • zadní stehna"': 'Exercise("Most na hýždě","Hýždě • zadní stehna"',
    'Exercise("Mountain climber","Core • kondice"': 'Exercise("Horolezec","Střed těla • kondice"',
    'Exercise("Wall sit","Nohy • kvadricepsy"': 'Exercise("Sed u zdi","Nohy • kvadricepsy"',
    'Exercise("Pike push-up","Ramena • triceps"': 'Exercise("Klik ve střeše","Ramena • triceps"',
    'Exercise("Superman","Záda • hýždě"': 'Exercise("Zvedání paží a nohou vleže","Záda • hýždě"',
    'Exercise("Bird dog","Core • záda"': 'Exercise("Vzpažení a zanožení na čtyřech","Střed těla • záda"',
    'Exercise("Dead bug","Core"': 'Exercise("Mrtvý brouk","Střed těla"',
    'Exercise("Side plank","Šikmé břišní svaly • ramena"': 'Exercise("Boční prkno","Šikmé břišní svaly • ramena"',
    'Exercise("Russian twist","Břicho • šikmé břišní svaly"': 'Exercise("Ruské otáčení","Břicho • šikmé břišní svaly"',
    'Exercise("High knees","Kondice • core"': 'Exercise("Běh s vysokými koleny","Kondice • střed těla"',
    'Exercise("Burpee","Celé tělo • kondice"': 'Exercise("Angličák","Celé tělo • kondice"',
    'Exercise("Bear crawl","Core • ramena • celé tělo"': 'Exercise("Medvědí chůze","Střed těla • ramena • celé tělo"',
    'Exercise("Donkey kick","Hýždě"': 'Exercise("Zanožování na čtyřech","Hýždě"',
    'Exercise("Fire hydrant","Hýždě • boky"': 'Exercise("Unožování na čtyřech","Hýždě • boky"',
    'Exercise("Hip hinge","Zadní stehna • hýždě • záda"': 'Exercise("Předklon v kyčlích","Zadní stehna • hýždě • záda"',
    'Exercise("Reverse crunch","Spodní část břicha"': 'Exercise("Obrácené zkracovačky","Spodní část břicha"',
    'Exercise("Plank shoulder tap","Core • ramena"': 'Exercise("Dotyky ramen v prkně","Střed těla • ramena"',
    'Exercise("Skater","Kondice • nohy"': 'Exercise("Bruslařské přeskoky","Kondice • nohy"',
    '"Hrudník • triceps • core"': '"Hrudník • triceps • střed těla"',
    '"Svalová heatmapa plánu"': '"Svalové zatížení plánu"',
    '"Online katalog • "': '"Internetový katalog • "',
    '"Offline knihovna • "': '"Místní knihovna • "',
    '"Offline profil"': '"Místní profil"',
    '"Online knihovna"': '"Internetová knihovna"',
    '"PŘIPOJIT GOOGLE PLAY BILLING"': '"PŘIPOJIT PLATBY GOOGLE PLAY"',
}
for old, new in replacements.items():
    main = main.replace(old, new)

# Preserve focus filtering after replacing Core labels.
main = main.replace('e.muscle.contains("Břicho") || e.muscle.contains("Core")',
                    'e.muscle.contains("Břicho") || e.muscle.contains("Střed těla")')

new_workout = r'''@Composable
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
'''

pattern = re.compile(r'@Composable\nfun WorkoutScreen\(profile:UserProfile,store:FitStore,close:\(\)->Unit\) \{.*?\n\}\n\n@Composable\nfun Stats', re.S)
if not pattern.search(main):
    raise SystemExit('WorkoutScreen block not found')
main = pattern.sub(new_workout + '\n@Composable\nfun Stats', main, count=1)

# Update the visual classifier for the new Czech names so the correct animation remains.
visual_repls = {
    '"plank" in name || "prkno" in name || "mountain" in name || "horolezec" in name || "shoulder tap" in name':
        '"plank" in name || "prkno" in name || "mountain" in name || "horolezec" in name || "shoulder tap" in name || "dotyky ramen" in name',
    '"side plank" in name': '"side plank" in name || "boční prkno" in name',
    '"dřep" in name || "squat" in name || "wall sit" in name': '"dřep" in name || "squat" in name || "wall sit" in name || "sed u zdi" in name',
    'if ("wall sit" in name)': 'if ("wall sit" in name || "sed u zdi" in name)',
    '"bridge" in name || "most" in name': '"bridge" in name || "most" in name',
    '"crunch" in name || "zkrac" in name || "dead bug" in name || "russian" in name': '"crunch" in name || "zkrac" in name || "dead bug" in name || "mrtvý brouk" in name || "russian" in name || "ruské otáčení" in name',
    '"superman" in name': '"superman" in name || "zvedání paží a nohou" in name',
    '"bird dog" in name || "bear crawl" in name || "donkey" in name || "hydrant" in name': '"bird dog" in name || "vzpažení a zanožení" in name || "bear crawl" in name || "medvědí chůze" in name || "donkey" in name || "zanožování" in name || "hydrant" in name || "unožování" in name',
    '"jump" in name || "high knees" in name || "skater" in name || "burpee" in name': '"jump" in name || "panák" in name || "high knees" in name || "vysokými koleny" in name || "skater" in name || "bruslařské" in name || "burpee" in name || "angličák" in name',
    '"hinge" in name': '"hinge" in name || "předklon v kyčlích" in name',
}
for old, new in visual_repls.items():
    visual = visual.replace(old, new)

# More Czech names in the internet catalog. This is deliberately broad so most catalog titles
# do not leak obvious English words into the Czech UI.
visual = visual.replace('"Advanced Kettlebell Windmill" to "Pokročilý kettlebell windmill",',
                        '"Advanced Kettlebell Windmill" to "Pokročilý větrný mlýn s kettlebellem",')
visual = visual.replace('"Burpee" to "Burpee",', '"Burpee" to "Angličák",')
visual = visual.replace('"High Knees" to "Vysoká kolena"', '"High Knees" to "Běh s vysokými koleny"')

main_path.write_text(main, encoding='utf-8')
visual_path.write_text(visual, encoding='utf-8')
print('Czech localization and stable workout layout applied.')
