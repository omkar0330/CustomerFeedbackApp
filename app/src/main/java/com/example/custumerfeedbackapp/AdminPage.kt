package com.example.custumerfeedbackapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.*

data class FeedbackEntry(val id: String, val feedback: String, val date: String)

@Composable
fun AdminPage(navController: NavController) {
    var feedbackList by remember { mutableStateOf(listOf<FeedbackEntry>()) }
    val dbRef = FirebaseDatabase.getInstance().reference.child("feedback")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<FeedbackEntry>()
                for (child in snapshot.children) {
                    val id = child.key ?: ""
                    val feedback = child.child("feedback").getValue(String::class.java) ?: ""
                    val date = child.child("date").getValue(String::class.java) ?: ""
                    entries.add(FeedbackEntry(id, feedback, date))
                }
                feedbackList = entries
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        dbRef.addValueEventListener(listener)
        onDispose { dbRef.removeEventListener(listener) }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Admin Feedback Dashboard", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(feedbackList) { entry ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ID: ${entry.id}", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(6.dp))
                            Text("Feedback: ${entry.feedback}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(6.dp))
                            Text("Date: ${entry.date}", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { /* View details */ }) { Text("View") }
                                TextButton(onClick = { /* Edit if needed */ }) { Text("Edit") }
                                TextButton(onClick = {
                                    dbRef.child(entry.id).removeValue()
                                }) { Text("Delete") }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Out")
            }
        }
    }
}
