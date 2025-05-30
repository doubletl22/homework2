package com.example.homework2.ui.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.homework2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    onNavigateToVerification: (email: String) -> Unit = {} // Callback for navigation
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) } // For email validation error message
    var isLoading by remember { mutableStateOf(false) } // For loading state

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current // For potential context-based operations (e.g. string resources)

    // Function to validate email
    fun validateEmail(input: String): String? {
        if (input.isBlank()) {
            return "Email cannot be empty."
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            return "Please enter a valid email address."
        }
        return null // No error
    }

    // Function to handle the "Next" button click
    fun handleNextClick() {
        focusManager.clearFocus() // Dismiss keyboard
        val error = validateEmail(email)
        emailError = error

        if (error == null) {
            isLoading = true
            // Simulate network call or processing
            coroutineScope.launch {
                kotlinx.coroutines.delay(2000) // Simulate delay
                isLoading = false
                // In a real app, you would call your ViewModel here to handle the logic
                // e.g., viewModel.requestPasswordReset(email)

                // Show success message
                snackbarHostState.showSnackbar(
                    message = "Verification code sent to $email",
                    duration = SnackbarDuration.Short
                )
                // Navigate to the next screen (e.g., verification code input)
                onNavigateToVerification(email)
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                // Replace with your actual logo resource. Using a common placeholder.
                painter = painterResource(id = R.drawable.uth),
                // painter = painterResource(id = R.drawable.uth_logo),
                contentDescription = "UTH Logo",
                modifier = Modifier
                    .height(80.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "SmartTasks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = "Forget Password?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Enter your Email, we will send you a verification code.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) { // Clear error when user starts typing
                        emailError = null
                    }
                },
                label = { Text("Your Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Reduced bottom padding
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done // Change to Done to trigger action or use Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = { handleNextClick() }
                ),
                shape = RoundedCornerShape(12.dp),
                isError = emailError != null, // Show error state
            )

            // Display error message below the TextField
            if (emailError != null) {
                Text(
                    text = emailError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 8.dp)
                        .fillMaxWidth()
                )
            } else {
                // Add a spacer to maintain layout consistency when no error
                Spacer(modifier = Modifier.height(MaterialTheme.typography.bodySmall.lineHeight.value.dp + 8.dp))

            }


            Button(
                onClick = { handleNextClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isLoading // Disable button when loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White, // Or MaterialTheme.colorScheme.onPrimary
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Next",
                        fontSize = 18.sp,
                        color = Color.White // Or MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgetPasswordScreenPreviewWithLogic() {
    MaterialTheme {
        ForgetPasswordScreen()
    }
}