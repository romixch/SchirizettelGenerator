pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "io.github.fvarrui.javapackager") {
                useModule("io.github.fvarrui:javapackager:${requested.version}")
            }
        }
    }
}