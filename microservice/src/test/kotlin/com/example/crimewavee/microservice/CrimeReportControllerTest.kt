package com.example.crimewavee.microservice

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureTestMvc
class CrimeReportControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var crimeReportRepository: CrimeReportRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        crimeReportRepository.deleteAll()
    }

    @Test
    fun `should create crime report successfully`() {
        val request = CreateCrimeReportRequest(
            title = "Robo en tienda",
            description = "Se reporta robo en tienda local",
            location = "Calle Principal 123",
            crimeType = "THEFT",
            reporterName = "Juan Pérez",
            reporterPhone = "+56912345678"
        )

        mockMvc.perform(
            post("/api/crime-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Robo en tienda"))
            .andExpect(jsonPath("$.location").value("Calle Principal 123"))
            .andExpect(jsonPath("$.status").value("PENDING"))
    }

    @Test
    fun `should get all crime reports`() {
        // Crear un reporte de prueba
        val report = CrimeReport(
            title = "Test Report",
            description = "Test Description",
            location = "Test Location",
            crimeType = "THEFT",
            reporterName = "Test User"
        )
        crimeReportRepository.save(report)

        mockMvc.perform(get("/api/crime-reports"))
            .andExpected(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].title").value("Test Report"))
    }

    @Test
    fun `should get reports by status`() {
        // Crear reportes con diferentes estados
        crimeReportRepository.save(
            CrimeReport(
                title = "Pending Report",
                description = "Description",
                location = "Location",
                crimeType = "THEFT",
                reporterName = "User",
                status = "PENDING"
            )
        )

        mockMvc.perform(get("/api/crime-reports/status/PENDING"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].status").value("PENDING"))
    }

    @Test
    fun `should update report status`() {
        // Crear un reporte
        val report = crimeReportRepository.save(
            CrimeReport(
                title = "Test Report",
                description = "Description",
                location = "Location",
                crimeType = "THEFT",
                reporterName = "User"
            )
        )

        val updateRequest = UpdateStatusRequest("IN_PROGRESS")

        mockMvc.perform(
            put("/api/crime-reports/${report.id}/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
    }

    @Test
    fun `should delete crime report`() {
        // Crear un reporte
        val report = crimeReportRepository.save(
            CrimeReport(
                title = "Test Report",
                description = "Description",
                location = "Location",
                crimeType = "THEFT",
                reporterName = "User"
            )
        )

        mockMvc.perform(delete("/api/crime-reports/${report.id}"))
            .andExpect(status().isOk)

        // Verificar que el reporte fue eliminado
        mockMvc.perform(get("/api/crime-reports/${report.id}"))
            .andExpect(status().isOk)
            .andExpect(content().string(""))
    }
}
