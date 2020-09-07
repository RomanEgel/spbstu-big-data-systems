import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.nio.charset.StandardCharsets
import java.security.Security
import javax.crypto.Cipher

fun main() {
    addSystemProperties()

    val settings = KeyStoreSettings("PKCS12", "/users/regel/2lab-keystore.jks", "passwordTuta",
            "tuta", "passwordTuta")
    val msg = "Message is there"

    val dataEncryptor = DataEncryptor(settings)

    val encryptedData = dataEncryptor.encryptData(msg.toByteArray(StandardCharsets.UTF_8))
    val decryptedData = dataEncryptor.decryptData(encryptedData)

    assert(msg == decryptedData!!.toString(StandardCharsets.UTF_8))
}

fun addSystemProperties() {
    Security.addProvider(BouncyCastleProvider())
    Security.setProperty("crypto.policy", "unlimited")
    val maxAllowedKeyLength = Cipher.getMaxAllowedKeyLength("AES")
    println("Max key size for AES $maxAllowedKeyLength")
}

data class KeyStoreSettings(val type: String, val path: String, val password: String,
                            val keyAlias: String, val keyPassword: String)
