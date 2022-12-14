package com.vodbot.egy.gradle.configuration

import com.vodbot.egy.gradle.ApkInfo
import com.vodbot.egy.gradle.createProgressLogger
import com.vodbot.egy.gradle.download
import com.vodbot.egy.gradle.getCloudstream
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import java.net.URL

class ApkConfigurationProvider : IConfigurationProvider {

    override val name: String
        get() = "apk"

    override fun provide(project: Project, dependency: Dependency) {
        val extension = project.extensions.getCloudstream()
        if (extension.apkinfo == null) {
            extension.apkinfo = ApkInfo(extension, dependency.version ?: "pre-release")
        }
        val apkinfo = extension.apkinfo!!

        apkinfo.cache.mkdirs()

        if (!apkinfo.jarFile.exists()) {
            project.logger.lifecycle("Fetching JAR")

            val url = URL("${apkinfo.urlPrefix}/classes.jar")
            url.download(apkinfo.jarFile, createProgressLogger(project, "Download JAR"))
        }

        project.dependencies.add("compileOnly", project.files(apkinfo.jarFile))
    }
}