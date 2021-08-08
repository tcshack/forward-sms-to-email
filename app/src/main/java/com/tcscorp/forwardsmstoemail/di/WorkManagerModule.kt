package com.tcscorp.forwardsmstoemail.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {
    @Provides
    fun providesWorkManager(@ApplicationContext appContext: Context): WorkManager =
        WorkManager.getInstance(appContext)
}
