import ir.mehdiyari.krypt.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

private const val ALIAS_HILT_ANDROID = "hilt.android"
private const val ALIAS_HILT_COMPILER = "hilt.compiler"

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
                apply("org.jetbrains.kotlin.kapt")
            }

            dependencies {
                "implementation"(libs.findLibrary(ALIAS_HILT_ANDROID).get())
                "kapt"(libs.findLibrary(ALIAS_HILT_COMPILER).get())
            }
        }
    }
}