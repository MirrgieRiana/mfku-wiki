import com.google.gson.GsonBuilder

plugins {
    java
    id("build-logic")
}

buildscript {
    dependencies {
        classpath("com.google.code.gson:gson:2.11.0")
    }
}

val srcZipFileName = "d3e9ff84-dce7-4b4a-a71c-dedc68b508dd.tar.gz"

// Wiki Dump を展開
val unpackWikiDump = tasks.register<Sync>("unpackWikiDump") {
    group = "build"
    from(tarTree(resources.gzip(layout.projectDirectory.dir("dumps").file(srcZipFileName))))
    into(layout.buildDirectory.dir("unpackWikiDump"))
}

// テキストに出力
val generateDumpText = tasks.register("generateDumpText") {
    group = "build"
    inputs.files(unpackWikiDump)
    val destinationFile = layout.projectDirectory.file("all.wiki.txt")
    outputs.file(destinationFile)
    doLast {
        destinationFile.asFile.parentFile?.mkdirs()
        destinationFile.asFile.printWriter().use { writer ->
            unpackWikiDump.get().outputs.files.asFileTree
                .sortedBy { it.path }
                .asSequence()
                .readAsWikiPages(this@register)
                .forEach { (title, body) ->
                    writer.print("##########################################")
                    writer.print("\n")
                    writer.print("##  Page: $title")
                    writer.print("\n")
                    writer.print("##########################################")
                    writer.print("\n")
                    writer.print("")
                    writer.print("\n")
                    writer.print(body)
                    writer.print("\n")
                }
        }
        logger.lifecycle("Written: ${destinationFile.asFile.absolutePath}")
    }
}
tasks.named("assemble").configure { dependsOn(generateDumpText) }

// jsonに出力
val generateDumpJson = tasks.register("generateDumpJson") {
    group = "build"
    inputs.files(unpackWikiDump)
    val destinationFile = layout.projectDirectory.file("all.wiki.json")
    outputs.file(destinationFile)
    doLast {
        destinationFile.asFile.parentFile?.mkdirs()
        destinationFile.asFile.printWriter().use { writer ->
            val data = unpackWikiDump.get().outputs.files.asFileTree
                .sortedBy { it.path }
                .asSequence()
                .readAsWikiPages(this@register)
                .associate { (title, body) -> title to body }
            val json = GsonBuilder().setPrettyPrinting().create().toJson(data)
            writer.println(json)
        }
        logger.lifecycle("Written: ${destinationFile.asFile.absolutePath}")
    }
}
tasks.named("assemble").configure { dependsOn(generateDumpJson) }

// 一旦フォーマットを整形する
val dumpWikiDirectory = tasks.register("dumpWikiDirectory") {
    group = "build"
    inputs.files(unpackWikiDump)
    val destinationDirectory = layout.buildDirectory.dir("dumpWikiDirectory")
    outputs.dir(destinationDirectory)
    doLast {
        destinationDirectory.get().asFile.deleteRecursively() || throw Exception("Cannot delete directory: ${destinationDirectory.get().asFile.absolutePath}")
        destinationDirectory.get().asFile.mkdirs() || throw Exception("Cannot create directory: ${destinationDirectory.get().asFile.absolutePath}")
        unpackWikiDump.get().outputs.files.asFileTree.asSequence().readAsWikiPages(this@register).forEach { (title, body) ->
            val destinationFile = destinationDirectory.get().asFile.resolve("${title.escapeFileName()}.wiki.txt")
            if (destinationFile.exists()) throw Exception("File already exists: ${destinationFile.absolutePath}")
            destinationFile.writeText(body)
        }
    }
}

// フォルダに出力
val generateWikiDirectory = tasks.register<Sync>("generateWikiDirectory") {
    group = "build"
    from(dumpWikiDirectory)
    into(layout.projectDirectory.dir("wiki"))
}
tasks.named("assemble").configure { dependsOn(generateWikiDirectory) }
