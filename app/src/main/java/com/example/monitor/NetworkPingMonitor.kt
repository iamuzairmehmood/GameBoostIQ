package com.example.monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.abs

class NetworkPingMonitor(private val context: Context) {

    data class NetworkData(
        val pingMs: Int,
        val jitterMs: Int,
        val packetLossPercent: Int,
        val connectionType: String,
        val isMetered: Boolean,
        val diagnosticStatus: String
    )

    private val _networkData = MutableStateFlow(
        NetworkData(
            pingMs = 0,
            jitterMs = 0,
            packetLossPercent = 0,
            connectionType = "Checking...",
            isMetered = false,
            diagnosticStatus = "Idle"
        )
    )
    val networkData: StateFlow<NetworkData> = _networkData.asStateFlow()

    private var monitorJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val recentPings = ArrayDeque<Int>(10)
    private var lastPing = 0
    private var probeCount = 0
    private var failedCount = 0

    fun start(intervalMs: Long = 2000L) {
        if (monitorJob?.isActive == true) return

        monitorJob = scope.launch {
            while (isActive) {
                checkNetworkState()
                runPingProbe()
                delay(intervalMs)
            }
        }
    }

    fun stop() {
        monitorJob?.cancel()
        monitorJob = null
    }

    private fun checkNetworkState() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNetwork)

        val type = when {
            caps == null -> "Offline"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Connected"
        }

        val isMetered = cm?.isActiveNetworkMetered ?: false

        _networkData.value = _networkData.value.copy(
            connectionType = type,
            isMetered = isMetered
        )
    }

    private fun runPingProbe() {
        val targets = listOf("1.1.1.1", "8.8.8.8")
        val target = targets[(probeCount % targets.size)]
        val port = 53
        val timeoutMs = 800

        probeCount++
        var pingResult = -1
        val startTime = System.currentTimeMillis()

        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(target, port), timeoutMs)
                pingResult = (System.currentTimeMillis() - startTime).toInt()
            }
        } catch (_: Exception) {
            pingResult = -1
        }

        if (pingResult > 0) {
            val jitter = if (lastPing > 0) abs(pingResult - lastPing) else 0
            lastPing = pingResult

            synchronized(recentPings) {
                if (recentPings.size >= 10) recentPings.removeFirst()
                recentPings.addLast(pingResult)
            }

            val loss = (failedCount.toFloat() / probeCount.coerceAtLeast(1) * 100).toInt().coerceIn(0, 100)
            val diag = when {
                pingResult < 45 -> "Optimal for Gaming"
                pingResult < 85 -> "Good (Stable)"
                pingResult < 150 -> "Moderate Latency"
                else -> "High Latency Warning"
            }

            _networkData.value = _networkData.value.copy(
                pingMs = pingResult,
                jitterMs = jitter,
                packetLossPercent = loss,
                diagnosticStatus = diag
            )
        } else {
            failedCount++
            val loss = (failedCount.toFloat() / probeCount.coerceAtLeast(1) * 100).toInt().coerceIn(0, 100)
            _networkData.value = _networkData.value.copy(
                packetLossPercent = loss,
                diagnosticStatus = "Probe Timeout / Packet Lost"
            )
        }
    }
}
