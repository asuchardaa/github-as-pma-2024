package com.example.myapp017avanocniappka.fragments

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.example.myapp017avanocniappka.R
import com.airbnb.lottie.LottieAnimationView
import java.text.SimpleDateFormat
import java.util.*

class GreetingFragment : Fragment(R.layout.fragment_greetings) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_greetings, container, false)

        val recipientName = view.findViewById<EditText>(R.id.recipientName)
        val customMessage = view.findViewById<EditText>(R.id.customMessage)
        val countdownText = view.findViewById<TextView>(R.id.countdown)
        val imageSpinner = view.findViewById<Spinner>(R.id.imageSpinner)
        val sendButton = view.findViewById<Button>(R.id.sendButton)
        val shareButton = view.findViewById<Button>(R.id.shareButton)
        val chosenImage = view.findViewById<ImageView>(R.id.chosenImage)

        val lottieView = view.findViewById<LottieAnimationView>(R.id.snowfallAnimationView)
        val toggleButton = view.findViewById<Button>(R.id.toggleAnimationButton)

        lottieView.setAnimation(R.raw.snowfall)
        lottieView.loop(true)
        lottieView.playAnimation()

        toggleButton.setOnClickListener {
            if (lottieView.isAnimating) {
                lottieView.pauseAnimation()
            } else {
                lottieView.playAnimation()
            }
        }

        startCountdown(countdownText)

        val imageOptions = listOf("Stromeček", "Sníh", "Sobi")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, imageOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        imageSpinner.adapter = adapter

        imageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> chosenImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.christmas_tree))
                    1 -> chosenImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.snow))
                    2 -> chosenImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.reindeer))
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                chosenImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.christmas_tree))
            }
        }

        sendButton.setOnClickListener {
            val recipient = recipientName.text.toString()
            val message = customMessage.text.toString()
            sendEmail(recipient, message)
        }

        shareButton.setOnClickListener {
            val recipient = recipientName.text.toString()
            val message = customMessage.text.toString()
            shareOnSocialMedia(recipient, message)
        }

        return view
    }

    private fun startCountdown(countdownTextView: TextView) {
        val christmasDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2024-12-25")!!
        val currentDate = Date()

        val countdownTimer = object : CountDownTimer(christmasDate.time - currentDate.time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val daysLeft = (millisUntilFinished / (1000 * 60 * 60 * 24)).toInt()
                val hoursLeft = (millisUntilFinished / (1000 * 60 * 60) % 24).toInt()
                val minutesLeft = (millisUntilFinished / (1000 * 60) % 60).toInt()
                val secondsLeft = (millisUntilFinished / 1000 % 60).toInt()

                countdownTextView.text = String.format(
                    Locale.getDefault(),
                    "Zbývá %d dní, %02d hodin, %02d minut a %02d sekund",
                    daysLeft, hoursLeft, minutesLeft, secondsLeft
                )
            }

            override fun onFinish() {
                countdownTextView.text = "Šťastné a veselé Vánoce!"
            }
        }

        countdownTimer.start()
    }

    private fun sendEmail(recipient: String, message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@example.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Vánoční přání")
        intent.putExtra(Intent.EXTRA_TEXT, "Ahoj $recipient, $message")
        startActivity(Intent.createChooser(intent, "Odeslat e-mail"))
    }

    private fun shareOnSocialMedia(recipient: String, message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Vánoční přání pro $recipient: $message")
        startActivity(Intent.createChooser(intent, "Sdílet"))
    }
}
