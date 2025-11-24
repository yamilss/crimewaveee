package com.example.crimewavee.ui.theme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.crimewavee.data.model.User
import com.example.crimewavee.data.model.AuthState
import com.example.crimewavee.data.model.BillingAddress
import com.example.crimewavee.data.model.ShippingAddress
import com.example.crimewavee.data.repository.UserRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application.applicationContext)

    private val _authState = mutableStateOf(AuthState())
    val authState: State<AuthState> = _authState

    init {
        userRepository.forceReinitializeUsers()

        _authState.value = AuthState(
            isAuthenticated = false,
            currentUser = null,
            error = null
        )
    }

    fun login(email: String, password: String) {
        _authState.value = _authState.value.copy(error = null)

        if (email.isBlank() || password.isBlank()) {
            _authState.value = _authState.value.copy(error = "Por favor complete todos los campos")
            return
        }

        val user = userRepository.authenticateUser(email.trim(), password.trim())

        if (user != null) {
            userRepository.saveCurrentUser(user)
            _authState.value = AuthState(
                isAuthenticated = true,
                currentUser = user,
                error = null
            )
        } else {
            _authState.value = _authState.value.copy(
                error = "Credenciales incorrectas"
            )
        }
    }

    fun register(email: String, phoneNumber: String, rut: String, password: String, confirmPassword: String) {

        if (email.isBlank() || phoneNumber.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _authState.value = _authState.value.copy(error = "Por favor complete todos los campos")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = _authState.value.copy(error = "Email inválido")
            return
        }

        if (phoneNumber.length != 9 || !phoneNumber.all { it.isDigit() }) {
            _authState.value = _authState.value.copy(error = "El número debe tener 9 dígitos")
            return
        }

        // Validar RUT si se proporciona
        if (rut.isNotBlank() && !com.example.crimewavee.utils.RutValidator.isValidRut(rut)) {
            _authState.value = _authState.value.copy(error = "RUT inválido")
            return
        }

        if (password != confirmPassword) {
            _authState.value = _authState.value.copy(error = "Las contraseñas no coinciden")
            return
        }

        if (password.length < 4) {
            _authState.value = _authState.value.copy(error = "La contraseña debe tener al menos 4 caracteres")
            return
        }

        val newUser = User(
            email = email,
            password = password,
            phoneNumber = phoneNumber,
            rut = if (rut.isBlank()) null else rut,
            isAdmin = false
        )

        val success = userRepository.registerUser(newUser)

        if (success) {
            userRepository.saveCurrentUser(newUser)
            _authState.value = AuthState(
                isAuthenticated = true,
                currentUser = newUser,
                error = null
            )
        } else {
            _authState.value = _authState.value.copy(error = "El usuario ya existe")
        }
    }

    fun logout() {
        userRepository.logout()
        _authState.value = AuthState(
            isAuthenticated = false,
            currentUser = null,
            error = null
        )
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    fun updateUserDetails(
        currentPassword: String,
        newPhone: String?,
        newPassword: String?
    ): Boolean {
        val currentUser = _authState.value.currentUser ?: return false

        if (currentPassword.isBlank() || currentUser.password != currentPassword) {
            _authState.value = _authState.value.copy(error = "Contraseña actual incorrecta")
            return false
        }

        if (!newPhone.isNullOrBlank()) {
            if (newPhone.length != 9 || !newPhone.all { it.isDigit() }) {
                _authState.value = _authState.value.copy(error = "El número debe tener 9 dígitos")
                return false
            }
        }

        if (!newPassword.isNullOrBlank()) {
            if (newPassword.length < 4) {
                _authState.value = _authState.value.copy(error = "La contraseña debe tener al menos 4 caracteres")
                return false
            }
        }


        val updatedUser = currentUser.copy(
            phoneNumber = newPhone?.takeIf { it.isNotBlank() } ?: currentUser.phoneNumber,
            password = newPassword?.takeIf { it.isNotBlank() } ?: currentUser.password
        )

        userRepository.updateUser(updatedUser)

        _authState.value = _authState.value.copy(
            currentUser = updatedUser,
            error = null
        )

        return true
    }

    fun saveShippingAddress(
        nombre: String,
        apellidos: String,
        direccion: String,
        rut: String,
        ciudad: String,
        codigoPostal: String,
        pais: String,
        region: String,
        celular: String,
        instagram: String
    ): Boolean {
        val currentUser = _authState.value.currentUser ?: return false


        if (nombre.isBlank() || apellidos.isBlank() || direccion.isBlank() ||
            rut.isBlank() || ciudad.isBlank() || codigoPostal.isBlank() ||
            pais.isBlank() || region.isBlank() || celular.isBlank()) {
            _authState.value = _authState.value.copy(error = "Por favor complete todos los campos obligatorios")
            return false
        }

        if (celular.length != 9 || !celular.all { it.isDigit() }) {
            _authState.value = _authState.value.copy(error = "El celular debe tener exactamente 9 dígitos")
            return false
        }

        if (!com.example.crimewavee.utils.RutValidator.isValidRut(rut)) {
            _authState.value = _authState.value.copy(error = "RUT inválido")
            return false
        }

        val shippingAddress = ShippingAddress(
            nombre = nombre,
            apellidos = apellidos,
            direccion = direccion,
            rut = rut,
            ciudad = ciudad,
            codigoPostal = codigoPostal,
            pais = pais,
            region = region,
            celular = celular,
            instagram = instagram
        )

        val updatedUser = currentUser.copy(shippingAddress = shippingAddress)

        userRepository.updateUser(updatedUser)

        _authState.value = _authState.value.copy(currentUser = updatedUser)

        return true
    }

    fun saveBillingAddress(
        nombre: String,
        apellidos: String,
        direccion: String,
        rut: String,
        ciudad: String,
        codigoPostal: String,
        pais: String,
        region: String,
        celular: String,
        instagram: String
    ): Boolean {
        val currentUser = _authState.value.currentUser ?: return false

        if (nombre.isBlank() || apellidos.isBlank() || direccion.isBlank() ||
            rut.isBlank() || ciudad.isBlank() || codigoPostal.isBlank() ||
            pais.isBlank() || region.isBlank() || celular.isBlank()) {
            _authState.value = _authState.value.copy(error = "Por favor complete todos los campos obligatorios")
            return false
        }

        if (celular.length != 9 || !celular.all { it.isDigit() }) {
            _authState.value = _authState.value.copy(error = "El celular debe tener exactamente 9 dígitos")
            return false
        }

        if (!com.example.crimewavee.utils.RutValidator.isValidRut(rut)) {
            _authState.value = _authState.value.copy(error = "RUT inválido")
            return false
        }

        val billingAddress = BillingAddress(
            nombre = nombre,
            apellidos = apellidos,
            direccion = direccion,
            rut = rut,
            ciudad = ciudad,
            codigoPostal = codigoPostal,
            pais = pais,
            region = region,
            celular = celular,
            instagram = instagram
        )

        val updatedUser = currentUser.copy(billingAddress = billingAddress)

        userRepository.updateUser(updatedUser)

        _authState.value = _authState.value.copy(currentUser = updatedUser)

        return true
    }

    fun deleteShippingAddress(): Boolean {
        val currentUser = _authState.value.currentUser ?: return false
        val updatedUser = currentUser.copy(shippingAddress = null)

        userRepository.updateUser(updatedUser)

        _authState.value = _authState.value.copy(currentUser = updatedUser)
        return true
    }



    fun deleteBillingAddress(): Boolean {
        val currentUser = _authState.value.currentUser ?: return false
        val updatedUser = currentUser.copy(billingAddress = null)

        userRepository.updateUser(updatedUser)

        _authState.value = _authState.value.copy(currentUser = updatedUser)
        return true
    }

    fun reinitializeUsers() {
        userRepository.forceReinitializeUsers()
    }

    fun getRegisteredUsers(): List<User> {
        return userRepository.getRegisteredUsers()
    }

    fun userExists(email: String): Boolean {
        return userRepository.getRegisteredUsers().any { it.email == email }
    }
}
