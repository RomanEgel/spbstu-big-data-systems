import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets

class DataEncryptorTest {
    @Before
    fun before() {
        addSystemProperties()
    }

    @Test
    fun testEncryption() {
        val msg = "Message is there"

        val dataEncryptor = getDataEncryptor()

        val encryptedData = dataEncryptor.encryptData(msg.toByteArray(StandardCharsets.UTF_8))
        val decryptedData = dataEncryptor.decryptData(encryptedData)

        assertEquals(msg, decryptedData!!.toString(StandardCharsets.UTF_8))
    }

    @Test
    fun testSign() {
        val msg = "Message is there"
        val dataEncryptor = getDataEncryptor()

        val signedData = dataEncryptor.signData(msg.toByteArray(StandardCharsets.UTF_8))
        assertTrue(dataEncryptor.verifySignedData(signedData!!))
    }

    private fun getDataEncryptor(): DataEncryptor {
        val settings = KeyStoreSettings("PKCS12", "/users/regel/2lab-keystore.jks", "passwordTuta",
                "tuta", "passwordTuta")

        return DataEncryptor(settings)
    }
}