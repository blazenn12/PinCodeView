package ru.zakharov.pincodeview

internal interface IPinStore {

    interface Callback {

        fun onCompleted(pinCode: String)

        fun onPinAdd(pinNumber: Int)
    }

    fun add(int: Int)

    fun clean()
}