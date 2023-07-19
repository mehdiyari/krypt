import com.android.build.api.dsl.ApplicationExtension
import ir.mehdiyari.krypt.configureFlavor
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationFlavorConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            extensions.configure<ApplicationExtension> {
                configureFlavor(this)
            }

        }
    }
}