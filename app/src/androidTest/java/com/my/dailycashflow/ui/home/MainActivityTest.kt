package com.my.dailycashflow.ui.home

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.my.dailycashflow.R
import com.my.dailycashflow.ui.allcashflows.AllCashFlowsActivity
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            ActivityScenario.launch(MainActivity::class.java)
        }
    }

    @Test
    fun `clickShowAllCashFlowsButtonWillDisplayAllCashFlowsActivity`() {
        Intents.init()
        onView(withId(R.id.btn_all_cashflows)).perform(click())
        intended(hasComponent(AllCashFlowsActivity::class.java.name))
        onView(withId(R.id.text_title_filterby)).check(matches(isDisplayed()))
    }
}