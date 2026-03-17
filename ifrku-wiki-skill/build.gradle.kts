val jekyllSource = layout.projectDirectory.dir("src/main/jekyll")
val skillOutput = layout.buildDirectory.dir("skill")

val bundleInstall by tasks.registering(Exec::class) {
    inputs.file("Gemfile")
    outputs.dir(layout.buildDirectory.dir("vendor/bundle"))
    commandLine("bash", "scripts/install.sh")
}

val jekyllBuild by tasks.registering(Exec::class) {
    inputs.dir(jekyllSource)
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
        inputs.file(source)
        outputs.file(skillOutput.map { it.file(source.name).asFile })
    }
    doLast {
        mdSources.forEach { source ->
            val lines = source.readLines()
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
}

val clean by tasks.registering(Delete::class) {
    delete(layout.buildDirectory)
}

val build by tasks.registering {
    dependsOn(skillZip)
}
