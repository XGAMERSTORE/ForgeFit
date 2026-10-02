from pathlib import Path

main = Path('app/src/main/java/com/xgamerstore/forgefit/MainActivity.kt')
visual = Path('app/src/main/java/com/xgamerstore/forgefit/ExercisePresentation.kt')
s = main.read_text(encoding='utf-8')

old_colors = '''private val Bg = Color(0xFF0B0D10)
private val Panel = Color(0xFF15191F)
private val Panel2 = Color(0xFF20262E)
private val Orange = Color(0xFFFF5A36)
private val Gold = Color(0xFFFFB347)
private val Muted = Color(0xFF9AA4AF)'''
new_colors = '''private val Bg = Color(0xFF090B0F)
private val Panel = Color(0xFF131820)
private val Panel2 = Color(0xFF1D2530)
private val Orange = Color(0xFFFF6B3D)
private val Gold = Color(0xFFFFC15A)
private val TextPrimary = Color(0xFFF7F8FA)
private val Muted = Color(0xFFADB7C4)
private val Outline = Color(0xFF3A4553)'''
if old_colors in s:
    s = s.replace(old_colors, new_colors)

old_theme = '''MaterialTheme(colorScheme=darkColorScheme(primary=Orange,onPrimary=Color.Black,secondary=Gold,onSecondary=Color.Black,background=Bg,onBackground=Color(0xFFF4F1F7),surface=Panel,onSurface=Color(0xFFF4F1F7),surfaceVariant=Panel2,onSurfaceVariant=Muted)) {
                val store = remember { FitStore(this) }
                App(store)
            }'''
new_theme = '''MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Orange,
                    onPrimary = Color(0xFF111111),
                    primaryContainer = Color(0xFF4A2118),
                    onPrimaryContainer = TextPrimary,
                    secondary = Gold,
                    onSecondary = Color(0xFF15120B),
                    secondaryContainer = Color(0xFF3A2D16),
                    onSecondaryContainer = TextPrimary,
                    tertiary = Color(0xFF8ED1C5),
                    onTertiary = Color(0xFF0B1715),
                    background = Bg,
                    onBackground = TextPrimary,
                    surface = Panel,
                    onSurface = TextPrimary,
                    surfaceVariant = Panel2,
                    onSurfaceVariant = Muted,
                    error = Color(0xFFFF6B6B),
                    onError = Color(0xFF1B0909),
                    outline = Outline
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Bg,
                    contentColor = TextPrimary
                ) {
                    val store = remember { FitStore(this) }
                    App(store)
                }
            }'''
if old_theme not in s and 'contentColor = TextPrimary' not in s:
    raise SystemExit('Theme block not found; refusing unsafe patch')
if old_theme in s:
    s = s.replace(old_theme, new_theme)

# Make custom dark surfaces always carry readable foreground colors.
s = s.replace('Surface(color=Panel,shape=', 'Surface(color=Panel,contentColor=TextPrimary,tonalElevation=2.dp,shadowElevation=1.dp,shape=')
s = s.replace('Surface(color=Panel2,shape=', 'Surface(color=Panel2,contentColor=TextPrimary,tonalElevation=1.dp,shape=')
s = s.replace('Surface(color=Bg,shape=', 'Surface(color=Bg,contentColor=TextPrimary,shape=')
s = s.replace('Surface(color=Color(0xFF251F2A),shape=', 'Surface(color=Color(0xFF251F2A),contentColor=TextPrimary,tonalElevation=2.dp,shape=')

# Explicit foregrounds for buttons/icons that sit on custom colors.
s = s.replace('ButtonDefaults.buttonColors(containerColor=Orange)', 'ButtonDefaults.buttonColors(containerColor=Orange,contentColor=Color(0xFF111111),disabledContainerColor=Panel2,disabledContentColor=Muted)')
s = s.replace('IconButtonDefaults.filledIconButtonColors(containerColor=Panel2)', 'IconButtonDefaults.filledIconButtonColors(containerColor=Panel2,contentColor=TextPrimary)')
s = s.replace('IconButtonDefaults.filledIconButtonColors(containerColor=Orange)', 'IconButtonDefaults.filledIconButtonColors(containerColor=Orange,contentColor=Color(0xFF111111))')

# Ensure the two search/name inputs never inherit a dark text color from a parent.
s = s.replace('OutlinedTextField(u.name,{u=u.copy(name=it)},label={Text("Jméno / přezdívka")},modifier=Modifier.fillMaxWidth())',
'''OutlinedTextField(
                        u.name,
                        {u=u.copy(name=it)},
                        label={Text("Jméno / přezdívka")},
                        modifier=Modifier.fillMaxWidth(),
                        textStyle=LocalTextStyle.current.copy(color=TextPrimary),
                        colors=OutlinedTextFieldDefaults.colors(
                            focusedTextColor=TextPrimary,
                            unfocusedTextColor=TextPrimary,
                            cursorColor=Orange,
                            focusedBorderColor=Orange,
                            unfocusedBorderColor=Outline,
                            focusedLabelColor=Orange,
                            unfocusedLabelColor=Muted
                        )
                    )''')

search_anchor = '''OutlinedTextField(
                value=query,onValueChange={query=it},
                modifier=Modifier.fillMaxWidth(),
                singleLine=true,
                leadingIcon={Icon(Icons.Default.Search,null)},
                label={Text("Hledat cvik, sval nebo vybavení")}'''
search_repl = '''OutlinedTextField(
                value=query,onValueChange={query=it},
                modifier=Modifier.fillMaxWidth(),
                singleLine=true,
                textStyle=LocalTextStyle.current.copy(color=TextPrimary),
                colors=OutlinedTextFieldDefaults.colors(
                    focusedTextColor=TextPrimary,
                    unfocusedTextColor=TextPrimary,
                    cursorColor=Orange,
                    focusedBorderColor=Orange,
                    unfocusedBorderColor=Outline,
                    focusedLabelColor=Orange,
                    unfocusedLabelColor=Muted,
                    focusedLeadingIconColor=Orange,
                    unfocusedLeadingIconColor=Muted
                ),
                leadingIcon={Icon(Icons.Default.Search,null)},
                label={Text("Hledat cvik, sval nebo vybavení")}'''
if search_anchor in s:
    s = s.replace(search_anchor, search_repl)

main.write_text(s, encoding='utf-8')

v = visual.read_text(encoding='utf-8')
v = v.replace('private val VisualBg = Color(0xFF20262E)', 'private val VisualBg = Color(0xFF18202A)')
v = v.replace('private val VisualBody = Color(0xFFF4F1F7)', 'private val VisualBody = Color(0xFFF7F8FA)')
v = v.replace('private val VisualAccent = Color(0xFFFFB347)', 'private val VisualAccent = Color(0xFFFFC15A)')
v = v.replace('private val VisualFloor = Color(0xFF59636F)', 'private val VisualFloor = Color(0xFF6E7A88)')
visual.write_text(v, encoding='utf-8')

print('ForgeFit visual polish applied')
