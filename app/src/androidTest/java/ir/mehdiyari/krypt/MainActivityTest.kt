package ir.mehdiyari.krypt

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import ir.mehdiyari.krypt.app.MainActivity
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {
    @Test
    fun testMainActivity() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java)
    }
}

