import com.android.build.api.dsl.ApplicationExtension
import ir.mehdiyari.krypt.applyAndroidApplicationPlugin
import ir.mehdiyari.krypt.configureFlavor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationFlavorConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            applyAndroidApplicationPlugin()
            configureFlavor(extensions.getByType<ApplicationExtension>())
        }
    }
}