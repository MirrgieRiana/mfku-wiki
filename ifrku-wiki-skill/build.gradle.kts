import org.apache.tools.ant.filters.ReplaceTokens

version = file("version.txt").readText().trim()

val jekyllSource = layout.projectDirectory.dir("src/main/jekyll")

val bundleInstall by tasks.registering(Exec::class) {
    inputs.file("Gemfile")
    outputs.dir(layout.buildDirectory.dir("vendor/bundle"))
    commandLine("bash", "scripts/install.sh")
}

val jekyllWork = layout.buildDirectory.dir("jekyll-work")
val prepareJekyllSource by tasks.registering(Sync::class) {
    from(jekyllSource)
    into(jekyllWork)
    filter<ReplaceTokens>("tokens" to mapOf("version" to version.toString()))
}

val jekyllBuild by tasks.registering(Exec::class) {
    inputs.files(prepareJekyllSource)
    inputs.files(bundleInstall)
    outputs.dir(layout.buildDirectory.dir("jekyll"))
    commandLine("bash", "scripts/jekyll-build.sh")
}

val copyHtml by tasks.registering(Sync::class) {
    from(jekyllBuild)
    into(layout.buildDirectory.dir("skill-html"))
}

val skillOutput = layout.buildDirectory.dir("skill")
val generateSkillMd by tasks.registering {
    val skipDirs = setOf("_layouts")
    val mdSources = jekyllSource.asFile.listFiles()!!
        .filter { it.isFile && it.extension == "md" && it.parentFile.name !in skipDirs }
    inputs.files(prepareJekyllSource)
    mdSources.forEach { source ->
        outputs.file(skillOutput.map { it.file(source.name).asFile })
    }
    val jekyllOnlyKeys = setOf("layout")
    doLast {
        mdSources.forEach { source ->
            val resolved = jekyllWork.get().file(source.name).asFile
            val lines = resolved.readLines()
            val closingDash = lines.withIndex().filter { it.value.trim() == "---" }.drop(1).firstOrNull()?.index ?: -1
            val frontMatter = lines.subList(1, closingDash)
                .filter { line -> jekyllOnlyKeys.none { line.startsWith("$it:") } }
            val body = lines.drop(closingDash + 1)
                .filter { it.trim() !in setOf("{% raw %}", "{% endraw %}") }
            skillOutput.get().file(source.name).asFile.writeText(
                (listOf("---") + frontMatter + listOf("---") + body).joinToString("\n"),
            )
        }
    }
}

val skillZip by tasks.registering(Zip::class) {
    from(generateSkillMd)
    destinationDirectory = layout.buildDirectory.dir("distributions")
    archiveBaseName = "ifrku-wiki-skill"
    archiveVersion = version.toString()
}

val clean by tasks.registering(Delete::class) {
    delete(layout.buildDirectory)
}

val build by tasks.registering {
    dependsOn(skillZip, copyHtml)
}
