package com.ionspin.kotlin.bignum.decimal

import kotlin.test.assertEquals
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-May-2021
 */
class ReportedIssuesTest {

    @Test
    fun testMod2() {

        val a = "15.5".toBigDecimal()
        val b = "360".toBigDecimal()

        assertEquals("15.5", ((b * 5 + a) % b).toStringExpanded())

        assertEquals("${-15.5 % 360.0}", (-a % b).toStringExpanded())
    }

    @Test
    fun multiplicationPrecisionLoss() {
        val a = "5.61".toBigDecimal().roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val b = "2.95".toBigDecimal().roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        val aJava = a.toJavaBigDecimal()
        val bJava = b.toJavaBigDecimal()
        val javaRes = aJava.multiply(bJava).setScale(2, java.math.RoundingMode.HALF_UP)
        val res = a.multiply(b).roundToDigitPositionAfterDecimalPoint(2, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        assertEquals(res.toStringExpanded(), javaRes.toPlainString())
    }

    @Test
    fun testDivision() {
        val a = 1.08589200008919E+4
        val b = 1.085892300018812E+5

        val aOverB = a / b
        println("a/b=${aOverB}")

        println(String.format("%16.8E",aOverB))

        val a2 = BigDecimal.fromDouble(a)
        val b2 = BigDecimal.fromDouble(b)

        RoundingMode.entries.filter { it == RoundingMode.AWAY_FROM_ZERO }.forEach { mode ->
            println("mode: $mode")
            listOf(5L, 6L, 7L).forEach { precision ->
                val a2OverB2 = a2.divide(b2, DecimalMode(precision, mode))
                println("\tprec: $precision, a2/b2=${a2OverB2}")
            }
            println()
        }
    }
}
