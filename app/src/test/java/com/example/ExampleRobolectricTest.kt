package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.QuestionBank
import com.example.model.QuestionState
import com.example.model.QuestionStatus
import com.example.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    assertEquals("CUET CBT", appName)
  }

  @Test
  fun `verify question bank loading and scoring`() {
    val sampleQuestions = QuestionBank.getQuestionsForSubject(Subject.CUET_SAMPLE_MOCK)
    assertEquals(5, sampleQuestions.size)
    assertEquals("Choose the word most nearly SIMILAR in meaning to 'CANDID':", sampleQuestions[0].questionText)
    assertEquals(1, sampleQuestions[0].correctOptionIndex)
    assertEquals("An article bought for Rs. 400 is sold for Rs. 500. What is the profit percentage?", sampleQuestions[1].questionText)
    assertEquals(1, sampleQuestions[1].correctOptionIndex)
    assertTrue(sampleQuestions.all { it.options.size == 4 })

    val full50 = QuestionBank.getQuestionsForSubject(Subject.FULL_CUET_MOCK)
    assertEquals(50, full50.size)
    assertEquals(1, full50.first().id)
    assertEquals(50, full50.last().id)
    assertTrue(full50.all { it.options.size == 4 })
  }
}
