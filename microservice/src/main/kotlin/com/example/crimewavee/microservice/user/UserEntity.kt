package com.example.crimewavee.microservice.user

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*
import java.time.LocalDateTime

// Entidad para usuarios
@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "phone_number")
    val phoneNumber: String? = null,

    @Column(nullable = false)
    val rut: String,

    @Column(name = "is_admin")
    val isAdmin: Boolean = false,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

// Repository para operaciones CRUD de usuarios
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailAndPassword(email: String, password: String): UserEntity?
    fun findByIsAdmin(isAdmin: Boolean): List<UserEntity>
}

// DTOs para usuarios
data class CreateUserRequest(
    val email: String,
    val password: String,
    val phoneNumber: String? = null,
    val rut: String,
    val isAdmin: Boolean = false
)

data class UpdateUserRequest(
    val password: String? = null,
    val phoneNumber: String? = null,
    val rut: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val id: Long,
    val email: String,
    val phoneNumber: String?,
    val rut: String,
    val isAdmin: Boolean,
    val message: String
)

data class UserResponse(
    val id: Long,
    val email: String,
    val phoneNumber: String?,
    val rut: String,
    val isAdmin: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

