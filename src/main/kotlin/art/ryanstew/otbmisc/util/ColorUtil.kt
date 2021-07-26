package art.ryanstew.otbmisc.util

import kotlin.math.roundToInt

class ColorUtil
{
    companion object ColorUtil
    {
        fun String.isValidHexCode(): Boolean
        {
            return this.matches("^#([A-Fa-f0-9]{6})\$".toRegex())
        }

        // #FF6600 -> [255, 102, 0]
        fun hexToRGBArray(hex: String): IntArray
        {
            val based = Integer.parseInt(hex.substring(1), 16)
            val r = (based shr 16) and 255
            val g = (based shr 8) and 255
            val b = based and 255
            return intArrayOf(r, g, b)
        }

        // [255, 102, 0] -> #FF6600
        fun rgbArrayToHex(rgbA: IntArray): String
        {
            val shifted = (1 shl 24) + (rgbA[0] shl 16) + (rgbA[1] shl 8) + rgbA[2]
            return "#${shifted.toString(16).substring(1)}"
        }

        // get [10, 10, 10] between [0, 0, 0] and [255, 255, 255]
        fun rgbArrayInterp(rgbA1: IntArray, rgbA2: IntArray, alpha: Double): IntArray
        {
            val result = rgbA1.copyOf()
            for (i in rgbA1.indices)
                result[i] = (rgbA1[i] + alpha * (rgbA2[i] - rgbA1[i])).roundToInt()

            return result
        }

        // #FF6600 -> &x&f&f&6&6&0&0
        fun hexToMCCode(hex: String): String
        {
            val charArray = hex.substring(1).trim().chunked(1)

            var colorCode = "&x"
            for (i in charArray.indices)
                colorCode += "&${charArray[i]}"

            return colorCode
        }
    }
}