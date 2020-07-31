package ru.zakharov.pincodeview

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PinStoreTest {

    private lateinit var pinStore: PinStore

    @Test
    fun addFourPins_Success() {
        var result: String? = null
        val callback = object : IPinStore.Callback {
            override fun onCompleted(pinCode: String) {
                result = pinCode
            }

            override fun onPinAdd(pinNumber: Int) {
            }
        }

        pinStore = PinStore(callback)
        pinStore.add(1)
        pinStore.add(2)
        pinStore.add(3)
        pinStore.add(4)
        assertEquals("1234", result)
    }

    @Test
    fun addPins_WithOverflow_Success() {
        var result: String? = null
        val callback = object : IPinStore.Callback {
            override fun onCompleted(pinCode: String) {
                result = pinCode
            }

            override fun onPinAdd(pinNumber: Int) {
            }
        }

        pinStore = PinStore(callback)
        pinStore.add(1)
        pinStore.add(2)
        pinStore.add(3)
        pinStore.add(4)
        pinStore.add(0)
        pinStore.add(9)
        pinStore.add(8)
        pinStore.add(7)

        assertEquals("1234", result)
    }

    @Test
    fun pinNumber_OnAdd_Success() {
        var result = 0

        val callback = object : IPinStore.Callback {
            override fun onCompleted(pinCode: String) {
            }

            override fun onPinAdd(pinNumber: Int) {
                result = pinNumber
            }
        }

        pinStore = PinStore(callback)
        pinStore.add(3)
        assertEquals(0, result)

        pinStore.add(5)
        assertEquals(1, result)

        pinStore.add(9)
        assertEquals(2, result)

        pinStore.add(0)
        assertEquals(3, result)
    }

    @Test
    fun addThreePins_Clear_AddFourPins_Success() {
        var result: String? = null

        val callback = object : IPinStore.Callback {
            override fun onCompleted(pinCode: String) {
                result = pinCode
            }

            override fun onPinAdd(pinNumber: Int) {
            }
        }

        pinStore = PinStore(callback)
        pinStore.add(1)
        pinStore.add(0)
        pinStore.add(6)
        pinStore.clear()
        pinStore.add(6)
        pinStore.add(3)
        pinStore.add(4)
        pinStore.add(9)
        assertEquals("6349", result)
    }

}