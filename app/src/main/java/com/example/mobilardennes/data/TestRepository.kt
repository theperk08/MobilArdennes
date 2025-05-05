package com.example.mobilardennes.data

import com.example.mobilardennes.network.TestApiService

interface TestRepository {
    suspend fun getTest1(chaine: String?): String //List<CyclamData>

}

class NetworkTestRepository(
    private val testApiService: TestApiService,

    ) : TestRepository {
     override suspend fun getTest1(chaine: String?): String = testApiService.getTestExample1(chaine=chaine)
}
