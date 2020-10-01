pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.hendraanggrian") {
                useModule("${requested.id.namespace}:packr-gradle-plugin:${requested.version}")
            }
        }
    }
}