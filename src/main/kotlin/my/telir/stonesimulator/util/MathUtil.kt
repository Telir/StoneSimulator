package my.telir.stonesimulator.util

import java.math.BigDecimal
import java.math.RoundingMode

object MathUtil {
    fun round(number: Double, charNumber: Int): Double {
        return BigDecimal(number).setScale(charNumber, RoundingMode.HALF_UP).toDouble()
    }
}