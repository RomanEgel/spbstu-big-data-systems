import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.cms.ContentInfo
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaCertStore
import org.bouncycastle.cms.*
import org.bouncycastle.cms.jcajce.*
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
import org.bouncycastle.util.Selector
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate

class DataEncryptor(private val settings: KeyStoreSettings) {
    private val certificate: X509Certificate
    private val privateKey: PrivateKey

    init {
        val keyStore = KeyStore.getInstance(settings.type).also { ks ->
            FileInputStream(settings.path).use { fis ->
                ks.load(fis, settings.password.toCharArray())
            }
        }

        certificate = keyStore.getCertificate(settings.keyAlias) as X509Certificate
        privateKey = keyStore.getKey(settings.keyAlias, settings.password.toCharArray()) as PrivateKey
    }

    fun encryptData(msg: ByteArray): ByteArray? {
        val cmsEnvelopedDataGenerator = CMSEnvelopedDataGenerator()
        val jceKey = JceKeyTransRecipientInfoGenerator(certificate)
        cmsEnvelopedDataGenerator.addRecipientInfoGenerator(jceKey)

        val cmsMsg = CMSProcessableByteArray(msg)
        val encryptor = JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
                .setProvider("BC")
                .build()
        val cmsEnvelopedData = cmsEnvelopedDataGenerator.generate(cmsMsg, encryptor)

        return cmsEnvelopedData.encoded
    }

    fun decryptData(encryptedData: ByteArray?): ByteArray? {
        val envelopedData: CMSEnvelopedData = CMSEnvelopedData(encryptedData)
        val recipientInfo = envelopedData.recipientInfos.recipients.iterator().next()
        val recipient = JceKeyTransEnvelopedRecipient(privateKey)

        return recipientInfo.getContent(recipient)
    }

    fun signData(data: ByteArray): ByteArray? {
        val cmsData = CMSProcessableByteArray(data)
        val certStore = JcaCertStore(listOf(certificate))
        val cmsGenerator = CMSSignedDataGenerator()


        val contentSigner = JcaContentSignerBuilder("SHA256withRSA")
                .build(privateKey)
        cmsGenerator.addSignerInfoGenerator(
                JcaSignerInfoGeneratorBuilder(JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                        .build(contentSigner, certificate))
        cmsGenerator.addCertificates(certStore)
        val cms = cmsGenerator.generate(cmsData, true)

        return cms.encoded
    }

    fun verifySignedData(signedData: ByteArray): Boolean {
        val asN1InputStream = ASN1InputStream(ByteArrayInputStream(signedData))
        val cmsSignedData = CMSSignedData(ContentInfo.getInstance(asN1InputStream.readObject()))
        val signerInfo = cmsSignedData.signerInfos.signers.iterator().next()
        val certCollection = cmsSignedData.certificates
                .getMatches(signerInfo.sid as Selector<X509CertificateHolder>)

        val certHolder = certCollection.iterator().next()
        return signerInfo.verify(JcaSimpleSignerInfoVerifierBuilder().build(certHolder))
    }


}