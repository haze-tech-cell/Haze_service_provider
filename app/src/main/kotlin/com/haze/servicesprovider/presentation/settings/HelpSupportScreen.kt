package com.haze.servicesprovider.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.DividerColor
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun HelpSupportScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Help & Support",
                onBackClick = onNavigateBack
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Contact Partner Support",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Our provider helpdesk is available 7 days a week from 9 AM to 8 PM to assist with listings, lead issues, and technical support.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SupportContactRow(
                        icon = Icons.Default.Call,
                        title = "Partner Helpline",
                        subtitle = "+91 1800 123 4567 (Toll Free)",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18001234567"))
                            context.startActivity(intent)
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    SupportContactRow(
                        icon = Icons.Default.Email,
                        title = "Email Support",
                        subtitle = "partners@hazeservices.com",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:partners@hazeservices.com"))
                            context.startActivity(intent)
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    SupportContactRow(
                        icon = Icons.Default.Language,
                        title = "Partner Help Center",
                        subtitle = "https://hazeservices.com/partners/help",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hazeservices.com"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            FaqCard(
                question = "How do I receive leads from customers?",
                answer = "Once your business registration is complete, your profile is listed in your city for your selected service category. Whenever a customer in your area requests a service, it appears instantly in your Leads tab."
            )

            Spacer(modifier = Modifier.height(10.dp))

            FaqCard(
                question = "Can I change my service category later?",
                answer = "Yes! You can update your service category, city, or workshop address at any time from Settings > Edit Business Details."
            )

            Spacer(modifier = Modifier.height(10.dp))

            FaqCard(
                question = "Are there any charges for registering?",
                answer = "Registration on Haze Services Provider is currently free for all qualified service professionals. Premium subscription and verified partner badges will be available soon."
            )
        }
    }
}

@Composable
private fun SupportContactRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ProfessionalBlueLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ProfessionalBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavyText
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun FaqCard(
    question: String,
    answer: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SecondaryText,
                    lineHeight = 20.sp
                )
            )
        }
    }
}
