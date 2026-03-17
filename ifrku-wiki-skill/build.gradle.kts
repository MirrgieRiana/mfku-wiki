version = file("version.txt").readText().trim()

val jekyllSource = layout.projectDirectory.dir("src/main/jekyll")
val jekyllWork = layout.buildDirectory.dir("jekyll-work")
val skillOutput = layout.buildDirectory.dir("skill")
val versionTokens = mapOf("version" to version.toString())

val bundleInstall by tasks.registering(Exec::class) {
    inputs.file("Gemfile")
    outputs.dir(layout.buildDirectory.dir("vendor/bundle"))
    commandLine("bash", "scripts/install.sh")
}

val prepareJekyllSource by tasks.registering(Sync::class) {
    from(jekyllSource)
    into(jekyllWork)
    filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to versionTokens)
}

val jekyllBuild by tasks.registering(Exec::class) {
    inputs.files(prepareJekyllSource)
    inputs.files(bundleInstall)
    outputs.dir(layout.buildDirectory.dir("jekyll"))
    commandLine("bash", "scripts/jekyll-build.sh")
}

val copyHtml by tasks.registering(Sync::class) {
    from(jekyllBuild)
    into(skillOutput)
}

val generateSkillMd by tasks.registering {
    dependsOn(copyHtml)
    val skipDirs = setOf("_layouts")
    val mdSources = jekyllSource.asFile.listFiles()!!
        .filter { it.isFile && it.extension == "md" && it.parentFile.name !in skipDirs }
    mdSources.forEach { source ->
        inputs.file(jekyllWork.map { it.file(source.name).asFile })
        outputs.file(skillOutput.map { it.file(source.name).asFile })
    }
    doLast {
        mdSources.forEach { source ->
            val resolved = jekyllWork.get().file(source.name).asFile
            val lines = resolved.readLines()
            val closingDash = lines.withIndex().filter { it.value.trim() == "---" }.drop(1).firstOrNull()?.index ?: -1
            skillOutput.get().file(source.name).asFile.writeText(
                lines.drop(closingDash + 1)
                    .filter { it.trim() !in setOf("{% raw %}", "{% endraw %}") }
                    .joinToString("\n"),
            )
        }
    }
}

val skillZip by tasks.registering(Zip::class) {
    dependsOn(generateSkillMd)
    from(skillOutput)
    destinationDirectory = layout.buildDirectory.dir("distributions")
    archiveBaseName = "ifrku-wiki-skill"
    archiveVersion = version.toString()
}

val clean by tasks.registering(Delete::class) {
    delete(layout.buildDirectory)
}

val build by tasks.registering {
    dependsOn(skillZip)
}
