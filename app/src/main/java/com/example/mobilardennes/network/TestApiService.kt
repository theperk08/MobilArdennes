package com.example.mobilardennes.network


import retrofit2.http.GET
import retrofit2.http.Path


interface TestApiService {
    @GET("{chaine}")
    suspend fun getTestExample1(
        @Path("chaine") chaine: String?,
    ): String 
}