import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.io.DataInputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.apache.commons:commons-compress:1.27.1")
    }
}

fun ByteArray.decodeEucJp() = Charset.forName("EUC-JP").decode(ByteBuffer.wrap(this)).toString()
fun String.decodeTitle() = this.chunked(2).map { it.toInt(16).toByte() }.toByteArray().decodeEucJp()

fun File.getZipEntries() = sequence {
    FileInputStream(this@getZipEntries).use { fis ->
        GZIPInputStream(fis).use { gis ->
            TarArchiveInputStream(gis).use { tis ->
                var entry = tis.nextTarEntry
                while (entry != null) {

                    yield(Pair(entry, tis))

                    entry = tis.nextTarEntry
                }
            }
        }
    }
}

fun Sequence<Pair<TarArchiveEntry, TarArchiveInputStream>>.visitPages(callback: (String, String) -> Unit) {
    this.forEach { (entry, tis) ->
        val fileName = entry.name
        if (entry.isDirectory) {
            println("Directory: $fileName")
            return@forEach
        }
        println("File: $fileName")

        val matchResult = """^mifai2024/wiki/((?:[0-9A-F]{2})+)\.txt$""".toRegex().matchEntire(fileName)
        if (matchResult == null) {
            println("Invalid file: $fileName")
            return@forEach
        }
        val title = matchResult.groups[1]!!.value.decodeTitle()

        if (title.startsWith(":")) return@forEach

        val buffer = ByteArray(entry.size.toInt())
        DataInputStream(tis).readFully(buffer)

        callback(title, buffer.decodeEucJp())
    }
}

fun dumpAllToSingleText(srcZipFile: File, outFile: File) {
    outFile.printWriter().use { writer ->
        val contents = mutableMapOf<String, List<String>>()
        srcZipFile.getZipEntries().visitPages { name, body ->
            contents[name] = listOf(
                "##########################################",
                "##  Page: $name",
                "##########################################",
                "",
                body,
            )
        }
        contents.entries.sortedBy { it.key }.map { it.value }.flatten().forEach {
            writer.print("$it\n")
        }
    }
}

fun dumpPagesToDirectory(srcZipFile: File, outDir: File) {
    val remainingFiles = outDir.walk().filter { it.isFile }.map { it.absoluteFile }.toMutableSet()
    outDir.mkdirs()
    srcZipFile.getZipEntries().visitPages { name, body ->
        val outFile = outDir.resolve("$name.wiki.txt")
        outFile.writeText(body)
        remainingFiles -= outFile
    }
    remainingFiles.forEach {
        println("Delete: $it")
        it.delete()
    }
}


val srcZipFile = File("3e81b3ca-7be8-4d5c-9ddd-d2e34a683adf.tar.gz")
val outFile = File("wiki-dump_out.txt")
val outDir = File("wiki")

tasks.register("build")

tasks.register("dump")
tasks.named("build") { dependsOn(tasks.named("dump")) }

tasks.register("dumpText") {
    doLast {
        dumpAllToSingleText(srcZipFile, outFile)
    }
}
tasks.named("dump") { dependsOn(tasks.named("dumpText")) }

tasks.register("dumpDir") {
    doLast {
        dumpPagesToDirectory(srcZipFile, outDir)
    }
}
tasks.named("dump") { dependsOn(tasks.named("dumpDir")) }
