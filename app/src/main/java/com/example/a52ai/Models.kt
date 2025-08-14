package com.example.a52ai

data class Message(val id: String, val text: String, val fromUser: Boolean, val ts: Long = System.currentTimeMillis())
data class Settings(val baseUrl: String, val apiKey: String, val model: String)
