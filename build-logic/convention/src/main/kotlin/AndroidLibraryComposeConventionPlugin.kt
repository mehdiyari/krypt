import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import ir.mehdiyari.krypt.applyAndroidLibraryPlugin
import ir.mehdiyari.krypt.configureComposeAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureComposeAndroid(extensions.getByType<LibraryExtension>())
        }
    }
}