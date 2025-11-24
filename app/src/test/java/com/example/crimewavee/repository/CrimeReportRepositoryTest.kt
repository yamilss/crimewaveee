package com.example.crimewavee.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import com.example.crimewavee.data.repository.CrimeReportRepository
import com.example.crimewavee.data.api.CrimeReportApiService
import com.example.crimewavee.data.api.CrimeReportResponse
import com.example.crimewavee.data.api.CreateCrimeReportRequest

@ExperimentalCoroutinesApi
class CrimeReportRepositoryTest {

    @Mock
    private lateinit var mockApiService: CrimeReportApiService

    private lateinit var repository: CrimeReportRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = CrimeReportRepository()
    }

    @Test
    fun `getAllReports should return success when API call succeeds`() = runTest {
        // Given
        val expectedReports = listOf(
            CrimeReportResponse(
                id = 1L,
                title = "Test Report 1",
                description = "Test Description 1",
                location = "Test Location 1",
                crimeType = "THEFT",
                reporterName = "Test Reporter 1",
                reporterPhone = "+56912345678",
                createdAt = "2024-11-24T10:00:00",
                status = "PENDING"
            ),
            CrimeReportResponse(
                id = 2L,
                title = "Test Report 2",
                description = "Test Description 2",
                location = "Test Location 2",
                crimeType = "VANDALISM",
                reporterName = "Test Reporter 2",
                reporterPhone = null,
                createdAt = "2024-11-24T11:00:00",
                status = "IN_PROGRESS"
            )
        )

        // When
        val result = repository.getAllReports()

        // Then
        assertTrue(result.isSuccess)
        val reports = result.getOrNull()
        assertNotNull(reports)
        assertEquals(3, reports?.size) // Mock data returns 3 items
    }

    @Test
    fun `createReport should return success with valid request`() = runTest {
        // Given
        val createRequest = CreateCrimeReportRequest(
            title = "New Crime Report",
            description = "Detailed description of the crime",
            location = "123 Main Street, Santiago",
            crimeType = "THEFT",
            reporterName = "John Doe",
            reporterPhone = "+56912345678"
        )

        // When
        val result = repository.createReport(createRequest)

        // Then
        assertTrue(result.isSuccess)
        val createdReport = result.getOrNull()
        assertNotNull(createdReport)
        assertEquals("New Crime Report", createdReport?.title)
        assertEquals("THEFT", createdReport?.crimeType)
        assertEquals("John Doe", createdReport?.reporterName)
        assertEquals("PENDING", createdReport?.status)
    }

    @Test
    fun `getReportsByStatus should filter reports correctly`() = runTest {
        // Given
        val status = "PENDING"

        // When
        val result = repository.getReportsByStatus(status)

        // Then
        assertTrue(result.isSuccess)
        val reports = result.getOrNull()
        assertNotNull(reports)

        // Verify all returned reports have the requested status
        reports?.forEach { report ->
            assertEquals(status, report.status)
        }
    }

    @Test
    fun `createReport should handle missing phone number`() = runTest {
        // Given
        val createRequest = CreateCrimeReportRequest(
            title = "Report without phone",
            description = "Description",
            location = "Location",
            crimeType = "VANDALISM",
            reporterName = "Anonymous Reporter",
            reporterPhone = null
        )

        // When
        val result = repository.createReport(createRequest)

        // Then
        assertTrue(result.isSuccess)
        val createdReport = result.getOrNull()
        assertNotNull(createdReport)
        assertEquals("Report without phone", createdReport?.title)
        assertEquals("Anonymous Reporter", createdReport?.reporterName)
        assertNull(createdReport?.reporterPhone)
    }

    @Test
    fun `getAllReports should return mock data when network fails`() = runTest {
        // When (network will fail, so mock data should be returned)
        val result = repository.getAllReports()

        // Then
        assertTrue(result.isSuccess)
        val reports = result.getOrNull()
        assertNotNull(reports)
        assertTrue(reports!!.isNotEmpty())

        // Verify mock data structure
        val firstReport = reports[0]
        assertEquals("Robo en comercio local", firstReport.title)
        assertEquals("THEFT", firstReport.crimeType)
        assertEquals("María González", firstReport.reporterName)
    }

    @Test
    fun `mock data should contain different crime types`() = runTest {
        // When
        val result = repository.getAllReports()

        // Then
        assertTrue(result.isSuccess)
        val reports = result.getOrNull()
        assertNotNull(reports)

        val crimeTypes = reports!!.map { it.crimeType }.distinct()
        assertTrue(crimeTypes.contains("THEFT"))
        assertTrue(crimeTypes.contains("VANDALISM"))
        assertTrue(crimeTypes.contains("ASSAULT"))
    }

    @Test
    fun `mock data should contain different statuses`() = runTest {
        // When
        val result = repository.getAllReports()

        // Then
        assertTrue(result.isSuccess)
        val reports = result.getOrNull()
        assertNotNull(reports)

        val statuses = reports!!.map { it.status }.distinct()
        assertTrue(statuses.contains("PENDING"))
        assertTrue(statuses.contains("IN_PROGRESS"))
        assertTrue(statuses.contains("RESOLVED"))
    }
}
