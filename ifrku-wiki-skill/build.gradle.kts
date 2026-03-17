val jekyllSource = layout.projectDirectory.dir("src/main/jekyll")
val jekyllOutput = layout.buildDirectory.dir("jekyll")
val skillOutput = layout.buildDirectory.dir("skill")

val bundleInstall by tasks.registering(Exec::class) {
    inputs.file("Gemfile")
    outputs.dir(layout.buildDirectory.dir("vendor/bundle"))
    commandLine("bash", "scripts/install.sh")
}

val jekyllBuild by tasks.registering(Exec::class) {
    dependsOn(bundleInstall)
    inputs.dir(jekyllSource)
    outputs.dir(jekyllOutput)
    commandLine("bash", "scripts/jekyll-build.sh")
}

val copyHtml by tasks.registering(Sync::class) {
    dependsOn(jekyllBuild)
    from(jekyllOutput)
    into(skillOutput)
}

val mdFiles = listOf("ifrku-wiki-skill.md", "SKILL.md")

val generateSkillMd by tasks.registering {
    dependsOn(copyHtml)
    mdFiles.forEach { name ->
        inputs.file(jekyllSource.file(name))
        outputs.file(skillOutput.map { it.file(name).asFile })
    }
    doLast {
        mdFiles.forEach { name ->
            val source = jekyllSource.file(name).asFile
            val destination = skillOutput.get().file(name).asFile
            var frontMatterEnded = false
            var dashCount = 0
            destination.printWriter().use { writer ->
                source.useLines { lines ->
                    for (line in lines) {
                        if (!frontMatterEnded) {
                            if (line.trim() == "---") {
                                dashCount++
                                if (dashCount >= 2) frontMatterEnded = true
                            }
                            continue
                        }
                        val trimmed = line.trim()
                        if (trimmed == "{% raw %}" || trimmed == "{% endraw %}") continue
                        writer.println(line)
                    }
                }
            }
        }
    }
}

val clean by tasks.registering(Delete::class) {
    delete(layout.buildDirectory)
}

val build by tasks.registering {
    dependsOn(copyHtml, generateSkillMd)
}
