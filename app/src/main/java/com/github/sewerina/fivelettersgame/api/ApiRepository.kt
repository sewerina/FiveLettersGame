package com.github.sewerina.fivelettersgame.api

import WordResponse
import com.github.sewerina.fivelettersgame.api.ApiClient.client
import io.ktor.client.call.body
import io.ktor.client.request.get

class ApiRepository {
    suspend fun getWord(): WordResponse = client.get(ApiRoutes.BASE_URL).body()
}