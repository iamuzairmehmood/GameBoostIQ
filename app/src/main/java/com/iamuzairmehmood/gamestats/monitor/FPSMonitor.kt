package com.iamuzairmehmood.gamestats.monitor

import android.view.Choreographer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class FPSMonitor {

    private val _fps = MutableStateFlow<Int?>(null)
    val fps: StateFlow<Int?> = _fps.asStateFlow()

    private val _minFps = MutableStateFlow<Int?>(null)
    val minFps: StateFlow<Int?> = _minFps.asStateFlow()

    private val _maxFps = MutableStateFlow<Int?>(null)
    val maxFps: StateFlow<Int?> = _maxFps.asStateFlow()

    private val _avgFps = MutableStateFlow<Int?>(null)
    val avgFps: StateFlow<Int?> = _avgFps.asStateFlow()

    private val isRunning = AtomicBoolean(false)
    private var lastFrameTimeNanos: Long = 0L
    private var frameCount = 0
    private var lastFpsCalculationTime = 0L

    private val recentFpsSamples = ArrayDeque<Int>(30)

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isRunning.get()) return

            if (lastFrameTimeNanos > 0) {
                val deltaNanos = frameTimeNanos - lastFrameTimeNanos
                if (deltaNanos > 0) {
                    frameCount++
                }
            }
            lastFrameTimeNanos = frameTimeNanos

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastFpsCalculationTime >= 1000) {
                if (lastFpsCalculationTime > 0) {
                    val seconds = (currentTime - lastFpsCalculationTime) / 1000f
                    val calculatedFps = (frameCount / seconds).toInt().coerceIn(1, 165)

                    _fps.value = calculatedFps

                    synchronized(recentFpsSamples) {
                        if (recentFpsSamples.size >= 30) {
                            recentFpsSamples.removeFirst()
                        }
                        recentFpsSamples.addLast(calculatedFps)

                        val currentMin = recentFpsSamples.minOrNull() ?: calculatedFps
                        val currentMax = recentFpsSamples.maxOrNull() ?: calculatedFps
                        val currentAvg = recentFpsSamples.average().toInt()

                        _minFps.value = currentMin
                        _maxFps.value = currentMax
                        _avgFps.value = currentAvg
                    }
                }
                frameCount = 0
                lastFpsCalculationTime = currentTime
            }

            if (isRunning.get()) {
                Choreographer.getInstance().postFrameCallback(this)
            }
        }
    }

    fun start() {
        if (isRunning.compareAndSet(false, true)) {
            lastFrameTimeNanos = 0L
            frameCount = 0
            lastFpsCalculationTime = System.currentTimeMillis()
            synchronized(recentFpsSamples) {
                recentFpsSamples.clear()
            }
            Choreographer.getInstance().postFrameCallback(frameCallback)
        }
    }

    fun stop() {
        if (isRunning.compareAndSet(true, false)) {
            Choreographer.getInstance().removeFrameCallback(frameCallback)
        }
    }

    fun reset() {
        stop()
        _fps.value = null
        _minFps.value = null
        _maxFps.value = null
        _avgFps.value = null
        synchronized(recentFpsSamples) {
            recentFpsSamples.clear()
        }
    }
}
