package com.haze.servicesprovider

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.presentation.navigation.HazeNavGraph
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ErrorRed
import com.haze.servicesprovider.presentation.theme.HazeServiceProviderTheme
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HazeServiceProviderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundLight
                ) {
                    var showDebugDialog by remember { mutableStateOf(false) }
                    val navController = rememberNavController()

                    Box(modifier = Modifier.fillMaxSize()) {
                        HazeNavGraph(navController = navController)

                        // Floating 🐞 Debug Button in top-right
                        FloatingActionButton(
                            onClick = { showDebugDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 44.dp, end = 16.dp)
                                .size(40.dp),
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color(0xFF38BDF8),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "Debug Diagnostics",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (showDebugDialog) {
                            DebugDiagnosticsDialog(
                                onDismiss = { showDebugDialog = false },
                                onClearSession = {
                                    try {
                                        ServiceLocator.sessionManager.clearSession()
                                        getSharedPreferences("haze_debug_store", Context.MODE_PRIVATE).edit().clear().commit()
                                        val intent = Intent(this@MainActivity, MainActivity::class.java).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        }
                                        startActivity(intent)
                                        finish()
                                    } catch (_: Exception) {}
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugDiagnosticsDialog(
    onDismiss: () -> Unit,
    onClearSession: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = ServiceLocator.sessionManager
    val isCompleted = sessionManager.isProfileCompleted()
    val userId = sessionManager.getUserId() ?: "Not logged in"
    val phone = sessionManager.getPhoneNumber() ?: "None"
    val profile = sessionManager.cachedProfile.value

    val prefs = context.getSharedPreferences("haze_debug_store", Context.MODE_PRIVATE)
    val lastCrash = prefs.getString("last_crash", null)

    val diagnosticsReport = buildString {
        appendLine("=== HAZE APP DIAGNOSTICS ===")
        appendLine("App Version: 1.0.2 (VersionCode: 3)")
        appendLine("Profile Completed: $isCompleted")
        appendLine("User ID: $userId")
        appendLine("Phone Number: $phone")
        appendLine("Cached Name: ${profile?.fullName ?: "None"}")
        appendLine("Cached City: ${profile?.city ?: "None"}")
        appendLine("Cached Category: ${profile?.displayCategory ?: "None"}")
        appendLine("Last Crash Log:")
        appendLine(lastCrash ?: "No recorded crashes.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = ProfessionalBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "App Diagnostics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkNavyText
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "App State & Diagnostics Log:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = DarkNavyText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = diagnosticsReport,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Haze Diagnostics", diagnosticsReport)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfessionalBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy All Diagnostics")
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedButton(
                    onClick = onClearSession,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear App Session & Re-Login", color = ErrorRed)
                }
            }
        },
        confirmButton = {},
        containerColor = PureWhite
    )
}
