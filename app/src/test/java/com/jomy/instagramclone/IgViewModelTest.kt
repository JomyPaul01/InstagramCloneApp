package com.jomy.instagramclone

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.jomy.instagramclone.dependency.IgApplicationContext
import com.jomy.instagramclone.viewmodel.IgViewModel
import com.jomy.instagramclone.viewmodel.StringResourceProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = IgApplication::class)
class IgViewModelTest {

    private lateinit var viewModel: IgViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        viewModel = IgViewModel(Firebase.auth,Firebase.firestore,Firebase.storage,ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun test_SignUp(){
        viewModel.onSignUp("abel","abel@gmail.com","abcd")
        assertNotNull(viewModel.auth.currentUser?.uid)
    }

}