package com.example.team14_turpakkeliste.ui

import ForecastData
import com.example.team14_turpakkeliste.EntityClass.Pakkliste
import com.example.team14_turpakkeliste.data.Alert

sealed interface TurpakklisteUiState {
    data class Success(val alerts: List<Alert>, var forecastData: ForecastData) : TurpakklisteUiState
    object Error : TurpakklisteUiState
    object Loading : TurpakklisteUiState

    object Booting : TurpakklisteUiState
}