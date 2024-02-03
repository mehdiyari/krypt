package ir.mehdiyari.krypt

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension

enum class FlavorDimension {
    main
}

enum class KryptFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
    val versionNameSuffix: String? = null
) {

    Development(
        dimension = FlavorDimension.main,
        applicationIdSuffix = ".dev",
        versionNameSuffix = "[Development]"
    ),
    Production(
        dimension = FlavorDimension.main
    )

}

fun configureFlavor(extension: CommonExtension<*, *, *, *, *>) {
    with(extension) {
        flavorDimensions += FlavorDimension.main.name

        productFlavors {
            KryptFlavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name

                    if (this@with is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                        }

                        if (it.versionNameSuffix != null) {
                            versionNameSuffix = it.versionNameSuffix
                        }
                    }
                }
            }
        }
    }
}