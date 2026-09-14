package com.iamuzairmehmood.gamestats.monitor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.math.abs

class PingTester {

    data class PingTestResult(
        val isRunning: Boolean = false,
        val progressPercent: Float = 0f,
        val minPing: Int = -1,
        val maxPing: Int = -1,
        val avgPing: Int = -1,
        val packetLossPercent: Int = 0,
        val jitterMs: Int = 0,
        val successCount: Int = 0,
        val failedCount: Int = 0,
        val timeRemainingSec: Int = 0
    )

    private val _testResult = MutableStateFlow(PingTestResult())
    val testResult: StateFlow<PingTestResult> = _testResult.asStateFlow()

    suspend fun runPingTest(durationSeconds: Int) {
        _testResult.value = PingTestResult(isRunning = true, timeRemainingSec = durationSeconds)
        
        var min = Int.MAX_VALUE
        var max = 0
        var totalPing = 0
        var successes = 0
        var fails = 0
        var lastPing = -1
        var totalJitter = 0
        
        val totalProbes = durationSeconds
        
        withContext(Dispatchers.IO) {
            for (i in 0 until totalProbes) {
                if (!isActive) break
                
                var currentPing = -1
                val startTime = System.currentTimeMillis()
                try {
                    Socket().use { socket ->
                        socket.connect(InetSocketAddress("8.8.8.8", 53), 800)
                        currentPing = (System.currentTimeMillis() - startTime).toInt()
                    }
                } catch (e: Exception) {
                    currentPing = -1
                }
                
                if (currentPing > 0) {
                    successes++
                    if (currentPing < min) min = currentPing
                    if (currentPing > max) max = currentPing
                    totalPing += currentPing
                    
                    if (lastPing > 0) {
                        totalJitter += abs(currentPing - lastPing)
                    }
                    lastPing = currentPing
                } else {
                    fails++
                    lastPing = -1
                }
                
                val currentProgress = (i + 1).toFloat() / totalProbes
                val currentLoss = (fails.toFloat() / (successes + fails) * 100).toInt()
                val currentAvg = if (successes > 0) totalPing / successes else -1
                val currentJitterAvg = if (successes > 1) totalJitter / (successes - 1) else 0
                
                _testResult.value = _testResult.value.copy(
                    progressPercent = currentProgress,
                    minPing = if (min == Int.MAX_VALUE) -1 else min,
                    maxPing = max,
                    avgPing = currentAvg,
                    packetLossPercent = currentLoss,
                    jitterMs = currentJitterAvg,
                    successCount = successes,
                    failedCount = fails,
                    timeRemainingSec = totalProbes - (i + 1)
                )
                
                delay(1000L) // 1 probe per second
            }
        }
        
        _testResult.value = _testResult.value.copy(isRunning = false, progressPercent = 1f, timeRemainingSec = 0)
    }
}
