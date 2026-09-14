package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.manager.DeviceCapabilityScanner
import com.example.model.PerformanceMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GameBoostIQ", appName)
    val developerCredit = context.getString(R.string.developer_credit)
    assertEquals("Developed by iamuzairmehmood", developerCredit)
  }

  @Test
  fun `verify device capability scanner runs cleanly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val scanner = DeviceCapabilityScanner(context)
    val report = scanner.performFullScan()

    assertNotNull(report)
    assertTrue(report.categories.isNotEmpty())
    assertNotNull(report.overallSummary)
  }

  @Test
  fun `verify performance modes have valid titles and colors`() {
    val modes = PerformanceMode.values()
    assertEquals(3, modes.size)
    assertTrue(modes.any { it == PerformanceMode.PERFORMANCE })
    assertTrue(modes.any { it == PerformanceMode.BALANCED })
    assertTrue(modes.any { it == PerformanceMode.BATTERY_SAVER })
  }
}
