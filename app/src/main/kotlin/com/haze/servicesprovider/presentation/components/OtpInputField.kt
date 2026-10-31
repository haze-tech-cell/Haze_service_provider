package com.haze.servicesprovider.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ErrorRed
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.PureWhite

@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    isError: Boolean = false,
    onComplete: (() -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Invisible input field catching keyboard and paste events
        BasicTextField(
            value = otpValue,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }.take(otpLength)
                onOtpChange(filtered)
                if (filtered.length == otpLength) {
                    onComplete?.invoke()
                }
            },
            modifier = Modifier
                .matchParentSize()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (otpValue.length == otpLength) {
                        onComplete?.invoke()
                    }
                }
            ),
            decorationBox = { /* Hidden, custom boxes rendered below */ }
        )

        // 6 Individual Boxes UI
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until otpLength) {
                val digit = otpValue.getOrNull(i)?.toString() ?: ""
                val isFocused = otpValue.length == i

                val borderColor = when {
                    isError -> ErrorRed
                    isFocused -> ProfessionalBlue
                    digit.isNotEmpty() -> ProfessionalBlue.copy(alpha = 0.5f)
                    else -> CardBorder
                }

                Box(
                    modifier = Modifier
                        .width(46.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isFocused || isError) 2.dp else 1.2.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { focusRequester.requestFocus() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = DarkNavyText,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}
