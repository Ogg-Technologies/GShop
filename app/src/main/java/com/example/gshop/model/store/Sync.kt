package com.example.gshop.model.store

import android.content.Context
import com.example.gshop.App
import com.example.gshop.model.utilities.GResult
import com.example.gshop.model.utilities.flatMap
import com.example.gshop.model.utilities.withTimeoutOrDefault
import com.example.gshop.redux.AsyncThunk
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun syncWithWatch() = AsyncThunk { state, dispatch ->
    val connectIQ = ConnectIQ.getInstance(App.context, ConnectIQ.IQConnectType.WIRELESS)
    val deviceResult = connectIQ.connectToDevice(App.context)
    if (deviceResult.isErr()) {
        println(deviceResult.error)
        return@AsyncThunk
    }
    val device = deviceResult.value
}

private suspend fun ConnectIQ.connectToDevice(
    context: Context,
): GResult<IQDevice, String> =
    initialize(context, timeoutMillis = 5000).flatMap { getDevice() }

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
    return if (device.status == IQDevice.IQDeviceStatus.CONNECTED) {
        GResult.Ok(device)
    } else {
        GResult.Err("Device is not connected, device status: ${device.status}")
    }
}

private suspend fun ConnectIQ.initialize(
    context: Context,
    timeoutMillis: Long? = null,
): GResult<Unit, String> = withTimeoutOrDefault(
    timeMillis = timeoutMillis,
    default = GResult.Err("Timed out when initializing ConnectIQ")
) {
    suspendCoroutine { continuation ->
        initialize(context, false, object : ConnectIQ.ConnectIQListener {
            override fun onSdkShutDown() {}
            override fun onSdkReady() = continuation.resume(GResult.Ok(Unit))
            override fun onInitializeError(errorStatus: ConnectIQ.IQSdkErrorStatus?) =
                continuation.resume(GResult.Err("Failed to initialize SDK with error ${errorStatus.toString()}"))
        })
    }
}

private const val IQ_APP_ID = "1cbcd060-595c-4b40-bbac-b26475f39d5b"

private suspend fun ConnectIQ.connectToApp(device: IQDevice, timeoutMillis: Long?) =
    withTimeoutOrDefault(
        timeMillis = timeoutMillis,
        default = GResult.Err("Timed out when connecting to app")
    ) {
        suspendCoroutine<GResult<IQApp, String>> { continuation ->
            getApplicationInfo(IQ_APP_ID,
                device,
                object : ConnectIQ.IQApplicationInfoListener {
                    override fun onApplicationInfoReceived(iqApp: IQApp) =
                        continuation.resume(GResult.Ok(iqApp))

                    override fun onApplicationNotInstalled(error: String?) =
                        continuation.resume(GResult.Err("IQApp is not installed: $error"))
                }
            )
        }
    }
