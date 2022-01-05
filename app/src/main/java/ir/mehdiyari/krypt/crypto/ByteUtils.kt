package ir.mehdiyari.krypt.crypto

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.SecureRandom

/**
 * combine array of elements as ByteArray together
 */
fun combine(vararg elements: ByteArray): ByteArray = ByteArrayOutputStream().let {
    try {
        for (element in elements) {
            it.write(element)
        }
        return it.toByteArray()
    } catch (e: IOException) {
        throw AssertionError(e)
    }
}

fun ByteArray.combineWith(vararg elements: ByteArray): ByteArray = combine(this, *elements)

/**
 * trim byte array @param input with size @param length
 */
fun ByteArray.trim(length: Int): ByteArray = ByteArray(length).apply {
    System.arraycopy(this@trim, 0, this, 0, this.size)
}

/**
 * Get secret bytes from secureRandom  with @param size
 */
fun getSecretBytes(size: Int): ByteArray = ByteArray(size).apply {
    SecureRandom().nextBytes(this)
}

/**
 * Convert String to bytes with charset utf-8
 */
fun String.toUtf8Bytes(): ByteArray {
    try {
        return this.toByteArray(Charsets.UTF_8)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError("UTF_8 not supported on this device")
    }
}

fun ByteArray.getAfterIndex(after: Int): ByteArray = ByteArray(this.size - after).apply {
    for ((index, i) in (after until this@getAfterIndex.size).withIndex()) {
        this@apply[index] = this@getAfterIndex[i]
    }
}

fun ByteArray.getBytesBetweenIndexes(start: Int, end: Int): ByteArray =
    ByteArray(end - start).apply {
        for ((index, i) in (start until end).withIndex()) {
            this@apply[index] = this@getBytesBetweenIndexes[i]
        }
    }

fun ByteArray.getBeforeIndex(before: Int): ByteArray = ByteArray(before).apply {
    for ((index, i) in (0 until before).withIndex())
        this@apply[index] = this@getBeforeIndex[i]
}


fun ByteArray.toBase64() = Base64.encodeBytes(this)
fun ByteArray.toUrlSafeBase64() = Base64.encodeBytes(this, Base64.URL_SAFE)