package com.example.team14_turpakkeliste.data
import ForecastData
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.gson.*
import java.io.IOException
import java.io.InputStream

class Datasource {

    //https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=60.12&lon=9.58
    //"https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=${lat}lon=${lon}"

    //https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/compact?lat=60.12&lon=9.58

    //endre til proxy
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { gson() }
        expectSuccess = true

    }
    suspend fun getForecastData(newlat: Double, newlon: Double): ForecastData {
        val client = client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/compact?lat=${newlat}&lon=${newlon}"){
            headers{
                append("X-Gravitee-API-Key","f1163555-9b8d-49bd-b24f-49e1c756b215")
            }
        }
        return client.body()
    }
    suspend fun getMetAlerts(): String {
        val client = client.get("https://gw-uio.intark.uh-it.no/in2000/weatherapi/metalerts/1.1?lang=no"){
            headers{
                append("X-Gravitee-API-Key","f1163555-9b8d-49bd-b24f-49e1c756b215")
            }
        }
        return client.bodyAsText()
    }

    suspend fun getCurrentAlerts(link: String): String{
        val client = client.get(link){
            headers{
                append("X-Gravitee-API-Key","f1163555-9b8d-49bd-b24f-49e1c756b215")
            }
        }
        return client.bodyAsText()
    }
    suspend fun getAllAlerts(): List<Alert>{
        val response = getMetAlerts()
        val inputStream : InputStream = response.byteInputStream()
        val alerts = XmlForMetAlerts().parse(inputStream)
        val alertListOfList= mutableListOf<List<Alert>>()
        val alertList = mutableListOf<Alert>()

        for (a in alerts) {
            val responseForAlert = getCurrentAlerts(a.link!!)
            val inputStreamForAlert : InputStream = responseForAlert.byteInputStream()
            alertListOfList.add(XmlCurrentAlert().parse(inputStreamForAlert))
        }
        //Kun Alerts med relevant informasjon for oss
        for (alertLists in alertListOfList){
            for (b in alertLists){
                if (b.domain == "land" && b.language == "no"){
                    alertList.add(b)
                }
            }
        }
        return alertList
    }


}