package hu.yettel.zg.domain

import hu.yettel.zg.domain.model.HighwayInfo
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GetHighwayInfoUseCaseTest {
    private lateinit var repository: HighwayRepository
    private lateinit var useCase: GetHighwayInfoUseCase

    @Before
    fun setUp() {
        repository = mock(HighwayRepository::class.java)
        useCase = GetHighwayInfoUseCase(repository)
    }

    @Test
    fun `invoke emits Loading followed by Success when repository returns success`() =
        runTest {
            // Arrange
            val mockHighwayInfo = HighwayInfo(
                vignettes = emptyList(),
                vehicleCategories = emptyList(),
                counties = emptyList(),
            )
            `when`(repository.getHighwayInfo()).thenReturn(Result.Success(mockHighwayInfo))

            // Act
            val results = useCase().toList()

            // Assert
            assertEquals(2, results.size)
            assertTrue(results[0] is Result.Loading)
            assertTrue(results[1] is Result.Success)
            assertEquals(mockHighwayInfo, (results[1] as Result.Success).data)
        }

    @Test
    fun `invoke emits Loading followed by Error when repository returns error`() =
        runTest {
            // Arrange
            val exception = RuntimeException("Test exception")
            `when`(repository.getHighwayInfo()).thenReturn(Result.Error(exception))

            // Act
            val results = useCase().toList()

            // Assert
            assertEquals(2, results.size)
            assertTrue(results[0] is Result.Loading)
            assertTrue(results[1] is Result.Error)
            assertEquals(exception, (results[1] as Result.Error).exception)
        }
}
