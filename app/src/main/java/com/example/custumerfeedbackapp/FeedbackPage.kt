package com.example.custumerfeedbackapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase

@Composable
fun FeedbackPage(navController: NavController) {
    var feedbackText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().reference.child("feedback")

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            message = ""
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Feedback", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                label = { Text("Your feedback") },
                placeholder = { Text("Write your feedback here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (feedbackText.isNotBlank()) {
                        val feedbackId = database.push().key ?: ""
                        val feedbackData = mapOf(
                            "feedback" to feedbackText,
                            "date" to System.currentTimeMillis().toString()
                        )
                        database.child(feedbackId).setValue(feedbackData)
                            .addOnSuccessListener {
                                message = "Feedback submitted!"
                                feedbackText = ""
                            }
                            .addOnFailureListener {
                                message = "Failed to submit: ${it.message}"
                            }
                    } else {
                        message = "Please enter feedback"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Submit")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    // navigate to admin dashboard (you might want to protect this in real app)
                    navController.navigate("admin")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Admin Dashboard")
            }
        }
    }
}
