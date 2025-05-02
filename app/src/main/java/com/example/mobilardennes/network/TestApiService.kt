package com.example.mobilardennes.network


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TestApiService {
    @GET("{chaine}")
    // @GET("photos")
    suspend fun getTestExample1(
        @Path("chaine") chaine: String?,
        //@Query("program") program: String
    ): String //List<NestedCyclamStation>
}