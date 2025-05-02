package com.example.mobilardennes.network

import com.example.mobilardennes.model.NestedCyclamStation
import com.example.mobilardennes.model.NestedCyclamStationVehicules
import com.example.mobilardennes.model.SncfTrainData
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query


interface SncfApiStationService {
    @GET("schedule-table/{sens}/{station}")
    // @GET("photos")
    suspend fun getSncf(
        // @Header("User-Agent")
        // user_agent: String ="Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0",
        // 'Accept': 'application/json',
               //        'Accept-Language': 'fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3',
                //        # 'Accept-Encoding': 'gzip, deflate, br, zstd',
        // 'Referer' : 'https://www.garesetconnexions.sncf/fr/gares-services/charleville-mezieres/horaires',
                //        'DNT': '1'
                //        'Connection': 'keep-alive',
               //        'Cookie': 'burguillos=; iadvize-6911-vuid=^%^7B^%^22vuid^%^22^%^3A^%^22b806f63e928f41f9a164fa6659867aa0d45d609ef7294^%^22^%^2C^%^22deviceId^%^22^%^3A^%^2276efc0c8-c875-48f8-bc7a-88ad2738f9f0^%^22^%^7D; datadome=oA9PyUamT61KOU4lARtUlYoCkw1AgasXTE1uUDHXxnyc6AZN16x3EQko46haPdCmGtxW_i3vkrV37VHNG94MO6KdL0iTS4a1ovMKyn7ZStUqQkYR3nDKX65LRT95lmsq; _dblcmUserId=ds49avbvgcq:1715007572419; CookieConsent={stamp:^%^27GXLEZNpcCIEMd4NrM4ihRght7IHEI0NZQdAy/eqI/UGNmkhm3DCmxA==^%^27^%^2Cnecessary:true^%^2Cpreferences:false^%^2Cstatistics:false^%^2Cmarketing:false^%^2Cmethod:^%^27explicit^%^27^%^2Cver:2^%^2Cutc:1715075569034^%^2Cregion:^%^27fr^%^27}; AWSALB=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; AWSALBCORS=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; GCO=AWS_PRD6',
                //        'Sec-Fetch-Dest': 'empty',
                //        'Sec-Fetch-Mode': 'cors',
               //        'Sec-Fetch-Site': 'same-origin',

        @Path("sens") sens: String?,
        @Path("station") station: String?
    ): List<SncfTrainData>
}

interface SncfApiStationService2 {
   //@GET("search")
   @GET("schedule-table/{sens}/{station}")

    // @GET("photos")
    suspend fun getSncf2(
        // @Header("User-Agent")
        // user_agent: String ="Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0",
        // 'Accept': 'application/json',
        //        'Accept-Language': 'fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3',
        //        # 'Accept-Encoding': 'gzip, deflate, br, zstd',
        // 'Referer' : 'https://www.garesetconnexions.sncf/fr/gares-services/charleville-mezieres/horaires',
        //        'DNT': '1'
        //        'Connection': 'keep-alive',
        //        'Cookie': 'burguillos=; iadvize-6911-vuid=^%^7B^%^22vuid^%^22^%^3A^%^22b806f63e928f41f9a164fa6659867aa0d45d609ef7294^%^22^%^2C^%^22deviceId^%^22^%^3A^%^2276efc0c8-c875-48f8-bc7a-88ad2738f9f0^%^22^%^7D; datadome=oA9PyUamT61KOU4lARtUlYoCkw1AgasXTE1uUDHXxnyc6AZN16x3EQko46haPdCmGtxW_i3vkrV37VHNG94MO6KdL0iTS4a1ovMKyn7ZStUqQkYR3nDKX65LRT95lmsq; _dblcmUserId=ds49avbvgcq:1715007572419; CookieConsent={stamp:^%^27GXLEZNpcCIEMd4NrM4ihRght7IHEI0NZQdAy/eqI/UGNmkhm3DCmxA==^%^27^%^2Cnecessary:true^%^2Cpreferences:false^%^2Cstatistics:false^%^2Cmarketing:false^%^2Cmethod:^%^27explicit^%^27^%^2Cver:2^%^2Cutc:1715075569034^%^2Cregion:^%^27fr^%^27}; AWSALB=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; AWSALBCORS=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; GCO=AWS_PRD6',
        //        'Sec-Fetch-Dest': 'empty',
        //        'Sec-Fetch-Mode': 'cors',
        //        'Sec-Fetch-Site': 'same-origin',


        @Path("sens") sens: String?,
        @Path("station") station: String?
        //@Query("client") client: String? = "firefox-b-d",
        //@Query("q") q: String? = "ibu+biathlon"
    ): String
}

interface TestService {
    //@GET("schedule-table/{sens}/{station}")

    @GET("photos")
    suspend fun getSncf2(
        // @Header("User-Agent")
        // user_agent: String ="Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0",
        // 'Accept': 'application/json',
        //        'Accept-Language': 'fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3',
        //        # 'Accept-Encoding': 'gzip, deflate, br, zstd',
        // 'Referer' : 'https://www.garesetconnexions.sncf/fr/gares-services/charleville-mezieres/horaires',
        //        'DNT': '1'
        //        'Connection': 'keep-alive',
        //        'Cookie': 'burguillos=; iadvize-6911-vuid=^%^7B^%^22vuid^%^22^%^3A^%^22b806f63e928f41f9a164fa6659867aa0d45d609ef7294^%^22^%^2C^%^22deviceId^%^22^%^3A^%^2276efc0c8-c875-48f8-bc7a-88ad2738f9f0^%^22^%^7D; datadome=oA9PyUamT61KOU4lARtUlYoCkw1AgasXTE1uUDHXxnyc6AZN16x3EQko46haPdCmGtxW_i3vkrV37VHNG94MO6KdL0iTS4a1ovMKyn7ZStUqQkYR3nDKX65LRT95lmsq; _dblcmUserId=ds49avbvgcq:1715007572419; CookieConsent={stamp:^%^27GXLEZNpcCIEMd4NrM4ihRght7IHEI0NZQdAy/eqI/UGNmkhm3DCmxA==^%^27^%^2Cnecessary:true^%^2Cpreferences:false^%^2Cstatistics:false^%^2Cmarketing:false^%^2Cmethod:^%^27explicit^%^27^%^2Cver:2^%^2Cutc:1715075569034^%^2Cregion:^%^27fr^%^27}; AWSALB=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; AWSALBCORS=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; GCO=AWS_PRD6',
        //        'Sec-Fetch-Dest': 'empty',
        //        'Sec-Fetch-Mode': 'cors',
        //        'Sec-Fetch-Site': 'same-origin',

        //@Path("sens") sens: String?,
        //@Path("station") station: String?
    ): String
}