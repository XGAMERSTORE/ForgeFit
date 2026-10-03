from pathlib import Path
import re

p = Path('app/src/main/java/com/xgamerstore/forgefit/MainActivity.kt')
s = p.read_text()

# Replace the static workout generator with a deterministic adaptive/rotating one.
pattern = r'fun workoutFor\(profile: UserProfile\): List<Exercise> \{.*?\n\}'
new = r'''fun exerciseGroup(e: Exercise): String = when {
    e.muscle.contains("Kondice", true) || e.muscle.contains("Celé tělo", true) -> "Kondice"
    e.muscle.contains("Nohy", true) || e.muscle.contains("Hýždě", true) || e.muscle.contains("Lýtka", true) -> "Spodek"
    e.muscle.contains("Hrudník", true) || e.muscle.contains("Triceps", true) || e.muscle.contains("Ramena", true) || e.muscle.contains("Paže", true) -> "Vršek"
    e.muscle.contains("Záda", true) -> "Záda"
    else -> "Střed"
}

fun workoutFor(
    profile: UserProfile,
    sessionIndex: Int = 0,
    weekSeed: Int = 0,
    avoidNames: Set<String> = emptySet(),
    averageRpe: Float = 0f
): List<Exercise> {
    val count = when {
        profile.minutes <= 20 -> 4
        profile.minutes <= 40 -> 6
        else -> 8
    }

    val allowed = exerciseLibrary.filter { e ->
        when(profile.experience) {
            "Začátečník" -> e.difficulty != "Pokročilý"
            "Mírně pokročilý" -> true
            else -> true
        }
    }

    fun focusMatch(e: Exercise): Boolean = profile.focus.any { focus ->
        when(focus) {
            "Celé tělo" -> true
            "Břicho" -> e.muscle.contains("Břicho", true) || e.muscle.contains("Střed těla", true)
            else -> e.muscle.contains(focus, true)
        }
    }

    val seed = weekSeed * 97 + sessionIndex * 31 + profile.goal.hashCode()
    fun variation(e: Exercise): Int = ((e.name.hashCode() xor seed) and 0x7fffffff) % 23

    fun score(e: Exercise): Int {
        var score = variation(e)
        if(focusMatch(e)) score += 45
        if(e.name in avoidNames) score -= 55

        when(profile.goal) {
            "Kondice", "Zhubnout" -> {
                if(e.muscle.contains("Kondice", true) || e.seconds > 0) score += 28
                if(exerciseGroup(e) == "Střed") score += 8
            }
            "Síla", "Nabrat svaly" -> {
                if(e.seconds == 0) score += 25
                if(exerciseGroup(e) == "Spodek" || exerciseGroup(e) == "Vršek" || exerciseGroup(e) == "Záda") score += 10
            }
            else -> if(exerciseGroup(e) == "Kondice") score += 8
        }

        // Jednoduchá autoregulace podle RPE: při dlouhodobě těžkých sériích
        // netlačíme pokročilé cviky nahoru, při lehkých je naopak lehce zvýhodníme.
        if(averageRpe >= 9f && e.difficulty == "Pokročilý") score -= 35
        if(averageRpe in 1f..7f && e.difficulty == "Pokročilý" && profile.experience == "Pokročilý") score += 10
        return score
    }

    val ranked = allowed.sortedWith(compareByDescending<Exercise> { score(it) }.thenBy { it.name })
    val result = mutableListOf<Exercise>()
    val groupCounts = mutableMapOf<String,Int>()

    // Nejdřív skládáme vyvážený trénink: nejvýše dva cviky ze stejné hlavní skupiny.
    for(e in ranked) {
        if(result.size >= count) break
        val g = exerciseGroup(e)
        if((groupCounts[g] ?: 0) < 2) {
            result += e
            groupCounts[g] = (groupCounts[g] ?: 0) + 1
        }
    }
    // U úzkého zaměření doplníme nejlepší zbývající cviky.
    for(e in ranked) {
        if(result.size >= count) break
        if(e !in result) result += e
    }
    return result
}'''
s, n = re.subn(pattern, new, s, count=1, flags=re.S)
if n != 1:
    raise SystemExit('workoutFor not found')

# Make workout completion remember the last exercise selection.
s = s.replace('fun addWorkout(minutes:Int, sets:Int, reps:Int) {', 'fun addWorkout(minutes:Int, sets:Int, reps:Int, plan:List<Exercise>) {', 1)
s = s.replace('.putInt("total",p.getInt("total",0)+1)\n            .apply()', '.putInt("total",p.getInt("total",0)+1)\n            .putStringSet("last_exercises",plan.map { it.name }.toSet())\n            .apply()', 1)

# Add a helper for recovery/novelty selection.
needle = '''    fun averageRpe(): Float {\n        val count = p.getInt("rpe_count",0)\n        return if(count==0) 0f else p.getInt("rpe_sum",0).toFloat()/count\n    }'''
replacement = needle + '''\n\n    fun lastExerciseNames(): Set<String> = p.getStringSet("last_exercises", emptySet()) ?: emptySet()'''
if needle not in s:
    raise SystemExit('averageRpe block not found')
s = s.replace(needle, replacement, 1)

# Home screen: select A/B/C/... from completed sessions and rotate weekly.
old = '    val stats=store.stats(); val plan=workoutFor(profile)'
new_home = '''    val stats=store.stats()\n    val sessionIndex=stats[0] % profile.days.coerceAtLeast(1)\n    val weekSeed=(LocalDate.now().toEpochDay()/7L).toInt()\n    val plan=workoutFor(profile,sessionIndex,weekSeed,store.lastExerciseNames(),store.averageRpe())\n    val planLetter=(\'A\'.code + sessionIndex.coerceIn(0,6)).toChar()'''
if old not in s:
    raise SystemExit('Home plan line not found')
s = s.replace(old, new_home, 1)
s = s.replace('Text("DNEŠNÍ FORGE",color=Orange,fontWeight=FontWeight.Black)', 'Text("DNEŠNÍ FORGE • TRÉNINK "+planLetter,color=Orange,fontWeight=FontWeight.Black)', 1)

# Workout screen uses exactly the same current session plan.
old = '    val plan=remember(profile){workoutFor(profile)}'
new_workout_plan = '''    val sessionIndex=store.stats()[0] % profile.days.coerceAtLeast(1)\n    val weekSeed=(LocalDate.now().toEpochDay()/7L).toInt()\n    val plan=remember(profile,sessionIndex,weekSeed){workoutFor(profile,sessionIndex,weekSeed,store.lastExerciseNames(),store.averageRpe())}'''
if old not in s:
    raise SystemExit('Workout plan line not found')
s = s.replace(old, new_workout_plan, 1)

# Save the actual plan to support next-session variation.
s = s.replace('store.addWorkout(profile.minutes,totalSets,totalReps)', 'store.addWorkout(profile.minutes,totalSets,totalReps,plan)', 1)

# Stats preview should follow the current session rather than a static plan.
s = s.replace('    val plan=workoutFor(profile)', '    val sessionIndex=s[0] % profile.days.coerceAtLeast(1)\n    val weekSeed=(LocalDate.now().toEpochDay()/7L).toInt()\n    val plan=workoutFor(profile,sessionIndex,weekSeed,store.lastExerciseNames(),store.averageRpe())', 1)

p.write_text(s)
