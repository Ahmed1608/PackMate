package com.example.team14_turpakkeliste.ui


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
import java.io.InputStream

class TurViewModel(): ViewModel() {

    var currentLatitude : Double = 0.0
    var currentLongitude : Double = 0.0

    var turUiState: TurpakklisteUiState by mutableStateOf(TurpakklisteUiState.Booting)
        private set
    private val source: Datasource = Datasource()

    init {
        getData()
    }
    fun getForecast(){
        viewModelScope.launch {
            val alertList = source.getAllAlerts()
            val forecast = source.getForecastData(currentLatitude, currentLongitude)
            turUiState = TurpakklisteUiState.Success(alertList, forecast)
        }
    }

    private fun getData() {
        viewModelScope.launch {
            turUiState = try {
                val alertList = source.getAllAlerts()
                val forecast = source.getForecastData(currentLatitude,currentLongitude)
                TurpakklisteUiState.Success(alertList, forecast)
            } catch (ex: ResponseException) {
                TurpakklisteUiState.Error
            } catch (ex: SerializationException) {
                TurpakklisteUiState.Error
            }catch (ex: Exception) {
                TurpakklisteUiState.Error
            }
        }
    }
}
