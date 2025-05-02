package com.example.mobilardennes.data

import com.example.mobilardennes.network.CyclamApiService
import com.example.mobilardennes.network.CyclamApiStationService
import com.example.mobilardennes.network.CyclamApiVehiculesService
import com.example.mobilardennes.network.FluoApiService
import com.example.mobilardennes.network.FluoStopsApiService
import com.example.mobilardennes.network.FluoStopsHoursService
import com.example.mobilardennes.network.FluoInstantStopsHoursService
import com.example.mobilardennes.network.FluoLineStopsHoursService
import com.example.mobilardennes.network.FluoStopsOperatorService
import com.example.mobilardennes.network.SncfApiStationService
import com.example.mobilardennes.network.SncfApiStationService2
import com.example.mobilardennes.network.TestApiService
// import com.example.mobilardennes.network.CyclamStationApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit

interface AppContainer {
    val cyclamStationsRepository: CyclamStationsRepository
    val sncfRepository: SncfRepository
    val sncfRepository2: SncfRepository2
    val fluoLinesRepository: FluoTacRepository
    // val fluoLinesStopsRepository: FluoTacRepository
    val fluoInstantLinesRepository: FluoTacInstantRepository
    val testRepository: TestRepository
}


class DefaultAppContainer : AppContainer {
    private val baseUrlCyclam =
        "https://api.cyclist.ecovelo.mobi"
    // url_station = "https://api.cyclist.ecovelo.mobi/2023_10_02/vehicules?dockless=false&program=cyclam&station="

    private val baseUrlSncf =
        //"https://www.francetravail.fr" // "https://www.garesetconnexions.sncf" // schedule-table/Departures/0087172007"
        "https://www.garesetconnexions.sncf"

    private val baseUrlGoogle =
        "https://www.google.com" // schedule-table/Departures/0087172007"

    private val baseUrlFluo =
        "https://api.grandest2.cityway.fr"

    private val baseUrlFluoInstant =
        "https://fluo.api.instant-system.com"

    private val baseUrlTest =
        //"https://www.infoclimat.fr"
        "https://www.garesetconnexions.sncf"
        //"https://www.google.com"


    //private val networkTestJson = Json { ignoreUnknownKeys = true}
    private val retrofitTest = Retrofit.Builder()
        //.addConverterFactory( //networkTestJson.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(baseUrlTest)
        .client(okhttpClient("rien").build())
        .build()

    private val retrofitTestService : TestApiService by lazy {
        retrofitTest.create(TestApiService::class.java)

    }

    override val testRepository: TestRepository by lazy {
        NetworkTestRepository(retrofitTestService)
    }



    private val networkJson = Json { ignoreUnknownKeys = true}
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrlCyclam)
        .build()

    private val retrofitService : CyclamApiService by lazy {
        retrofit.create(CyclamApiService::class.java)

    }


    private val retrofitService2 : CyclamApiStationService by lazy {

        retrofit.create(CyclamApiStationService::class.java)

    }

    private val retrofitServiceVehicules : CyclamApiVehiculesService by lazy {

        retrofit.create(CyclamApiVehiculesService::class.java)

    }


    override val cyclamStationsRepository: CyclamStationsRepository by lazy {
        NetworkCyclamStationsRepository(retrofitService, retrofitService2, retrofitServiceVehicules)
    }


    private val networkJsonSncf = Json { ignoreUnknownKeys = true}
    private val retrofitSncf =  Retrofit.Builder()
        .addConverterFactory(networkJsonSncf.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrlSncf)
        .client(okhttpClient("rien").build())
        .build()

    private val retrofitServiceSncf : SncfApiStationService by lazy {
        retrofitSncf.create( SncfApiStationService::class.java)

    }

    private val retrofitSncf2 = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(baseUrlSncf)
        .client(okhttpClient("rien").build())
        .build()

    private fun okhttpClient(apiKey: String) = OkHttpClient().newBuilder()
        .addInterceptor(
            object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:137.0) Gecko/20100101 Firefox/137.0")
                        //.header("Accept", "application/json")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .header("Referer", "https://www.garesetconnexions.sncf/fr/gares-services/charleville-mezieres/horaires")
                        .header("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
                        //.header("Accept-Encoding", "gzip, deflate, br, zstd")
                        .header("Priority",	"u=0, i")
                        .header("DNT", "1")
                        .header("Host", "www.garesetconnexions.sncf")
                        .header("Alt-Used", "www.garesetconnexions.sncf")
                        //.header("Connection", "keep-alive")
                       // .header("Sec-Fetch-Dest", "document")
                        .header("Sec-Fetch-Dest", "document")
                        //.header("Sec-Fetch-Mode", "navigate")
                        .header("Sec-Fetch-Mode", "no-cors")
                        //.header("Sec-Fetch-Site", "none")
                        .header("Sec-Fetch-Site", "cross-site")
                        .header("TE", "trailers")
                        //.header("Sec-Fetch-User", "?1")
                        .header("Upgrade-Insecure-Requests", "1")
                        .build()
                    return chain.proceed(request)
                }
            }
        )
    private fun okhttpClient2(apiKey: String) = OkHttpClient().newBuilder()
        .addInterceptor(
            object: Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:137.0) Gecko/20100101 Firefox/137.0")
                        //.header("Accept", "application/json")
                        .header("Accept", "*/*")
                        // .header("Referer", "https://www.garesetconnexions.sncf/fr/gares-services/charleville-mezieres/horaires")
                        .header("Referer", "https://services.fluo.grandest.fr/")
                        .header("Accept-Language", "fr")
                        .header("Origin", "https://services.fluo.grandest.fr",)
                        //.header("Accept-Encoding", "gzip, deflate, br, zstd")
                        .header("DNT", "1")
                        //.header("Host", "www.garesetconnexions.sncf")
                        //.header("Connection", "keep-alive")
                        .header("Sec-Fetch-Dest", "empty")
                        .header("Sec-Fetch-Mode", "cors")
                        .header("Sec-Fetch-Site", "cross-site")
                        //.header("TE", "trailers")
                        //.header("Upgrade-Insecure-Requests", "1")
                        .header("Priority", "u=4")
                        .header("X-Api-Key","1b6338fcd461ab9ea7c039903295fa69")
                        .build()
                    return chain.proceed(request)
                }
            }
        )
// @Header("User-Agent") user_agent: String ="Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0",
    // @Header("Accept") accept: String ="application/json",
    //        'Accept-Language': 'fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3',\n" +
    //        # 'Accept-Encoding': 'gzip, deflate, br, zstd',\n" +
    // @Header("Referer") referer: String ="https://www.garesetconnexions.sncf/fr/gares-services/charleville-mezieres/horaires",
    //        'DNT': '1',\n" +
    //        'Connection': 'keep-alive',\n" +
    //        # 'Cookie': 'burguillos=; iadvize-6911-vuid=^%^7B^%^22vuid^%^22^%^3A^%^22b806f63e928f41f9a164fa6659867aa0d45d609ef7294^%^22^%^2C^%^22deviceId^%^22^%^3A^%^2276efc0c8-c875-48f8-bc7a-88ad2738f9f0^%^22^%^7D; datadome=oA9PyUamT61KOU4lARtUlYoCkw1AgasXTE1uUDHXxnyc6AZN16x3EQko46haPdCmGtxW_i3vkrV37VHNG94MO6KdL0iTS4a1ovMKyn7ZStUqQkYR3nDKX65LRT95lmsq; _dblcmUserId=ds49avbvgcq:1715007572419; CookieConsent={stamp:^%^27GXLEZNpcCIEMd4NrM4ihRght7IHEI0NZQdAy/eqI/UGNmkhm3DCmxA==^%^27^%^2Cnecessary:true^%^2Cpreferences:false^%^2Cstatistics:false^%^2Cmarketing:false^%^2Cmethod:^%^27explicit^%^27^%^2Cver:2^%^2Cutc:1715075569034^%^2Cregion:^%^27fr^%^27}; AWSALB=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; AWSALBCORS=yS6k+vja6jLDxESt2bF06VW9BBOKZfTHDyYEVAagTj1pPcmtVzn6IPBeDuuQn9xwuS7UplvUvHqfFC1HvwM3UdwnVu9G/tLzWTx+hGNX/P4zvgCUpxu6JZ3ReshC; GCO=AWS_PRD6',\n" +
    //        'Sec-Fetch-Dest': 'empty',\n" +
    //        'Sec-Fetch-Mode': 'cors',\n" +
    //        'Sec-Fetch-Site': 'same-origin',\n" +


    private val retrofitService2Sncf : SncfApiStationService2 by lazy {

        retrofitSncf2.create(SncfApiStationService2::class.java)

    }


    override val sncfRepository: SncfRepository by lazy {
        NetworkSncfRepository(retrofitServiceSncf)
    }


    override val sncfRepository2: SncfRepository2 by lazy {
        NetworkSncfRepository2(retrofitService2Sncf)
    }

    private val networkJsonFluo = Json { ignoreUnknownKeys = true}
    private val retrofitFluo = Retrofit.Builder()
        .addConverterFactory(networkJsonFluo.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrlFluo)
        .build()

    private val retrofitServiceFluo : FluoApiService by lazy {
        retrofitFluo.create(FluoApiService::class.java)

    }


    private val retrofitServiceFluoStops : FluoStopsApiService by lazy {
        retrofitFluo.create(FluoStopsApiService::class.java)

    }

    private val retrofitServiceFluoStopsOperator : FluoStopsOperatorService by lazy {
        retrofitFluo.create(FluoStopsOperatorService::class.java)

    }

    private val retrofitServiceFluoStopsHours : FluoStopsHoursService by lazy {
        retrofitFluo.create(FluoStopsHoursService::class.java)

    }

    private val retrofitServiceFluoLineStopsHours : FluoLineStopsHoursService by lazy {
        retrofitFluo.create(FluoLineStopsHoursService::class.java)

    }
/*
    private val retrofitServiceFluoStopsHours2 : FluoStopsHoursService2 by lazy {
        retrofitFluo.create(FluoStopsHoursService2::class.java)

    }
*/
    override val fluoLinesRepository: FluoTacRepository by lazy {
        NetworkFluoTacRepository(retrofitServiceFluo, retrofitServiceFluoStops, retrofitServiceFluoStopsOperator, retrofitServiceFluoStopsHours, retrofitServiceFluoLineStopsHours) //, retrofitServiceFluoStopsHours2)
    }



    private val networkJsonFluoInstant = Json { ignoreUnknownKeys = true}
    private val retrofitFluoInstant = Retrofit.Builder()
        .addConverterFactory(networkJsonFluoInstant.asConverterFactory("application/json".toMediaType()))
        .client(okhttpClient2("rien").build())
        .baseUrl(baseUrlFluoInstant)
        .build()

    private val retrofitServiceFluoStopsHoursInstant : FluoInstantStopsHoursService by lazy {
        retrofitFluoInstant.create(FluoInstantStopsHoursService::class.java)

    }

    override val fluoInstantLinesRepository: FluoTacInstantRepository by lazy {
        NetworkFluoTacInstantRepository(retrofitServiceFluoStopsHoursInstant)
    }
}
