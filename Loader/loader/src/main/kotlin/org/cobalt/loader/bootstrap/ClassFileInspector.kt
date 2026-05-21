package org.cobalt.loader.bootstrap

import java.io.ByteArrayInputStream
import java.io.DataInputStream

object ClassFileInspector {
    private const val CLASS_MAGIC = 0xCAFEBABE.toInt()

    fun className(bytes: ByteArray): String {
        DataInputStream(ByteArrayInputStream(bytes)).use { input ->
            require(input.readInt() == CLASS_MAGIC) { "Downloaded bytecode is not a Java class file" }
            input.readUnsignedShort()
            input.readUnsignedShort()

            val constantPool = arrayOfNulls<Any>(input.readUnsignedShort())
            var index = 1
            while (index < constantPool.size) {
                when (val tag = input.readUnsignedByte()) {
                    1 -> constantPool[index] = input.readUTF()
                    3, 4 -> input.skipFully(4)
                    5, 6 -> {
                        input.skipFully(8)
                        index++
                    }
                    7, 8, 16, 19, 20 -> constantPool[index] = input.readUnsignedShort()
                    9, 10, 11, 12, 17, 18 -> input.skipFully(4)
                    15 -> input.skipFully(3)
                    else -> error("Unsupported class constant pool tag $tag")
                }
                index++
            }

            input.readUnsignedShort()
            val thisClassIndex = input.readUnsignedShort()
            val classNameIndex = constantPool[thisClassIndex] as? Int
                ?: error("Invalid class file: missing this_class entry")
            val internalName = constantPool[classNameIndex] as? String
                ?: error("Invalid class file: missing class name")
            return internalName.replace('/', '.')
        }
    }

    private fun DataInputStream.skipFully(bytes: Int) {
        var remaining = bytes
        while (remaining > 0) {
            val skipped = skipBytes(remaining)
            require(skipped > 0) { "Unexpected end of class file" }
            remaining -= skipped
        }
    }
}
