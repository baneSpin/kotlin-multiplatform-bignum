package com.ionspin.kotlin.bignum.decimal

import java.math.MathContext
import kotlin.test.assertEquals
import org.junit.Test
import kotlin.test.assertTrue

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

        println(String.format("%16.8E", aOverB))
//
//        val asd = java.math.BigDecimal(0.0999999723794, MathContext(5, java.math.RoundingMode.CEILING))
//        val asdf = java.math.BigDecimal(0.0999999723794, MathContext(8, java.math.RoundingMode.CEILING))
//        val asdfg = java.math.BigDecimal(0.0989999723794, MathContext(5, java.math.RoundingMode.CEILING))
//        println("asd " + asd)
//        println("asdf " + asdf)
//        println("asdfg " + asdfg)
        val a2 = BigDecimal.fromDouble(a)
        val b2 = BigDecimal.fromDouble(b)
        RoundingMode.entries.filter { it == RoundingMode.CEILING }.forEach { mode ->
            println("mode: $mode")
//            val a2OverB2 = a2.divide(b2, DecimalMode(20, mode))
            (2L..8L).forEach { precision ->
//                println("precision $precision")
                val divided = a2.divide(b2, DecimalMode(precision, mode))
                println("\tprec: ${divided.precision} $precision, a2/b2=${divided.significand}, $divided, ${divided.exponent}")

            }
            println()
        }
    }

    @Test
    fun testDivisionOriginal() {
        val a = 1.08589200008919E+4
        val b = 1.085892300018812E+5

        val aOverB = a / b
        println("a/b=${aOverB}")

        val a2 = BigDecimal.fromDouble(a)
        val b2 = BigDecimal.fromDouble(b)

        RoundingMode.entries.filter { it == RoundingMode.CEILING }.forEach { mode ->
            println("mode: $mode")
            listOf(5L, 6L, 7L).forEach { precision ->
                val a2OverB2 = a2.divide(b2, DecimalMode(precision, mode))
                println("\tprec: $precision, a2/b2=${a2OverB2}")
            }
            println()
        }
    }

    @Test
    fun testDivisionJava() {
        val a = 1.085892300018812E5
        val b = 1.08589200008919E4
        1085892300018812
        108589200008919
        val aOverB = a / b
        println("a/b=${aOverB}")

        println(String.format("%16.8E", aOverB))
        val roundingModesMap = mapOf(
            // work
//            Pair(java.math.RoundingMode.CEILING, RoundingMode.CEILING),
//            Pair(java.math.RoundingMode.FLOOR, RoundingMode.FLOOR),
//            Pair(java.math.RoundingMode.UP, RoundingMode.AWAY_FROM_ZERO),
//            Pair(java.math.RoundingMode.DOWN, RoundingMode.TOWARDS_ZERO),
            // do not work
//            Pair(java.math.RoundingMode.HALF_DOWN, RoundingMode.ROUND_HALF_TOWARDS_ZERO),
            Pair(java.math.RoundingMode.HALF_UP, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO),

//            Pair(java.math.RoundingMode.HALF_EVEN, RoundingMode.ROUND_HALF_TO_EVEN),
        )

        val kotlinBigDecimalA = BigDecimal.fromDouble(a)
        val kotlinBigDecimalB = BigDecimal.fromDouble(b)
        val javaBigDecimalA = kotlinBigDecimalA.toJavaBigDecimal()
        val javaBigDecimalB = kotlinBigDecimalB.toJavaBigDecimal()
        /*java.math.RoundingMode.HALF_DOWN == RoundingMode.ROUND_HALF_TOWARDS_ZERO
        java.math.RoundingMode.HALF_UP == RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        java.math.RoundingMode.DOWN == RoundingMode.TOWARDS_ZERO
        java.math.RoundingMode.UP == RoundingMode.AWAY_FROM_ZERO
        java.math.RoundingMode.HALF_EVEN == RoundingMode.ROUND_HALF_TO_EVEN*/
        roundingModesMap.forEach { javaMode, kotlinMode ->
            println(kotlinMode)
            (9..100).forEach { precision ->
                val javaDivided =
                    javaBigDecimalA.divide(javaBigDecimalB, MathContext(precision, javaMode))
                val kotlinDivided =
                    kotlinBigDecimalA.divide(kotlinBigDecimalB, DecimalMode(precision.toLong(), kotlinMode))
                println("javaDivided \tprec: $precision,  a2/b2=${javaDivided}, $javaDivided")
                println("kotlinDivided \tprec: $precision, a2/b2=${kotlinDivided} ${kotlinDivided.toStringExpanded()}")
                assertTrue {
                    javaDivided.compareTo(kotlinDivided.toJavaBigDecimal()) == 0
                }
            }
        }
//        java.math.RoundingMode.entries.filter { it == java.math.RoundingMode.CEILING }.forEach { mode ->
//            println("mode: $mode")
//            (2..100).forEach { precision ->
//                val javaDivided =
//                    javaBigDecimalA.divide(javaBigDecimalB, MathContext(precision, java.math.RoundingMode.CEILING))
//                val kotlinDivided =
//                    kotlinBigDecimalA.divide(kotlinBigDecimalB, DecimalMode(precision.toLong(), RoundingMode.CEILING))
////                println("javaDivided \tprec: $precision,  a2/b2=${javaDivided}, $javaDivided")
////                println("kotlinDivided \tprec: $precision, a2/b2=${kotlinDivided} ${kotlinDivided.toStringExpanded()}")
////                assertEquals(divided, divided2.toJavaBigDecimal())
//                assertTrue {
//                    javaDivided.compareTo(kotlinDivided.toJavaBigDecimal()) == 0
//                }
//
//            }
//            println()
        }


}
