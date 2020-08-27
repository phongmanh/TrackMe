package com.manhnguyen.trackme.di.component

import com.manhnguyen.trackme.data.api.ApiInterface
import dagger.Subcomponent
import javax.inject.Scope


@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope

@ActivityScope
@Subcomponent
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    /*Inject annotation and provision function
    * if you don't want to use Inject annotation*/
    fun getApiService(): ApiInterface

   /* fun inject(loginActivity: LoginActivity)
    *//*if have some Fragment relate to LoginActivity, we have to inject them  */

}