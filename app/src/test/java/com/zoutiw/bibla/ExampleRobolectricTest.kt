package com.zoutiw.bibla

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Bib la", appName)
  }

  @Test
  fun `verify French New Testament verses loaded from assets`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.zoutiw.bibla.data.FrenchBibleRepository(context)

    // Test Matthew 1:1
    val mat1 = repo.getFrenchVersesForChapter("Matye", 1)
    org.junit.Assert.assertTrue("Matye 1 should have verses", mat1.isNotEmpty())
    org.junit.Assert.assertTrue("Matye 1:1 should contain David", mat1[1]?.contains("David") == true)

    // Test John 3:16
    val jhn3 = repo.getFrenchVersesForChapter("Jan", 3)
    org.junit.Assert.assertTrue("Jan 3 should have verses", jhn3.isNotEmpty())
    org.junit.Assert.assertTrue("Jan 3:16 should contain Dieu", jhn3[16]?.contains("Dieu") == true)

    // Test Revelation 22:21
    val rev22 = repo.getFrenchVersesForChapter("Revelasyon", 22)
    org.junit.Assert.assertTrue("Revelasyon 22 should have verses", rev22.isNotEmpty())
    org.junit.Assert.assertTrue("Revelasyon 22:21 should contain grâce", rev22[21]?.contains("grâce") == true)

    // Test search in French New Testament
    val searchResults = repo.searchNewTestament("monde")
    org.junit.Assert.assertTrue("Search for 'monde' in NT should return results", searchResults.isNotEmpty())
  }
}

