// ARCHIVO DE TESTS ELIMINADO - REFERENCIAS A NOTICIAS REMOVIDAS
// Usar ClothingViewModelCleanTest.kt en su lugar
import com.example.crimewavee.data.api.NewsSource

@ExperimentalCoroutinesApi
class ClothingViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockProductRepository: ProductRepository

    @Mock
    // mockCrimeNewsRepository eliminado - no se usa en app de ropa

    private lateinit var viewModel: ClothingViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Mock de productos para testing
        val mockProducts = listOf(
            ClothingItem(
                id = "1",
                name = "Test Product 1",
                description = "Test Description",
                price = 25000.0,
                imageUrl = "test_image",
                imageUrls = listOf("test_image"),
                category = ProductType.POLERAS,
                isNew = true,
                isFeatured = false,
                sizes = listOf("S", "M", "L")
            ),
            ClothingItem(
                id = "2",
                name = "Test Product 2",
                description = "Featured Product",
                price = 35000.0,
                imageUrl = "test_image2",
                imageUrls = listOf("test_image2"),
                category = ProductType.POLERONES,
                isNew = false,
                isFeatured = true,
                sizes = listOf("M", "L", "XL")
            )
        )

        `when`(mockProductRepository.getProducts()).thenReturn(mockProducts)

        viewModel = ClothingViewModel(mockApplication)
    }

    @Test
    fun `should load products successfully`() {
        // Given
        val expectedProductCount = 2

        // When
        val products = viewModel.products.value

        // Then
        assertEquals(expectedProductCount, products.size)
        assertEquals("Test Product 1", products[0].name)
        assertEquals("Test Product 2", products[1].name)
    }

    @Test
    fun `should filter featured products correctly`() {
        // Given - setup already done in @Before

        // When
        val featuredProducts = viewModel.featuredProducts.value

        // Then
        assertEquals(1, featuredProducts.size)
        assertEquals("Test Product 2", featuredProducts[0].name)
        assertTrue(featuredProducts[0].isFeatured)
    }

    @Test
    fun `should load categories successfully`() {
        // When
        val categories = viewModel.categories.value

        // Then
        assertTrue(categories.isNotEmpty())
        assertEquals(6, categories.size)
        assertEquals("CATEGORÍA 1", categories[0].name)
    }

    @Test
    fun `should get product by id successfully`() {
        // Given
        val productId = "1"
        val expectedProduct = ClothingItem(
            id = "1",
            name = "Test Product By ID",
            description = "Test Description",
            price = 25000.0,
            imageUrl = "test_image",
            imageUrls = listOf("test_image"),
            category = ProductType.POLERAS,
            isNew = true,
            isFeatured = false,
            sizes = listOf("S", "M", "L")
        )

        `when`(mockProductRepository.getProductById(productId)).thenReturn(expectedProduct)

        // When
        val result = viewModel.getProductById(productId)

        // Then
        assertNotNull(result)
        assertEquals("Test Product By ID", result?.name)
        assertEquals(productId, result?.id)
    }

    @Test
    fun `should select product successfully`() {
        // Given
        val productToSelect = ClothingItem(
            id = "1",
            name = "Selected Product",
            description = "Test Description",
            price = 25000.0,
            imageUrl = "test_image",
            imageUrls = listOf("test_image"),
            category = ProductType.POLERAS,
            isNew = true,
            isFeatured = false,
            sizes = listOf("S", "M", "L")
        )

        // When
        viewModel.selectProduct(productToSelect)

        // Then
        assertEquals(productToSelect, viewModel.selectedProduct.value)
        assertEquals("Selected Product", viewModel.selectedProduct.value?.name)
    }

    @Test
    fun `should handle crime news loading successfully`() = runTest {
        // Given
        val mockNewsResponse = CrimeNewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                NewsArticle(
                    title = "Test Crime News",
                    description = "Test Description",
                    url = "https://test.com",
                    urlToImage = "https://test.com/image.jpg",
                    publishedAt = "2024-11-24T10:00:00Z",
                    source = NewsSource(id = "test", name = "Test Source")
                )
            )
        )

        `when`(mockCrimeNewsRepository.getCrimeNews()).thenReturn(Result.success(mockNewsResponse))

        // When
        viewModel.loadCrimeNews()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.crimeNews.value.size)
        assertEquals("Test Crime News", viewModel.crimeNews.value[0].title)
        assertFalse(viewModel.isLoadingNews.value)
        assertNull(viewModel.newsError.value)
    }

    @Test
    fun `should handle crime news loading error`() = runTest {
        // Given
        val errorMessage = "Network error"
        `when`(mockCrimeNewsRepository.getCrimeNews()).thenReturn(Result.failure(Exception(errorMessage)))

        // When
        viewModel.loadCrimeNews()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.crimeNews.value.isEmpty())
        assertFalse(viewModel.isLoadingNews.value)
        assertTrue(viewModel.newsError.value?.contains(errorMessage) == true)
    }

    @Test
    fun `should refresh news successfully`() = runTest {
        // Given
        val mockNewsResponse = CrimeNewsResponse(
            status = "ok",
            totalResults = 2,
            articles = listOf(
                NewsArticle(
                    title = "Refreshed News 1",
                    description = "Description 1",
                    url = "https://test1.com",
                    urlToImage = null,
                    publishedAt = "2024-11-24T10:00:00Z",
                    source = NewsSource(id = "test1", name = "Test Source 1")
                ),
                NewsArticle(
                    title = "Refreshed News 2",
                    description = "Description 2",
                    url = "https://test2.com",
                    urlToImage = null,
                    publishedAt = "2024-11-24T11:00:00Z",
                    source = NewsSource(id = "test2", name = "Test Source 2")
                )
            )
        )

        `when`(mockCrimeNewsRepository.getCrimeNews()).thenReturn(Result.success(mockNewsResponse))

        // When
        viewModel.refreshNews()
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.crimeNews.value.size)
        assertEquals("Refreshed News 1", viewModel.crimeNews.value[0].title)
        assertEquals("Refreshed News 2", viewModel.crimeNews.value[1].title)
    }
}
