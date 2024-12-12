package com.example.myapp016asharedtasklist

import com.google.firebase.firestore.PropertyName

data class Task(
    var id: String = "", // Identifikátor úkolu
    var name: String = "", // Název úkolu
    @PropertyName("isCompleted")
    var isCompleted: Boolean = false, // Stav dokončení
    var assignedTo: String = "" // Přiřazená osoba
)
