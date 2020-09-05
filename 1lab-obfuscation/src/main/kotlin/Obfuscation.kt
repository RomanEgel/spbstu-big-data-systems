import org.xml.sax.InputSource
import org.xml.sax.helpers.XMLFilterImpl
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult

const val sourceLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
const val targetLetters = "Q5A8ZWS0XEDC6RFVT9GBY4HNU3J2MI1KO7LPqwertyuiopasdfghjklzxcvbnm"

fun main(args: Array<String>) {
    assert(args.size == 3) { "Wrong number of input args" }

    val sourcePath: String = args[0]
    val targetPath: String = args[1]
    val obfuscateFlag: Boolean = args[2].toBoolean()

    obfuscateXml(sourcePath, targetPath, obfuscateFlag)
}

fun obfuscateXml(sourcePath: String, targetPath: String, obfuscateFlag: Boolean) {
    val reader = FileReader(sourcePath, StandardCharsets.UTF_8)
    val writer = FileWriter(targetPath, StandardCharsets.UTF_8)

    val transformer = TransformerFactory.newInstance().newTransformer()
    transformer.setOutputProperty("omit-xml-declaration", "yes");

    reader.use {
        writer.use {
            transformer.transform(SAXSource(XmlReaderWithObfuscation(obfuscateFlag), InputSource(reader)),
                    StreamResult(writer))
        }
    }
}

class XmlReaderWithObfuscation(private val obfuscateFlag: Boolean) :
        XMLFilterImpl(SAXParserFactory.newInstance().newSAXParser().xmlReader) {
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        val src = if (obfuscateFlag) sourceLetters else targetLetters
        val tgt = if (obfuscateFlag) targetLetters else sourceLetters

        for (i in start..start + length) {
            val index = src.indexOf(ch!![i])

            if (index != -1) {
                ch[i] = tgt[index]
            }
        }

        super.characters(ch, start, length)
    }
}