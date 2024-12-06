package com.example.bytebuilders.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.bytebuilders.R
import com.example.bytebuilders.databinding.ActivityLogginActiviyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLogginActiviyBinding
    private lateinit var auth: FirebaseAuth

    private var isPasswordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        binding = ActivityLogginActiviyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el botón "Log in"
        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (validateInput(email, password)) {
                signInWithEmailAndPassword(email, password)
            }
        }

        // Configurar la flecha para ir atrás
        binding.backArrow.setOnClickListener {
            navigateToMain()
            finish() // Finaliza la actividad
        }

        // Configurar el ícono de visibilidad de la contraseña
        binding.passwordInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.passwordInput.compoundDrawables[2] // Ícono al final
                if (drawableEnd != null) {
                    val touchAreaWidth = drawableEnd.bounds.width() + 40 // Aumenta el área clicable en 40px
                    if (event.rawX >= (binding.passwordInput.right - touchAreaWidth)) {
                        togglePasswordVisibility()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
        binding.newRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        // Cambia el tipo de entrada del campo de texto
        if (isPasswordVisible) {
            binding.passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visivility, 0)
        } else {
            binding.passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0)
        }

        // Coloca el cursor al final del texto
        binding.passwordInput.setSelection(binding.passwordInput.text?.length ?: 0)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMenu()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailInput.error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is required"
            return false
        }
        return true
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso, navega al menú
                    navigateToMenu()
                } else {
                    // Muestra un mensaje de error
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToMain() {
        // Aquí navegas al menú principal (actualiza con tu lógica)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finaliza esta actividad para que no se pueda volver con "atrás"
    }
    private fun navigateToMenu(){
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}
