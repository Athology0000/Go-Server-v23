package org.phantom.loader.bootstrap

import org.phantom.loader.LoaderConfig
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object ModuleCrypto {
    fun decryptAesGcm(moduleKeyBase64: String?, encrypted: ByteArray): ByteArray {
        val keyText = moduleKeyBase64?.takeIf { it.isNotBlank() }
            ?: LoaderConfig.moduleKeyB64
            ?: error("Manifest did not provide a module decryption key.")

        val key = Base64.getDecoder().decode(keyText)
        require(key.size == 32) { "Module decryption key must be 32 bytes." }
        require(encrypted.size > 12) { "Encrypted module is too short." }

        val nonce = encrypted.copyOfRange(0, 12)
        val ciphertext = encrypted.copyOfRange(12, encrypted.size)
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, nonce))
            cipher.doFinal(ciphertext)
        } finally {
            key.fill(0)
            nonce.fill(0)
            ciphertext.fill(0)
        }
    }
}
