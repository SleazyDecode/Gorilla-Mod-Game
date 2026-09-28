package wtf.pearl.client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class PearlTheme { RED, BLUE, PURPLE, BLACK }
data class ThemeColors(val accent: Color, val panel: Color, val bg: Color)
fun colorsFor(t: PearlTheme) = when (t) {
    PearlTheme.RED -> ThemeColors(Color(0xFFFF4655), Color(0xFF17191E), Color(0xFF07080A))
    PearlTheme.BLUE -> ThemeColors(Color(0xFF5B8CFF), Color(0xFF171A21), Color(0xFF07080A))
    PearlTheme.PURPLE -> ThemeColors(Color(0xFFA970FF), Color(0xFF19171F), Color(0xFF07080A))
    PearlTheme.BLACK -> ThemeColors(Color(0xFFE5E7EB), Color(0xFF151515), Color(0xFF050505))
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PearlApp() }
    }
}
@Composable
fun PearlApp() {
    var theme by remember { mutableStateOf(PearlTheme.BLACK) }
    var tab by remember { mutableStateOf("Client") }
    val c = colorsFor(theme)
    MaterialTheme(colorScheme = darkColorScheme(primary = c.accent, surface = c.panel, background = c.bg)) {
        Surface(color = c.bg, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Header(tab, { tab = it }, c)
                Spacer(Modifier.height(12.dp))
                when (tab) {
                    "Client" -> ClientScreen(c)
                    "Terminal" -> TerminalScreen(c)
                    "Settings" -> SettingsScreen(theme, { theme = it }, c)
                }
            }
        }
    }
}
@Composable
fun Header(tab: String, onTab: (String) -> Unit, c: ThemeColors) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Pearl.wtf", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        listOf("Client", "Terminal", "Settings").forEach { name ->
            Text(name, modifier = Modifier.clickable { onTab(name) }.padding(horizontal = 10.dp, vertical = 8.dp),
                color = if (tab == name) c.accent else Color.LightGray,
                fontWeight = if (tab == name) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
@Composable
fun ClientScreen(c: ThemeColors) {
    var paired by remember { mutableStateOf(false) }
    var attached by remember { mutableStateOf(false) }
    var ip by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = c.panel)) {
                Column(Modifier.padding(16.dp)) {
                    Text("SETUP WIRELESS ADB", color = c.accent, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Connect the headset and workstation to the same Wi-Fi. On Android 11+ use Developer Options → Wireless debugging → Pair using pairing code.")
                    Spacer(Modifier.height(8.dp))
                    Text("Official ADB pairing uses an IP address, pairing port and pairing code.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = c.panel)) {
                Column(Modifier.padding(16.dp)) {
                    Text("PAIRING", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(ip, { ip = it }, label = { Text("IP") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(port, { port = it }, label = { Text("Pair port") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(code, { code = it }, label = { Text("Pairing code") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            val command = "adb pair " + ip.ifBlank { "IP" } + ":" + port.ifBlank { "PORT" }
                            val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("ADB pair", command))
                            Toast.makeText(ctx, "ADB pair command copied", Toast.LENGTH_SHORT).show()
                        }) { Text("COPY ADB PAIR") }
                        OutlinedButton(onClick = { paired = ip.isNotBlank() && port.isNotBlank() && code.length >= 4 }) { Text("MARK PAIRED") }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(if (paired) "● Paired / ready" else "○ Waiting for pairing",
                        color = if (paired) Color(0xFF75E6A5) else Color.Gray)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = c.panel)) {
                Column(Modifier.padding(16.dp)) {
                    Text("ATTACH", color = c.accent, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    StatusLine("Gorilla Tag process", paired)
                    StatusLine("Locomotion", false)
                    StatusLine("Networking", false)
                    Spacer(Modifier.height(8.dp))
                    Button(enabled = paired, onClick = { attached = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (attached) "ATTACHED" else "ATTACH")
                    }
                    if (!paired) Text("Pair the headset first.", color = Color.Gray, fontSize = 12.sp)
                    if (attached) Text("Client attached. Game-specific injection is intentionally not included in this starter.",
                        color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
@Composable
fun StatusLine(name: String, ok: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(if (ok) "●" else "○", color = if (ok) Color(0xFF75E6A5) else Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text(name)
    }
}
@Composable
fun TerminalScreen(c: ThemeColors) {
    val lines = listOf("> Pearl.wtf terminal", "> client initialized", "> waiting for wireless ADB pairing", "> no game process attached", "> ready")
    Card(colors = CardDefaults.cardColors(containerColor = Color.Black), modifier = Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            Text("TERMINAL", color = c.accent, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            lines.forEach { Text(it, color = Color(0xFFB9FFCB), fontSize = 13.sp) }
        }
    }
}
@Composable
fun SettingsScreen(theme: PearlTheme, setTheme: (PearlTheme) -> Unit, c: ThemeColors) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("SETTINGS", color = c.accent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Card(colors = CardDefaults.cardColors(containerColor = c.panel)) {
            Column(Modifier.padding(16.dp)) {
                Text("THEME", fontWeight = FontWeight.Bold)
                PearlTheme.entries.forEach { t ->
                    Row(Modifier.fillMaxWidth().clickable { setTheme(t) }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = theme == t, onClick = { setTheme(t) })
                        Text(t.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = c.panel)) {
            Column(Modifier.padding(16.dp)) {
                Text("VR MENU", fontWeight = FontWeight.Bold)
                Text("X button → toggle menu", color = Color.Gray)
                Text("Follow player sphere → optional", color = Color.Gray)
                Text("Long Arms → mod-side feature", color = Color.Gray)
            }
        }
    }
}
