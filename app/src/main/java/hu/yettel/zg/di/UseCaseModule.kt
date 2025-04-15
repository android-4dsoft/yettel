@file:Suppress("MaxLineLength")

package hu.yettel.zg.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import hu.yettel.zg.domain.HighwayRepository
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import hu.yettel.zg.domain.usecase.GetVehicleInfoUseCase
import hu.yettel.zg.domain.usecase.PlaceOrderUseCase

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideGetHighwayInfoUseCase(repository: HighwayRepository): GetHighwayInfoUseCase = GetHighwayInfoUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetVehicleInfoUseCase(repository: HighwayRepository): GetVehicleInfoUseCase = GetVehicleInfoUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providePlaceOrderUseCase(repository: HighwayRepository): PlaceOrderUseCase = PlaceOrderUseCase(repository)
}
