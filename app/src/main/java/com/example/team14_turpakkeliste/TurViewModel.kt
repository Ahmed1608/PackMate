package com.example.team14_turpakkeliste


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team14_turpakkeliste.data.*
import com.example.team14_turpakkeliste.ui.TurpakklisteUiState
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException

//satt til Oslo. Ta med i Kommentar
class TurViewModel: ViewModel() {
    var isOffline : Boolean = false
    var prevScreen : String = "SavedScreen"
    var error : String? = null

    var currentLatitude : Double = 59.9
    var currentLongitude : Double = 10.7
    lateinit var alerts: List<Alert>
    var numberOfDays : Int = 0
    var chosenDay: Int = 0
    lateinit var outerLayerList: List<Clothing>
    lateinit var innerLayerList: List<Clothing>
    lateinit var weatherInfo: WeatherValues
    lateinit var weatherImg: String
    lateinit var location: String

    var turUiState: TurpakklisteUiState by mutableStateOf(TurpakklisteUiState.Booting)
        private set
    private val source: Datasource = Datasource()
    init {
        getData()
    }

    fun checkIntitialized(): String{
        return if(this::location.isInitialized){
            location
        } else{
            "${currentLatitude}, $currentLongitude"
        }
    }

    fun updateDays(days: Int){

        numberOfDays = days

    }
    fun getForecast(){
        viewModelScope.launch {

            turUiState = try {
                val forecast = source.getForecastData(currentLatitude, currentLongitude)
                TurpakklisteUiState.Success(alerts, forecast)
            } catch (ex: ResponseException) {
                error = ex.toString()
                TurpakklisteUiState.Error
            } catch (ex: SerializationException) {
                error = ex.toString()
                TurpakklisteUiState.Error
            } catch(e:Throwable){
                error = e.toString()
                TurpakklisteUiState.Error
            }
        }
    }

    fun getAlertDataForArea(): Triple<String, String, String>?{
        var alertColor = "green"
        var alertType = ""
        var alertdescription = ""
        for(alert in alerts){
            if(pinpointLocation(currentLatitude,currentLongitude,alert.areaPolygon!!)){
                val string= alert.awareness_level?.split(";")
                val awarenesslevel = string?.get(1)?.trim()
                val typeString = alert.awareness_type?.split(";")
                val typeStringsplit1 = typeString?.get(1)?.split("-")
                val awarenesstype = typeStringsplit1?.get(0)?.trim()
                if(awarenesslevel == "red"){
                    alertColor = "red"
                    alertType = awarenesstype.toString()
                    alertdescription = alert.description.toString()
                    break
                }
                //warningen som dukker opp her stemmer ikke. AlertColor != "red" kan bli false.
                if(alertColor != "red" && awarenesslevel == "orange"){
                    alertColor = "orange"
                    alertType = awarenesstype.toString()
                    alertdescription = alert.description.toString()
                }
                if(awarenesslevel == "yellow" && alertColor == "green"){
                    alertColor = "yellow"
                    alertType = awarenesstype.toString()
                    alertdescription = alert.description.toString()
                }
            }
        }
        return if (alertColor == "green"){
            null
        } else {
            Triple(alertType, alertColor, alertdescription)
        }
    }

    private fun getData() {
        viewModelScope.launch {
            turUiState = try {
                val alertList = source.getAllAlerts()
                alerts = alertList
                val forecast = source.getForecastData(currentLatitude,currentLongitude)
                TurpakklisteUiState.Success(alertList, forecast)
            } catch (ex: ResponseException) {
                error = ex.toString()
                isOffline = true
                TurpakklisteUiState.OfflineMode
            } catch (ex: SerializationException) {
                error = ex.toString()
                isOffline = true
                TurpakklisteUiState.OfflineMode
            } catch(e:Throwable){
                error = e.toString()
                isOffline = true
                TurpakklisteUiState.OfflineMode
            }
        }
    }
}
