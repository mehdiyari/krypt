import com.android.build.api.dsl.ApplicationExtension
import ir.mehdiyari.krypt.Versions
import ir.mehdiyari.krypt.applyAndroidApplicationPlugin
import ir.mehdiyari.krypt.applyKotlinAndroidPlugin
import ir.mehdiyari.krypt.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyAndroidApplicationPlugin()
            applyKotlinAndroidPlugin()

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = Versions.TARGET_SDK
            }
        }
    }
}