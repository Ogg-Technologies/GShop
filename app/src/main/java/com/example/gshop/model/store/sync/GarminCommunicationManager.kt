package com.example.gshop.model.store.sync

import com.example.gshop.App
import com.example.gshop.model.utilities.monads.GResult
import com.example.gshop.model.utilities.monads.suspendedGResult
import com.garmin.android.connectiq.ConnectIQ

object GarminCommunicationManager {
    private var cachedCommunication: GarminCommunication? = null

    suspend fun getCommunication(onCommunicationSetup: suspend () -> Unit): GResult<GarminCommunication, String> =
        suspendedGResult {
            return@suspendedGResult GResult.Ok(cachedCommunication ?: run {
                onCommunicationSetup()
                val com = createCommunication().bind()
                cachedCommunication = com
                com
            })
        }

    private suspend fun createCommunication() =
        ConnectIQ.getInstance(App.context, ConnectIQ.IQConnectType.WIRELESS)
            .setupCommunication(App.context, GARMIN_APP_ID, 1000)

}