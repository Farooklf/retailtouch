package com.lfssolutions.retialtouch.domain


import com.lfssolutions.retialtouch.data.remote.api.TOKEN_EXPIRY_THRESHOLD
import com.lfssolutions.retialtouch.utils.DateTime.getCurrentDateAndTimeInEpochMilliSeconds
import com.lfssolutions.retialtouch.utils.DateTime.getHoursDifferenceFromEpochMillSeconds
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object ApiUtils : KoinComponent {

    // Inject PreferencesRepository and ApiService using Koin
    private val preferences: PreferencesRepository by inject()
    private val apiService: ApiService by inject()


    private suspend fun getBaseUrl(): String {
        return preferences.getBaseURL().first()  // Collects the first emitted value from Flow
    }

    private suspend fun getBearerToken(): String {
        return "Bearer ${preferences.getToken().first()}"
        /*return if (isTokenExpired())
            "Bearer ${refreshToken()}"
        else
            "Bearer ${preferences.getToken().first()}"*/
    }

    private suspend fun isTokenExpired() : Boolean {
        val tokenTime = preferences.getTokenTime().first()
        val currentTime = getCurrentDateAndTimeInEpochMilliSeconds()
        val hoursPassed = getHoursDifferenceFromEpochMillSeconds(tokenTime, currentTime)
        return hoursPassed > TOKEN_EXPIRY_THRESHOLD
    }

   /* private suspend fun refreshToken(): String {
        return runBlocking {
            try {
                var result=""
                withContext(Dispatchers.IO) {
                    apiService.hitLoginAPI(getLoginDetails()).collect{response->
                        when(response){
                            is RequestState.Success -> {
                                val token=response.data.result
                                preferences.setToken(token?:"")
                                result=token?:""
                            }
                            else ->{
                            }
                        }
                    }
                    result
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }*/

   /* private suspend fun getLoginDetails(): LoginRequest {
        var loginRequest = LoginRequest()
        databaseRepository.selectUserByUserId(preferences.getUserId().first())
            .collect { authDao ->
                loginRequest = LoginRequest(
                    usernameOrEmailAddress = authDao.userName,
                    tenancyName = authDao.tenantName,
                    password = authDao.password,
                )
            }
        return loginRequest

    }*/

    fun <T> performApiRequestWithBaseUrl(
        httpClient: HttpClient,
        apiUrl: String,
        requestBody: Any,
        handleResponse: suspend (HttpResponse) -> RequestState<T>
    ): Flow<RequestState<T>> {
        return performApiRequest(
            httpClient = httpClient,
            apiUrl = apiUrl,
            requestBody = requestBody,
            handleResponse = handleResponse
        )
    }

    private inline fun <T> performApiRequest(
        httpClient: HttpClient,
        apiUrl: String,
        requestBody: Any,
        crossinline handleResponse: suspend (HttpResponse) -> RequestState<T>
    ): Flow<RequestState<T>> = flow {
        emit(RequestState.Loading)  // Emit loading state
        try {
            val token = getBearerToken()  // Get the token
            val response = httpClient.post(getBaseUrl() + apiUrl) {
                setBody(requestBody)
                headers {
                    append(ContentType, io.ktor.http.ContentType.Application.Json)
                    append("Authorization", token)
                }
            }
            emit(handleResponse(response))  // Handle the API response
        } catch (e: Exception) {
            emit(handleException(e))  // Handle exceptions
        }
    }

    suspend inline fun <reified T : Any> handleApiResponse(response: HttpResponse): RequestState<T> {
        return try {
            when (response.status) {
                HttpStatusCode.OK -> RequestState.Success(response.body<T>())
                HttpStatusCode.Unauthorized-> RequestState.Error("Current user did not login to the application!")
                HttpStatusCode.BadRequest,
                HttpStatusCode.InternalServerError,
                HttpStatusCode.NotFound -> RequestState.Error(response.bodyAsText())
                else -> RequestState.Error("Unexpected status code: ${response.status}")
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    fun handleException(e: Exception): RequestState.Error {
        val errorMessage = when (e) {
            is ClientRequestException -> "Client error: ${e.response.status.description}"  // 4xx errors
            is ServerResponseException -> "Server error: ${e.response.status.description}"  // 5xx errors
            is ConnectTimeoutException, is SocketTimeoutException -> "Request timed out"
            is UnresolvedAddressException -> "No internet connection or invalid URL"
            is IOException -> "Network error: ${e.message}"
            else -> e.message ?: "Unknown error"
        }
        return RequestState.Error(errorMessage)
    }


    suspend fun <T> observeResponse(
        apiResponse: Flow<RequestState<T>>,
        onLoading: () -> Unit = { println("Loading...") },
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit = { errorMsg -> println("Error: $errorMsg") }
    ) {
        apiResponse.collect { state ->
            when (state) {
                is RequestState.Error -> {
                    onError(state.getErrorMessage())
                }
                is RequestState.Idle -> {}
                is RequestState.Loading -> {
                    onLoading()
                }
                is RequestState.Success -> {
                    val data = state.getSuccessData()
                    onSuccess(data)
                }
            }
        }
    }

    // Define a generic function to handle RequestState
    fun <T> observeResponseNew(
        requestState: RequestState<T>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit = { /* Handle error */ },
        onLoading: () -> Unit = { /* Handle loading */ },
        onIdle: () -> Unit = { /* Handle idle */ }
    ) {
        when (requestState) {
            is RequestState.Error -> onError(requestState.getErrorMessage())
            is RequestState.Idle -> onIdle()
            is RequestState.Loading -> onLoading()
            is RequestState.Success -> onSuccess(requestState.getSuccessData())
        }
    }



}