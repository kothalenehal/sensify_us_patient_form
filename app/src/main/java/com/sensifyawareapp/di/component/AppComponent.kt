package com.sensifyawareapp.di.component

import android.app.Application
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.api.ApiManager
import com.sensifyawareapp.api.RetrofitInterceptor
import com.sensifyawareapp.di.module.AppModule
import com.sensifyawareapp.di.module.NetworkModule
import com.sensifyawareapp.di.module.TwitterModule
import com.sensifyawareapp.model.base.BaseModel
import com.sensifyawareapp.utils.NetworkUtils
import com.sensifyawareapp.utils.PrefUtils
import com.sensifyawareapp.utils.common.FontCache
import com.sensifyawareapp.utils.permission.PermissionUtil
import dagger.Component
import javax.inject.Singleton

/**
 *
 *
 */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, TwitterModule::class])
interface AppComponent {
    fun inject(activity: BaseActivity)

    fun inject(baseModel: BaseModel);
    fun inject(interceptor: RetrofitInterceptor)
    fun provideApiManager(): ApiManager
    fun providePrefUtil(): PrefUtils
    fun provideApplication(): Application

    //    fun provideFileUtils(): FileUtils?
    fun provideNetworkUtils(): NetworkUtils
    fun providePermissionUtil(): PermissionUtil
    fun provideFontCache(): FontCache
}