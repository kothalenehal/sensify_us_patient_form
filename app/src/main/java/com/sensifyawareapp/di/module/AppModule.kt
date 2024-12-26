package com.sensifyawareapp.di.module

import android.app.Application
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.api.ApiInterface
import com.sensifyawareapp.api.ApiManager
import com.sensifyawareapp.utils.NetworkUtils
import com.sensifyawareapp.utils.PrefUtils
import com.sensifyawareapp.utils.common.FontCache
import com.sensifyawareapp.utils.permission.PermissionUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Singleton

/**
 *
 *
 */
@Module
@DisableInstallInCheck
class AppModule(application: SensifyAwareApplication) {
    private val mApplication: SensifyAwareApplication = application

    @Provides
    @Singleton
    fun providesApplication(): Application {
        return mApplication
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    fun  // Application reference must come from AppModule.class
            providesSharedPreferences(): PrefUtils {
        return PrefUtils()
    }

    @Provides
    @Singleton
    fun provideBaseAppActivity(baseActivity: BaseActivity): BaseActivity {
        return baseActivity
    }

//    @Provides
//    @Singleton
//    fun provideApiManager(apiInterface: ApiInterface): ApiManager {
//        return ApiManager(apiInterface)
//    }

    /*@Provides
    @Singleton
    fun provideFileUtils(): FileUtils {
        return FileUtils()
    }*/

    @Provides
    @Singleton
    fun provideNetworkUtils(): NetworkUtils {
        return NetworkUtils(mApplication.getApplicationContext())
    }

    @Provides
    @Singleton
    fun providePermissionUtil(): PermissionUtil {
        return PermissionUtil()
    }

    @Provides
    @Singleton
    fun provideFontCache(): FontCache {
        return FontCache(mApplication.getApplicationContext())
    }

}