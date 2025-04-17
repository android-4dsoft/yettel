@file:Suppress("MaxLineLength")

package hu.yettel.zg.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import hu.yettel.zg.domain.HighwayRepository
import hu.yettel.zg.domain.SelectedCountiesRepository
import hu.yettel.zg.domain.SelectedVignetteRepository
import hu.yettel.zg.domain.usecase.ClearSelectedCountiesUseCase
import hu.yettel.zg.domain.usecase.ClearSelectedVignetteUseCase
import hu.yettel.zg.domain.usecase.GetHighwayInfoUseCase
import hu.yettel.zg.domain.usecase.GetSelectedCountiesUseCase
import hu.yettel.zg.domain.usecase.GetSelectedVignetteUseCase
import hu.yettel.zg.domain.usecase.GetVehicleInfoUseCase
import hu.yettel.zg.domain.usecase.PlaceOrderUseCase
import hu.yettel.zg.domain.usecase.SelectCountyUseCase
import hu.yettel.zg.domain.usecase.SelectVignetteTypeUseCase
import hu.yettel.zg.domain.usecase.UnselectCountyUseCase

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

    @Provides
    @ViewModelScoped
    fun provideClearSelectedCountiesUseCase(repository: SelectedCountiesRepository): ClearSelectedCountiesUseCase = ClearSelectedCountiesUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetSelectedCountiesUseCase(repository: SelectedCountiesRepository): GetSelectedCountiesUseCase = GetSelectedCountiesUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSelectCountyUseCase(repository: SelectedCountiesRepository): SelectCountyUseCase = SelectCountyUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUnSelectCountyUseCase(repository: SelectedCountiesRepository): UnselectCountyUseCase = UnselectCountyUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSelectVignetteTypeUseCase(repository: SelectedVignetteRepository): SelectVignetteTypeUseCase = SelectVignetteTypeUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetSelectedVignetteUseCase(repository: SelectedVignetteRepository): GetSelectedVignetteUseCase = GetSelectedVignetteUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideClearSelectedVignetteUseCase(repository: SelectedVignetteRepository): ClearSelectedVignetteUseCase = ClearSelectedVignetteUseCase(repository)
}
