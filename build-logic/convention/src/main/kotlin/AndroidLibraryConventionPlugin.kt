import com.android.build.gradle.LibraryExtension
import ir.mehdiyari.krypt.applyAndroidLibraryPlugin
import ir.mehdiyari.krypt.applyKotlinAndroidPlugin
import ir.mehdiyari.krypt.configureFlavor
import ir.mehdiyari.krypt.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            applyAndroidLibraryPlugin()
            applyKotlinAndroidPlugin()

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }
        }
    }

}