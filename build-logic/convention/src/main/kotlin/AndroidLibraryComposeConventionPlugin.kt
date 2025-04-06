import com.android.build.api.dsl.LibraryExtension
import ir.mehdiyari.krypt.configureComposeAndroid
import ir.mehdiyari.krypt.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureComposeAndroid(extensions.getByType<LibraryExtension>())
            pluginManager.apply(libs.findPlugin("compose.compiler").get().get().pluginId)
        }
    }
}