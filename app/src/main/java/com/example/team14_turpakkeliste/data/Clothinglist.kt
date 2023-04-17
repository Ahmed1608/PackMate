package com.example.team14_turpakkeliste.data

import ForecastData
import kotlin.text.toDoubleOrNull

fun getClothes(): List<Clothing>{
    var clothingList: List<Clothing> = listOf(
        //klær inspirert av stat-system fra ulvang på ullklær andre klær hentet fra Norrøna
        Clothing("Shell", "jacket","outer", 1, 2, 2, "ShellJO"),
        Clothing("Shell", "pants", "outer", 1,2, 2,"ShellPO" ),
        Clothing("Down", "jacket", "outer", 4,1,2, "DownJO"),
        Clothing("Softshell", "jacket", "outer", 2, 1,2,"SoftJO"),
        Clothing("Primaloft", "jacket", "outer",3, 1,2, "PrimaJO"),
        Clothing("Softshell", "pants", "outer", 2,1,2, "SoftPO"),
        Clothing("Wool", "sweater", "inner", 3,1,1,"UllSI"),
        Clothing("Wool", "pants", "innter", 3, 1, 1, "UllPI"),
        Clothing("LightWool", "sweater", "inner", 2,1,1,"LUllSI"),
        Clothing("LightWool", "pants", "inner", 2,1,1,"LUllPI"),
        Clothing("LightWool", "tshirt", "inner", 1, 1,1, "LUllTI")
    )
    return  clothingList
}
fun sortClothing(forecastData: ForecastData): List<Clothing>{
    // Ta imot værdata og få som ouput outerReqMin, outerReqMax, innerReqMin og innerReqMax
    // Legg ved en boolean f.eks som sier om det er nedbør, kan være viktig for valg av klær, dersom man trenger varme, men ikke fra ytterlag.
    // Da verdsettes f.eks vannavstøtende kvaliteter, og et innerlag verdsetter høyere varme.
    //iterere gjennom og samle vann for hver time :D
    val temp: Double = forecastData.properties.timeseries.get(0).data.instant.details.air_temperature.toDouble()
    val wind: Double = forecastData.properties.timeseries.get(0).data.instant.details.wind_speed.toDouble()
    var water: Double = 0.0
    //Bør samle vann for alle tider i døgnet altså instant + next1 + next6 + next12
    forecastData.properties.timeseries.get(0).data.next_1_hours.details?.precipitation_amount?.let {
        water = forecastData.properties.timeseries.get(0).data.next_1_hours.details!!.precipitation_amount.toDouble()
    }
    // evt ha en minimumsvarme som må nås med alle lag sin varme kombinert
    val outerReqMin = chooseReqsOuter(temp, wind, water)
    val innerReqMin = chooseReqsInner(temp)
    // disse to verdiene kan også gjelde for sko/fottøy
    val tempList: MutableList<Clothing> = mutableListOf()
    for(clothing in getClothes()){
        val warmth: Int = clothing.warmth
        val wind: Int = clothing.windproof
        val water: Int = clothing.waterproof

        //trumfer varme dersom det regner
        if(outerReqMin.waterproof == water){
            //tempList.add(clothing)
        }
        //sjekk også for vann her
        //metoden er treig og bør derfor byttes ut med indeksering der vi hopper videre til neste object
        //skippe visse plagg fordi de allerede er brukt opp

        //drite i varme for skalljakke og softshelljakke da disse er beskyttende lag

        //beskyttende lag
        if(warmth == outerReqMin.warmth
            && wind >= outerReqMin.windproof
            && clothing.type == "jacket"
            && clothing.layer == "outer"){
                tempList.add(clothing)
                continue
        }
        //beskyttende lag
        if(warmth >= outerReqMin.warmth
            && wind >= outerReqMin.windproof
            && clothing.type == "pants"
            && clothing.layer == "outer"){
                tempList.add(clothing)
                continue
        }
        //ikke maksgrense for inner, men heller en spesifik == verdi
        //ikke vits å sjekke for vind
        if(warmth == innerReqMin.warmth
            && clothing.type == "sweater"
            && clothing.layer == "inner"){
                tempList.add(clothing)
                continue
        }
        if(warmth == innerReqMin.warmth
            && clothing.type == "pants"
            && clothing.layer == "inner"){
                tempList.add(clothing)
                continue
            }
    }
    return tempList
}
//precipicationamount forteller om nedbør
fun chooseReqsOuter(temp: Double, wind: Double, water: Double?): MinRequirementsClothes{
    var warmth: Int = 0
    var windproof: Int = 0
    var waterproof: Int = 0
    when(temp){
        in -40.0..-21.0 -> warmth = 6
        in -20.9..-11.0 -> warmth = 5
        in -10.9..-1.0 -> warmth = 4
        in 0.9..9.9-> warmth = 3
        in 10.0..19.9-> warmth = 2
        in 20.0..40.9 -> warmth = 1
    }
    println(warmth)
    when(wind){
        //finne ut verdi for hvordan vær tolkes
        in 0.0..2.5 -> windproof = 1
        in 2.6..5.0 -> windproof = 2
        in 5.1..7.5 -> windproof = 3
        in 7.6..10.0 -> windproof = 4
        in 10.1..12.5->windproof = 5
        in 12.6..32.7-> windproof = 6
    }
    println(wind)
    when(water!!){
        in 0.0..0.2 -> waterproof = 1
        in 0.3..0.5 -> waterproof = 2
        in 0.6..0.8 -> waterproof = 3
        in 0.9..1.1 -> waterproof = 4
        in 1.2..1.4 -> waterproof = 5
        in 1.5..50.0-> waterproof = 6
    }
    println(water)
    return MinRequirementsClothes(warmth,waterproof, windproof)
}
fun chooseReqsInner(temp: Double): MinRequirementsClothes{
    var warmth: Int = 0
    when(temp){
        in -40.0..-21.0 -> warmth = 6
        in -20.9..-11.0 -> warmth = 5
        in -10.9..-1.0 -> warmth = 4
        in 0.9..9.9-> warmth = 3
        in 10.0..19.9-> warmth = 2
        in 20.0..40.9 -> warmth = 1
    }
    return MinRequirementsClothes(warmth,1,1)
}
fun getWeather(forecastData: ForecastData): String{
    val temp: String = forecastData.properties.timeseries.get(0).data.instant.details.air_temperature.toString()
    val wind: String = forecastData.properties.timeseries.get(0).data.instant.details.wind_speed.toString()
    var water = "0.0"
    forecastData.properties.timeseries.get(0).data.next_1_hours.details?.precipitation_amount?.let {
        water = forecastData.properties.timeseries.get(0).data.next_1_hours.details!!.precipitation_amount.toString()
    }
    val returnString = "Det er meldt ${temp} grader \nog vind på ${wind} m/s \nDu kan forvente ${water} mm nedbør"
    return returnString
}