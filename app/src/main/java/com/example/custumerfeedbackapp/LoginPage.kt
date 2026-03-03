package com.example.custumerfeedbackapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
@Composable
fun LoginPage(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current

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
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        message = "Please enter email and password"
                        return@Button
                    }
                    val database = FirebaseDatabase.getInstance().reference.child("users")
                    database.get().addOnSuccessListener { data ->
                        var userFound = false
                        for (user in data.children) {
                            val userEmail = user.child("email").getValue(String::class.java)
                            val userPassword = user.child("password").getValue(String::class.java)
                            if (email == userEmail && password == userPassword) {
                                userFound = true
                                navController.navigate("feedback") {
                                    popUpTo("login") { inclusive = true }
                                }
                                break
                            }
                        }
                        if (!userFound) {
                            message = "Invalid email or password"
                        }
                    }.addOnFailureListener {
                        message = "Login failed: ${it.message}"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = {
                    navController.navigate("registration")
                }
            ) {
                Text("Don't have an account? Sign up")
            }
        }
    }
}
