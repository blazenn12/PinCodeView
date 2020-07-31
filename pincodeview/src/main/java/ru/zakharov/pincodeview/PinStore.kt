package ru.zakharov.pincodeview

internal class PinStore(private val callback: IPinStore.Callback?) : IPinStore {

    companion object {
        private const val UNREACHABLE = -1
        private const val PIN_ARRAY_SIZE = 4
        private const val CURSOR_START_VALUE = 0
        private const val CURSOR_MAX_VALUE = 3
        private const val MIN_VALUE = 0
        private const val MAX_VALUE = 9
    }

    private var pinArray: Array<Int> = Array(PIN_ARRAY_SIZE) { UNREACHABLE }
    private var cursor: Int = CURSOR_START_VALUE

    override fun add(int: Int) {
        if (int < MIN_VALUE || int > MAX_VALUE) {
            throw IllegalArgumentException("Add number $int can't be more then 9 and less then 0")
        }
        if (cursor in CURSOR_START_VALUE..CURSOR_MAX_VALUE) {
            pinArray[cursor] = int
            callback?.onPinAdd(cursor)

            if (cursor == CURSOR_MAX_VALUE) {
                callback?.let {
                    it.onCompleted(pinArray.joinToString("") { i -> i.toString() })
                }
            }
            nextCursor()
        }
    }

    override fun clear() {
        pinArray = Array(PIN_ARRAY_SIZE) { UNREACHABLE }
        cursor = CURSOR_START_VALUE
    }

    private fun nextCursor() {
        if (cursor < CURSOR_MAX_VALUE) {
            cursor++
        } else if (cursor == CURSOR_MAX_VALUE) {
            cursor = UNREACHABLE
        }
    }
}