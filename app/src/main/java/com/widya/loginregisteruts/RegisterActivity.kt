package com.widya.loginregisteruts

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val passwordEditText = findViewById<EditText>(R.id.editPassword)
        val togglePassword = findViewById<ImageView>(R.id.ivTogglePassword)

        togglePassword.setOnClickListener {
            if (passwordEditText.inputType == (android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_show)
            } else {
                passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_hide)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Initialize database
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_db").build()

        val registerButton = findViewById<Button>(R.id.btnRegister)
        val nameEditText = findViewById<EditText>(R.id.editName)
        val emailEditText = findViewById<EditText>(R.id.editEmail)
        val phoneEditText = findViewById<EditText>(R.id.editPhone)
        val addressEditText = findViewById<EditText>(R.id.editAddress)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()

            // Validate email
            val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@gmail\\.com$")
            if (!email.matches(emailPattern)) {
                Toast.makeText(this, "Email tidak sesuai format(@gmail.com)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate password (min 8 karakter)
            if (password.isEmpty() || password.length < 8) {
                Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate nama
            if (name.isEmpty()) {
                Toast.makeText(this, "Name tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek apakah email sudah terdaftar
            CoroutineScope(Dispatchers.IO).launch {
                val existingUser = database.userDao().getUserByEmail(email)
                if (existingUser != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Email sudah ada!", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val user = User(name = name, email = email, password = password, phone = phone, address = address)
                database.userDao().insert(user)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Registrasi sukses!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        val loginLink = findViewById<TextView>(R.id.tvLoginLink)
        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}