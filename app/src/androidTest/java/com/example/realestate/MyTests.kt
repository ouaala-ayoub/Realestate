package com.example.realestate

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.realestate.ui.viewmodels.userregistermodels.SmsSendModel
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.debug.testing.DebugAppCheckTestHelper
import com.google.firebase.auth.FirebaseAuth
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyTests {
    private val debugAppCheckTestHelper =
        DebugAppCheckTestHelper.fromInstrumentationArgs()

    @Test
    fun testWithDefaultApp() {
        debugAppCheckTestHelper.withDebugProvider<Exception> {
            // Test code that requires a debug AppCheckToken.
            val model = SmsSendModel(FirebaseAuth.getInstance())
//            launchActivity<MyActivity>().use {
//            }
//            ActivityScenarioRule
//            model.sendVerification("+212658729171")
        }
    }

    @Test
    fun testWithNonDefaultApp() {
        debugAppCheckTestHelper.withDebugProvider<Exception>(
            FirebaseApp.getInstance("nonDefaultApp")
        ) {
            // Test code that requires a debug AppCheckToken.
        }
    }
}