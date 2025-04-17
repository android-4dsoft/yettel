package hu.yettel.zg.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.yettel.zg.data.repository.HighwayRepositoryImpl
import hu.yettel.zg.data.repository.SelectedCountiesRepositoryImpl
import hu.yettel.zg.data.repository.SelectedVignetteRepositoryImpl
import hu.yettel.zg.domain.HighwayRepository
import hu.yettel.zg.domain.SelectedCountiesRepository
import hu.yettel.zg.domain.SelectedVignetteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHighwayRepository(repository: HighwayRepositoryImpl): HighwayRepository

    @Binds
    @Singleton
    abstract fun bindSelectedCountiesRepository(repository: SelectedCountiesRepositoryImpl): SelectedCountiesRepository

    @Binds
    @Singleton
    abstract fun bindSelectedVignetteRepository(repository: SelectedVignetteRepositoryImpl): SelectedVignetteRepository
}
