package ru.zakharov.pincodeview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), PinCodeView.OnPinCodeEnterListener {

    private lateinit var pinCodeView: PinCodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pinCodeView = findViewById(R.id.pincode)

        pinCodeView.addOnPinCodeEnterListener(this)
    }

    override fun onPinCodeAttempt(pinCode: String) {
        when (pinCode == "0123") {
            true -> pinCodeView.onSuccess()
            false -> pinCodeView.onFail()
        }
    }
}