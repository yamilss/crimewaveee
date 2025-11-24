// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Spring Boot plugins for microservice - Compatible Java 11
    kotlin("jvm") version "1.8.22" apply false
    kotlin("plugin.spring") version "1.8.22" apply false
    kotlin("plugin.jpa") version "1.8.22" apply false
    id("org.springframework.boot") version "2.7.14" apply false
    id("io.spring.dependency-management") version "1.0.15.RELEASE" apply false
}