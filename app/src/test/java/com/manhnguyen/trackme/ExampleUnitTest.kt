package com.manhnguyen.trackme

import com.manhnguyen.trackme.testing.CallSingleton
import com.manhnguyen.trackme.testing.SingletonClass
import org.junit.Test

import org.junit.Assert.*
import java.lang.Exception

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun singletonCls() {

        val callSingleton = CallSingleton()
        assertEquals(1, SingletonClass.instance.intVar)
        callSingleton.increase()
        assertEquals(2, SingletonClass.instance.intVar)


    }

    @Test
    fun return_Finally() {
        assertEquals("joined", returnValue())
    }

    private fun returnValue(): String {
        try {
            if (1 == 1)
                return "joined"
        } catch (ex: Exception) {
            return "ex"
        } finally {
            return "finally"
        }

    }

    @Test
    fun write_Values() {

/*        val lstOutput = printString(0)
        assertEquals("12345**8", lstOutput)*/
        /*  val lstOutput = multiInt(listOf(1, 7, 3, 4))
          assertEquals(1, lstOutput)*/
        val lstOutput = mergeArray()
        assertEquals(1, lstOutput)
    }

    private fun getPrefix(maxValue: Int): String {
        var output = ""
        for (i in 1..maxValue) {
            output += i
        }
        return output
    }

    private fun printString(input: Int): MutableList<String> {
        val maxInput = input + 3
        var output = ""
        var lstOutput = mutableListOf<String>()
        for (i in 1..input) {
            output = "\n" + getPrefix(i)
            for (k in i + 1..maxInput) {
                if (k == i + 1 || k == i + 2)
                    output += "*"
                else
                    output += k
            }
            lstOutput.add(output)
        }
        return lstOutput
    }

    private fun multiInt(lst: List<Int>): MutableList<Int> {
        val result = mutableListOf<Int>()
        for (i in lst) {
            var valueGet: Int = 1
            for (k in lst) {
                if (k == i) continue
                valueGet *= k
            }
            result.add(valueGet)
        }
        return result
    }

    private fun mergeArray(): MutableList<Int> {
        val lst1 = mutableListOf(3, 4, 6, 10, 11, 15)
        val lst2 = mutableListOf(1, 5, 8, 12, 14, 19)


        for (item in lst1) {
            lst2.add(item)
        }

        for (i in 0 until lst2.size) {
            for (k in i + 1 until lst2.size) {
                if (lst2[i] > lst2[k]) {
                    val max = lst2[i]
                    lst2[i] = lst2[k]
                    lst2[k] = max
                }
            }
        }

        return lst2
    }

}
