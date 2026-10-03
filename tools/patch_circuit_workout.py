from pathlib import Path
import re

p = Path('app/src/main/java/com/xgamerstore/forgefit/MainActivity.kt')
s = p.read_text()

s = s.replace('SmallStat(stats[0].toString()+"/"+profile.days,"tréninky",Modifier.weight(1f))',
              'SmallStat(minOf(stats[0],profile.days).toString()+"/"+profile.days,"tréninky",Modifier.weight(1f))')
s = s.replace('SmallStat(s[0].toString()+"/"+profile.days,"tréninky",Modifier.weight(1f))',
              'SmallStat(minOf(s[0],profile.days).toString()+"/"+profile.days,"tréninky",Modifier.weight(1f))')

new_workout = '''@Composable
fun WorkoutScreen(profile:UserProfile,store:FitStore,close:()->Unit) {
    val plan=remember(profile){workoutFor(profile)}
    val sequence=remember(plan){
        buildList {
            val rounds=plan.maxOfOrNull { it.sets } ?: 1
            for(round in 1..rounds) {
                plan.forEachIndexed { index,exercise ->
                    if(round<=exercise.sets) add(index to round)
                }
            }
        }
    }
    var stepIndex by remember{mutableIntStateOf(0)}
    var rest by remember{mutableIntStateOf(0)}
    var totalSets by remember{mutableIntStateOf(0)}
    var totalReps by remember{mutableIntStateOf(0)}
    var done by remember{mutableStateOf(false)}
    var rpe by remember{mutableIntStateOf(8)}
    val current=sequence[stepIndex.coerceAtMost(sequence.lastIndex)]
    val i=current.first
    val round=current.second
    val e=plan[i]
    val maxRounds=plan.maxOfOrNull { it.sets } ?: 1

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
            },Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Orange,contentColor=Color(0xFF111111),disabledContainerColor=Panel2,disabledContentColor=Muted)) {
                Text("ULOŽIT TRÉNINK")
            }
        }
        return
    }

    Column(Modifier.fillMaxSize().background(Bg).padding(18.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            IconButton(close){Icon(Icons.Default.Close,null,tint=Color.White)}
            Text("Cvik "+(i+1)+"/"+plan.size,color=Muted)
            Spacer(Modifier.weight(1f))
            Text("Kolo "+round+"/"+maxRounds,color=Color.White)
        }
        LinearProgressIndicator(
            progress={ (stepIndex+1).toFloat()/sequence.size },
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

        Box(
            Modifier.fillMaxWidth().height(126.dp),
            contentAlignment=Alignment.Center
        ) {
            if(rest>0) {
                Surface(color=Panel2,contentColor=TextPrimary,tonalElevation=1.dp,shape=RoundedCornerShape(18.dp),modifier=Modifier.fillMaxSize()) {
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
                    if(stepIndex<sequence.lastIndex) {
                        stepIndex++
                        rest=e.rest
                    } else done=true
                },Modifier.fillMaxWidth().height(58.dp),colors=ButtonDefaults.buttonColors(containerColor=Orange,contentColor=Color(0xFF111111),disabledContainerColor=Panel2,disabledContentColor=Muted)) {
                    Icon(Icons.Default.Check,null)
                    Spacer(Modifier.width(6.dp))
                    Text("SÉRIE HOTOVÁ",fontWeight=FontWeight.Black)
                }
            }
        }
    }
}
'''

pattern = r'@Composable\nfun WorkoutScreen\(profile:UserProfile,store:FitStore,close:\(\)->Unit\) \{.*?\n\}\n\n@Composable\nfun Stats'
replaced, n = re.subn(pattern, new_workout + '\n@Composable\nfun Stats', s, count=1, flags=re.S)
if n != 1:
    raise SystemExit('WorkoutScreen block not found')
p.write_text(replaced)
