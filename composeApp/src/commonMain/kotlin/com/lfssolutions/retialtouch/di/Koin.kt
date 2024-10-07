package com.lfssolutions.retialtouch.di

import com.lfssolutions.retialtouch.data.local.PreferencesImpl
import com.lfssolutions.retialtouch.data.remote.api.ApiServiceImpl
import com.lfssolutions.retialtouch.dataBase.DatabaseRepository
import com.lfssolutions.retialtouch.dataBase.DatabaseRepositoryImpl
import com.lfssolutions.retialtouch.domain.LocalRepository
import com.lfssolutions.retialtouch.domain.repositories.NetworkRepository
import com.lfssolutions.retialtouch.domain.ApiService
import com.lfssolutions.retialtouch.domain.ApiUtils
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.RemoteService
import com.lfssolutions.retialtouch.utils.viewModelDefinition
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.DashBoardViewmodel
import com.lfssolutions.retialtouch.presentation.viewModels.EmployeeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.HomeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.LoginViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.PaymentTypeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.PosViewModel
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module


private const val NETWORK_TIME_OUT = 60_000L
fun appModule() = module {

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
            }


            install(HttpTimeout) {
                requestTimeoutMillis = NETWORK_TIME_OUT
                connectTimeoutMillis = NETWORK_TIME_OUT
                socketTimeoutMillis = NETWORK_TIME_OUT

            }
        }
    }

    single { Settings() }
    single { RemoteService(get(), get(),get()) }
    single { NetworkRepository() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single { LocalRepository(get()) }
    single<ApiService> { ApiServiceImpl(httpClient = get(), preferences = get(), databaseRepository = get())}

    // Initialize ApiUtils with PreferencesRepository
   /* single {
        val preferences: PreferencesRepository = get()
        val apiService: ApiService = get<ApiService>()
        ApiUtils.init(preferences,apiService) // Call the init method
    }*/

    //DataBaseRepository
    single<DatabaseRepository> { DatabaseRepositoryImpl(get()) }

    viewModelDefinition { BaseViewModel() }
    viewModelDefinition { LoginViewModel() }
    viewModelDefinition { DashBoardViewmodel() }
    viewModelDefinition { EmployeeViewModel() }
    viewModelDefinition { HomeViewModel() }
    viewModelDefinition { PosViewModel() }
    viewModelDefinition { PaymentTypeViewModel() }
}