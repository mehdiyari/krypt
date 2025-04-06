import com.android.build.api.dsl.ApplicationExtension
import ir.mehdiyari.krypt.applyAndroidApplicationPlugin
import ir.mehdiyari.krypt.configureComposeAndroid
import ir.mehdiyari.krypt.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            applyAndroidApplicationPlugin()
            pluginManager.apply(libs.findPlugin("compose.compiler").get().get().pluginId)
            configureComposeAndroid(extensions.getByType<ApplicationExtension>())
        }
    }
}