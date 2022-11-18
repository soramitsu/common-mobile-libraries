package jp.co.soramitsu.xnetworking.scale.dataType

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import java.io.ByteArrayOutputStream

fun <T> StringScaleTransformer<T>.toByteArray(value: T): ByteArray {
    val stream = ByteArrayOutputStream()
    val writer = ScaleCodecWriter(stream)

    //write(writer, value)

    return stream.toByteArray()
}