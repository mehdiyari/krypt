package ir.mehdiyari.krypt.utils

import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions


fun getAnimationNavUtils(optionsBuilder: NavOptionsBuilder.() -> Unit = {}) = navOptions {
    anim {
        this.enter = android.R.anim.slide_in_left
        this.exit = android.R.anim.slide_out_right
    }
    optionsBuilder(this)
}