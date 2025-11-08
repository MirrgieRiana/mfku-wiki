import org.gradle.api.Task
import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.streams.asSequence

fun ByteArray.decodeEucJp() = Charset.forName("EUC-JP").decode(ByteBuffer.wrap(this)).toString()
fun String.decodeTitle() = this.chunked(2).map { it.toInt(16).toByte() }.toByteArray().decodeEucJp()
fun String.escapeFileName() = this.replace("""[<>:"/\\|?*\u0000-\u001F\u007F%]+|[ .]+$""".toRegex()) { m ->
    m.value.chars().asSequence().joinToString("") { "%" + it.toString(16).padStart(2, '0') }
}

fun Sequence<File>.readAsWikiPages(task: Task) = this.mapNotNull { file ->

    if (file.isDirectory) {
        return@mapNotNull null
    }
    if (!file.isFile) {
        task.logger.warn("Invalid file: $file")
        return@mapNotNull null
    }

    val matchResult = """^((?:[0-9A-F]{2})+)\.txt$""".toRegex().matchEntire(file.name) ?: run {
        task.logger.warn("Invalid file: ${file.name}")
        return@mapNotNull null
    }
    val title = matchResult.groups[1]!!.value.decodeTitle()
    if (title.startsWith(":")) {
        task.logger.info("Skip special page: $title")
        return@mapNotNull null
    }

    val body = file.readBytes().decodeEucJp()

    Pair(title, body)
}
