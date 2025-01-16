package com.lfssolutions.retialtouch.di

import com.lfssolutions.retialtouch.data.local.PreferencesImpl
import com.lfssolutions.retialtouch.data.remote.api.ApiServiceImpl
import com.lfssolutions.retialtouch.domain.SqlRepository
import com.lfssolutions.retialtouch.data.sqlDelightDb.SqlRepositoryImpl
import com.lfssolutions.retialtouch.domain.repositories.NetworkRepository
import com.lfssolutions.retialtouch.domain.ApiService
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.repositories.DataBaseRepository
import com.lfssolutions.retialtouch.presentation.ui.payout.PayoutViewModel
import com.lfssolutions.retialtouch.presentation.ui.settings.SettingViewModel
import com.lfssolutions.retialtouch.utils.viewModelDefinition
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.EmployeeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.HomeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.LoginViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.PaymentCollectorViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.PaymentTypeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.PrinterViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.SettlementViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.TransactionViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.TransactionDetailsViewModel
import com.lfssolutions.retialtouch.retailTouchDB
import com.lfssolutions.retialtouch.sync.SyncViewModel
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
    single { NetworkRepository() }
    single { DataBaseRepository() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<ApiService> { ApiServiceImpl(httpClient = get(), preferences = get(), sqlRepository = get())}


   //Database Driver
    single<retailTouchDB> { retailTouchDB.invoke(get()) }
    //DataBaseRepository
    single<SqlRepository> { SqlRepositoryImpl(get()) }
    //ViewModels
    single { SharedPosViewModel() }
    single { HomeViewModel() }
    single { PrinterViewModel() }

    viewModelDefinition { BaseViewModel() }
    viewModelDefinition { SyncViewModel() }
    viewModelDefinition { PaymentCollectorViewModel() }
    viewModelDefinition { LoginViewModel() }
    viewModelDefinition { EmployeeViewModel() }
    viewModelDefinition { PaymentTypeViewModel() }
    viewModelDefinition { TransactionViewModel() }
    viewModelDefinition { TransactionDetailsViewModel() }
    viewModelDefinition { SettlementViewModel() }
    viewModelDefinition { SettingViewModel() }
    viewModelDefinition { PayoutViewModel() }
}