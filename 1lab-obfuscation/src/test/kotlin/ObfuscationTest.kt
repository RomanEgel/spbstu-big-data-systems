import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets

class ObfuscationTest {
    val src = """
        <employees>
            <employee id="111">
                 <firstName>Lokesh</firstName>
                 <lastName>Gupta</lastName>
                 <location>India</location>
             </employee>
             <employee id="222">
                 <firstName>Alex</firstName>
                 <lastName>Gussin</lastName>
                 <location>Russia</location>
             </employee>
             <employee id="333">
                 <firstName>David</firstName>
                 <lastName>Feezor</lastName>
                 <location>USA</location>
             </employee>
        </employees>""".trimIndent()

    val tgt = """
        <employees>
            <employee id="111">
                 <firstName>wFDZG0</firstName>
                 <lastName>OYVBQ</lastName>
                 <location>LR8XQ</location>
             </employee>
             <employee id="222">
                 <firstName>JCZN</firstName>
                 <lastName>OYGGXR</lastName>
                 <location>iYGGXQ</location>
             </employee>
             <employee id="333">
                 <firstName>IQ4X8</firstName>
                 <lastName>KZZ3F9</lastName>
                 <location>aoJ</location>
             </employee>
        </employees>""".trimIndent()

    @Test
    fun testObfuscation() {
        val srcPath = "/users/regel/src.xml"
        val tgtPath = "/users/regel/tgt.xml"
        FileWriter(srcPath, StandardCharsets.UTF_8).use {
            it.write(src)
        }


        val args = arrayOf(srcPath, tgtPath, "true")
        main(args)

        FileReader(tgtPath).use {
            assertEquals(tgt, it.readText())
        }
    }

    @Test
    fun testUnobfuscation() {
        val srcPath = "/users/regel/src.xml"
        val tgtPath = "/users/regel/tgt.xml"
        FileWriter(srcPath, StandardCharsets.UTF_8).use {
            it.write(tgt)
        }


        val args = arrayOf(srcPath, tgtPath, "false")
        main(args)

        FileReader(tgtPath).use {
            assertEquals(src, it.readText())
        }
    }
}