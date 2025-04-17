package hu.yettel.zg.domain

import hu.yettel.zg.domain.model.LocalizedName
import hu.yettel.zg.domain.model.Result
import hu.yettel.zg.domain.model.Vehicle
import hu.yettel.zg.domain.usecase.GetVehicleInfoUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GetVehicleInfoUseCaseTest {
    private lateinit var repository: HighwayRepository
    private lateinit var useCase: GetVehicleInfoUseCase

    @Before
    fun setUp() {
        repository = mock(HighwayRepository::class.java)
        useCase = GetVehicleInfoUseCase(repository)
    }

    @Test
    fun `invoke returns Success when repository returns success`() =
        runTest {
            // Arrange
            val mockVehicle = Vehicle(
                internationalCode = "H",
                type = "CAR",
                ownerName = "Michael Scott",
                licensePlate = "ABC-123",
                country = LocalizedName(
                    hungarian = "Magyarország",
                    english = "Hungary",
                ),
                vignetteType = "D1",
            )

            `when`(repository.getVehicleInfo()).thenReturn(Result.Success(mockVehicle))

            // Act
            val result = useCase()

            // Assert
            assertTrue(result is Result.Success)
            assertEquals(mockVehicle, (result as Result.Success).data)
            verify(repository).getVehicleInfo()
        }

    @Test
    fun `invoke returns Error when repository returns error`() =
        runTest {
            // Arrange
            val exception = RuntimeException("Test exception")
            `when`(repository.getVehicleInfo()).thenReturn(Result.Error(exception))

            // Act
            val result = useCase()

            // Assert
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            verify(repository).getVehicleInfo()
        }
}
