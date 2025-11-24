package com.example.crimewavee.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.crimewavee.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USERS = "registered_users"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_IS_AUTHENTICATED = "is_authenticated"
    }

    init {
        
        if (getRegisteredUsers().isEmpty()) {
            initializeDefaultUsers()
        }
    }

    private fun initializeDefaultUsers() {
        val defaultUsers = listOf(
            User(
                email = "admin",
                password = "admin",
                phoneNumber = "123456789",
                rut = "11.111.111-1", // RUT válido de ejemplo
                isAdmin = true
            ),
            User(
                email = "hola",
                password = "hola",
                phoneNumber = "987654321",
                rut = "22.222.222-2", // RUT válido de ejemplo
                isAdmin = false
            )
        )

       
        sharedPreferences.edit().remove(KEY_USERS).commit()
        saveRegisteredUsers(defaultUsers)
    }

    
    fun forceReinitializeUsers() {
        
        sharedPreferences.edit()
            .remove(KEY_USERS)
            .remove(KEY_CURRENT_USER)
            .putBoolean(KEY_IS_AUTHENTICATED, false)
            .commit()

       
        initializeDefaultUsers()
    }

    fun getRegisteredUsers(): List<User> {
        val usersJson = sharedPreferences.getString(KEY_USERS, "[]")
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(usersJson, type) ?: emptyList()
    }

    private fun saveRegisteredUsers(users: List<User>) {
        val usersJson = gson.toJson(users)
        sharedPreferences.edit()
            .putString(KEY_USERS, usersJson)
            .commit() 
    }

    fun registerUser(user: User): Boolean {
        val currentUsers = getRegisteredUsers().toMutableList()

       
        if (currentUsers.any { it.email == user.email }) {
            return false
        }

        currentUsers.add(user)
        saveRegisteredUsers(currentUsers)

        
        val savedUsers = getRegisteredUsers()
        return savedUsers.any { it.email == user.email }
    }

    fun authenticateUser(email: String, password: String): User? {
        val users = getRegisteredUsers()
        return users.find { it.email == email && it.password == password }
    }

    fun saveCurrentUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit()
            .putString(KEY_CURRENT_USER, userJson)
            .putBoolean(KEY_IS_AUTHENTICATED, true)
            .commit() // Usar commit() para sincronización inmediata
    }

    fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString(KEY_CURRENT_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }

    fun isAuthenticated(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_AUTHENTICATED, false)
    }

    fun logout() {
        sharedPreferences.edit()
            .remove(KEY_CURRENT_USER)
            .putBoolean(KEY_IS_AUTHENTICATED, false)
            .commit() 
    }

    fun updateUser(updatedUser: User) {
        val currentUsers = getRegisteredUsers().toMutableList()
        val userIndex = currentUsers.indexOfFirst { it.email == updatedUser.email }

        if (userIndex >= 0) {
            currentUsers[userIndex] = updatedUser
            saveRegisteredUsers(currentUsers)

            
            if (getCurrentUser()?.email == updatedUser.email) {
                saveCurrentUser(updatedUser)
            }
        }
    }

   
    fun getDataStatus(): String {
        val users = getRegisteredUsers()
        val currentUser = getCurrentUser()
        val isAuth = isAuthenticated()

        return "Usuarios: ${users.size}, Usuario actual: ${currentUser?.email ?: "none"}, Autenticado: $isAuth"
    }
}
