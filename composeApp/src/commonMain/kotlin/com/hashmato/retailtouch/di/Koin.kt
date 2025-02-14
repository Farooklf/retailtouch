package com.hashmato.retailtouch.di

import com.hashmato.retailtouch.data.local.PreferencesImpl
import com.hashmato.retailtouch.data.remote.api.ApiServiceImpl
import com.hashmato.retailtouch.domain.SqlRepository
import com.hashmato.retailtouch.data.sqlDelightDb.SqlRepositoryImpl
import com.hashmato.retailtouch.domain.repositories.NetworkRepository
import com.hashmato.retailtouch.domain.ApiService
import com.hashmato.retailtouch.domain.PreferencesRepository
import com.hashmato.retailtouch.domain.repositories.DataBaseRepository
import com.hashmato.retailtouch.presentation.ui.members.MemberViewModel
import com.hashmato.retailtouch.presentation.ui.payout.PayoutViewModel
import com.hashmato.retailtouch.presentation.ui.settings.SettingViewModel
import com.hashmato.retailtouch.presentation.ui.stocks.StockViewModel
import com.hashmato.retailtouch.utils.viewModelDefinition
import com.hashmato.retailtouch.presentation.viewModels.BaseViewModel
import com.hashmato.retailtouch.presentation.viewModels.EmployeeViewModel
import com.hashmato.retailtouch.presentation.viewModels.HomeViewModel
import com.hashmato.retailtouch.presentation.viewModels.LoginViewModel
import com.hashmato.retailtouch.presentation.viewModels.PaymentCollectorViewModel
import com.hashmato.retailtouch.presentation.viewModels.PaymentTypeViewModel
import com.hashmato.retailtouch.presentation.viewModels.PrinterViewModel
import com.hashmato.retailtouch.presentation.viewModels.SettlementViewModel
import com.hashmato.retailtouch.presentation.viewModels.TransactionViewModel
import com.hashmato.retailtouch.presentation.viewModels.SharedPosViewModel
import com.hashmato.retailtouch.presentation.viewModels.TransactionDetailsViewModel
import com.hashmato.retailtouch.sqldelight.retailtouch
import com.hashmato.retailtouch.sync.SyncViewModel
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
    single<retailtouch> { retailtouch.invoke(get()) }
    //DataBaseRepository
    single<SqlRepository> { SqlRepositoryImpl(get()) }
    //ViewModels
    single { SharedPosViewModel() }
    single { HomeViewModel() }

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
    viewModelDefinition { PrinterViewModel() }
    viewModelDefinition { StockViewModel() }
    viewModelDefinition { MemberViewModel() }
}