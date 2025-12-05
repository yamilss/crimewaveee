package com.example.crimewavee.microservice.user

import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

// Controlador REST para usuarios
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = ["*"])
class UserController(
    private val userRepository: UserRepository
) {

    /**
     * Obtener todos los usuarios
     */
    @GetMapping
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        val user = userRepository.findById(id).orElse(null)
        return if (user != null) {
            ResponseEntity.ok(user.toResponse())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Obtener usuario por email
     */
    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserResponse> {
        val user = userRepository.findByEmail(email)
        return if (user != null) {
            ResponseEntity.ok(user.toResponse())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Obtener todos los administradores
     */
    @GetMapping("/admins/list")
    fun getAllAdmins(): List<UserResponse> {
        return userRepository.findByIsAdmin(true).map { it.toResponse() }
    }

    /**
     * Registrar nuevo usuario
     */
    @PostMapping("/register")
    fun registerUser(@RequestBody request: CreateUserRequest): ResponseEntity<Any> {
        return try {
            // Validar que el email no exista
            if (userRepository.findByEmail(request.email) != null) {
                return ResponseEntity.badRequest().body(
                    mapOf("error" to "El email ya está registrado")
                )
            }

            val newUser = UserEntity(
                email = request.email,
                password = request.password,
                phoneNumber = request.phoneNumber,
                rut = request.rut,
                isAdmin = false // Los nuevos usuarios no son admin por defecto
            )

            val savedUser = userRepository.save(newUser)
            ResponseEntity.status(HttpStatus.CREATED).body(savedUser.toResponse())
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                mapOf("error" to "Error al registrar usuario: ${e.message}")
            )
        }
    }

    /**
     * Login - Autenticar usuario
     */
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val user = userRepository.findByEmailAndPassword(request.email, request.password)
            if (user != null) {
                ResponseEntity.ok(
                    LoginResponse(
                        id = user.id,
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        rut = user.rut,
                        isAdmin = user.isAdmin,
                        message = "Login exitoso"
                    )
                )
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    mapOf("error" to "Email o contraseña incorrectos")
                )
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                mapOf("error" to "Error al iniciar sesión: ${e.message}")
            )
        }
    }

    /**
     * Actualizar usuario
     */
    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Any> {
        return try {
            val user = userRepository.findById(id).orElse(null)
            if (user != null) {
                val updatedUser = user.copy(
                    password = request.password ?: user.password,
                    phoneNumber = request.phoneNumber ?: user.phoneNumber,
                    rut = request.rut ?: user.rut,
                    updatedAt = LocalDateTime.now()
                )
                val savedUser = userRepository.save(updatedUser)
                ResponseEntity.ok(savedUser.toResponse())
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                mapOf("error" to "Error al actualizar usuario: ${e.message}")
            )
        }
    }

    /**
     * Eliminar usuario
     */
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id)
                ResponseEntity.ok(mapOf("message" to "Usuario eliminado exitosamente"))
            } else {
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                mapOf("error" to "Error al eliminar usuario: ${e.message}")
            )
        }
    }

    /**
     * Inicializar usuarios predefinidos
     */
    @PostMapping("/init-default-users")
    fun initializeDefaultUsers(): ResponseEntity<Any> {
        return try {
            val defaultUsers = listOf(
                UserEntity(
                    email = "admin",
                    password = "admin",
                    phoneNumber = "123456789",
                    rut = "11.111.111-1",
                    isAdmin = true
                ),
                UserEntity(
                    email = "hola",
                    password = "hola",
                    phoneNumber = "987654321",
                    rut = "22.222.222-2",
                    isAdmin = false
                )
            )

            var createdCount = 0
            defaultUsers.forEach { user ->
                if (userRepository.findByEmail(user.email) == null) {
                    userRepository.save(user)
                    createdCount++
                }
            }

            ResponseEntity.ok(
                mapOf(
                    "message" to "Usuarios predefinidos inicializados",
                    "created" to createdCount,
                    "total" to userRepository.count()
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                mapOf("error" to "Error al inicializar usuarios: ${e.message}")
            )
        }
    }

    /**
     * Obtener estadísticas de usuarios
     */
    @GetMapping("/stats/summary")
    fun getStats(): ResponseEntity<Map<String, Any>> {
        return try {
            val totalUsers = userRepository.count()
            val adminCount = userRepository.findByIsAdmin(true).size
            val regularCount = totalUsers.toInt() - adminCount

            ResponseEntity.ok(
                mapOf(
                    "totalUsers" to totalUsers,
                    "admins" to adminCount,
                    "regularUsers" to regularCount
                )
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
}

// Función de extensión para convertir UserEntity a UserResponse
fun UserEntity.toResponse() = UserResponse(
    id = this.id,
    email = this.email,
    phoneNumber = this.phoneNumber,
    rut = this.rut,
    isAdmin = this.isAdmin,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

