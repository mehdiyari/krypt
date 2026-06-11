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

fun configureFlavor(extension: CommonExtension) {
    extension.flavorDimensions += FlavorDimension.main.name

    KryptFlavor.values().forEach { flavor ->
        extension.productFlavors.create(flavor.name) {
            dimension = flavor.dimension.name

            if (extension is ApplicationExtension && this is ApplicationProductFlavor) {
                if (flavor.applicationIdSuffix != null) {
                    applicationIdSuffix = flavor.applicationIdSuffix
                }

                if (flavor.versionNameSuffix != null) {
                    versionNameSuffix = flavor.versionNameSuffix
                }
            }
        }
    }
}