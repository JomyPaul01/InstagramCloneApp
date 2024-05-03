package com.jomy.instagramclone.dependency

import android.content.Context
import com.jomy.instagramclone.IgApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @IgApplicationContext
    fun provideApplication(@ApplicationContext app: Context):   IgApplication{
        return app as IgApplication
    }
}

annotation class IgApplicationContext
