package com.example.gshop.model.store.sync

import android.content.Context
import com.example.gshop.model.utilities.GResult
import com.example.gshop.model.utilities.flatMap
import com.example.gshop.model.utilities.suspenedGResult
import com.example.gshop.model.utilities.withTimeoutOrDefault
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


private suspend fun ConnectIQ.initialize(
    context: Context,
    timeoutMillis: Long? = null,
): GResult<Unit, String> = withTimeoutOrDefault(
    timeMillis = timeoutMillis, default = GResult.Err("Timed out when initializing ConnectIQ")
) {
    suspendCancellableCoroutine { continuation ->
        initialize(context, false, object : ConnectIQ.ConnectIQListener {
            override fun onSdkShutDown() {}
            override fun onSdkReady() {
                continuation.resume(GResult.Ok(Unit))
            }

            override fun onInitializeError(errorStatus: ConnectIQ.IQSdkErrorStatus?) =
                continuation.resume(GResult.Err("Failed to initialize SDK with error ${errorStatus.toString()}"))
        })
    }
}

data class GarminCommunication(val connectIQ: ConnectIQ, val iqDevice: IQDevice, val iqApp: IQApp)

suspend fun ConnectIQ.setupCommunication(
    context: Context, garminAppId: String, timeoutMillis: Long? = null
): GResult<GarminCommunication, String> =
    suspenedGResult<GarminCommunication, String> {
        val device = connectToDevice(context, timeoutMillis).bind()
        val app = connectToApp(device, garminAppId, timeoutMillis).bind()
        GResult.Ok(GarminCommunication(this@setupCommunication, device, app))
    }

private suspend fun ConnectIQ.connectToDevice(
    context: Context, timeoutMillis: Long? = null
): GResult<IQDevice, String> = initialize(context, timeoutMillis).flatMap { getDevice() }

/**
 * Requires the ConnectIQ to be initialized first.
 */
private fun ConnectIQ.getDevice(): GResult<IQDevice, String> {
    val knownDevices = try {
        knownDevices
    } catch (e: Exception) {
        return GResult.Err("Could not get known devices: ${e.message}")
    }
    val device = knownDevices.firstOrNull() ?: return GResult.Err("No known devices")
    val deviceStatus = getDeviceStatus(device)
    return if (deviceStatus == IQDevice.IQDeviceStatus.CONNECTED) {
        GResult.Ok(device)
    } else {
        GResult.Err("Device is not connected, device status: $deviceStatus")
    }
}

private suspend fun ConnectIQ.connectToApp(
    device: IQDevice, garminAppId: String, timeoutMillis: Long? = null
) = withTimeoutOrDefault(
    timeMillis = timeoutMillis, default = GResult.Err("Timed out when connecting to app")
) {
    suspendCancellableCoroutine<GResult<IQApp, String>> { continuation ->
        getApplicationInfo(garminAppId, device, object : ConnectIQ.IQApplicationInfoListener {
            override fun onApplicationInfoReceived(iqApp: IQApp) =
                continuation.resume(GResult.Ok(iqApp))

            override fun onApplicationNotInstalled(error: String?) =
                continuation.resume(GResult.Err("IQApp is not installed: $error"))
        })
    }
}

suspend fun GarminCommunication.getGarminResponseFromPrompt(
    promptMessage: Any, timeoutMillis: Long? = null
): GResult<Any, String> = withTimeoutOrDefault(
    timeMillis = timeoutMillis, default = GResult.Err("Timed out when getting response from prompt")
) {
    val deferredResponse = getGarminResponseAsync()
    val sentMessageStatus = sendMessageToWatchForStatus(promptMessage, timeoutMillis)
    val response = deferredResponse.await()
    if (sentMessageStatus.isOk()) {
        response
    } else {
        GResult.Err("Failed to get Garmin response from prompt")
    }
}

fun GarminCommunication.getGarminResponseAsync(): Deferred<GResult<Any, String>> {
    val deferred: CompletableDeferred<GResult<Any, String>> = CompletableDeferred()
    connectIQ.registerForAppEvents(iqDevice, iqApp) { _, _, responseList, iqMessageStatus ->
        if (!iqMessageStatus.isSuccess()) {
            deferred.complete(GResult.Err("Failed to get Garmin response from prompt with message: $iqMessageStatus"))
        }
        val response = responseList.firstOrNull()
            ?: deferred.complete(GResult.Err("Received an empty response list"))
        deferred.complete(GResult.Ok(response))
    }
    return deferred
}

suspend fun GarminCommunication.sendMessageToWatchForStatus(
    message: Any, timeoutMillis: Long? = null
): GResult<Unit, String> = withTimeoutOrDefault(
    timeMillis = timeoutMillis,
    default = GResult.Err("Timed out waiting for messageStatus after sending message to watch")
) {
    suspendCancellableCoroutine { continuation ->
        connectIQ.sendMessage(iqDevice, iqApp, message) { _, _, iqMessageStatus ->
            if (iqMessageStatus.isSuccess()) {
                continuation.resume(GResult.Ok(Unit))
            } else {
                continuation.resume(GResult.Err("Error sending message to watch: $iqMessageStatus"))
            }
        }
    }
}

private fun ConnectIQ.IQMessageStatus?.isSuccess() = this == ConnectIQ.IQMessageStatus.SUCCESS

fun ConnectIQ.sendMessageToWatch(device: IQDevice, iqApp: IQApp, message: Any) =
    sendMessage(device, iqApp, message) { _, _, _ -> }
