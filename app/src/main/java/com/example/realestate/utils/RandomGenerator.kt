package com.example.realestate.utils

import java.security.SecureRandom
import java.util.*
import kotlin.random.Random

object RandomGenerator {
    private const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    fun createUniqueImageName(extension: String): String {
        val random = SecureRandom()
        val timeNow = Date().time
        val randomBytes = ByteArray(6)
        random.nextBytes(randomBytes)
        val randomHex = randomBytes.joinToString("") { "%02x".format(it) }
        return "$timeNow.$randomHex.$extension"
    }
    fun generateRandomText(minLength: Int, maxLength: Int): String {
        val length = generateRandomLength(minLength, maxLength)
        val sb = StringBuilder(length)
        val random = Random.Default

        for (i in 0 until length) {
            val randomIndex = random.nextInt(CHARACTERS.length)
            val randomChar = CHARACTERS[randomIndex]
            sb.append(randomChar)
        }

        return sb.toString()
    }

    private fun generateRandomLength(minLength: Int, maxLength: Int): Int {
        val random = Random.Default
        return random.nextInt(maxLength - minLength + 1) + minLength
    }

    fun generateRandomEmptyString(minLength: Int, maxLength: Int): String {
        val length = generateRandomLength(minLength, maxLength)
        return " ".repeat(length)
    }
}