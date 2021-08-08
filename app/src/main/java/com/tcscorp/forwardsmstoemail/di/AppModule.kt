package com.tcscorp.forwardsmstoemail.di

import com.tcscorp.forwardsmstoemail.data.DefaultMessageRepository
import com.tcscorp.forwardsmstoemail.data.MessageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun provideMessageRepository(
        defaultMessageRepository: DefaultMessageRepository
    ): MessageRepository

}