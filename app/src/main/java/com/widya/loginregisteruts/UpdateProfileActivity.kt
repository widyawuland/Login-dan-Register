package com.widya.loginregisteruts

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.widya.loginregisteruts.com.widya.loginregisteruts.HomeActivity
import kotlinx.coroutines.*

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private var currentUserId: Int = -1
    private var currentPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

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

        val cancelButton = findViewById<Button>(R.id.btnCancel)

        cancelButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        SessionManager.init(applicationContext)

        val email = SessionManager.getUserEmail()
        if (email == null) {
            finish()
            return
        }

        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_db").build()

        val nameEditText = findViewById<EditText>(R.id.editName)
        val emailEditText = findViewById<EditText>(R.id.editEmail)
        val phoneEditText = findViewById<EditText>(R.id.editPhone)
        val addressEditText = findViewById<EditText>(R.id.editAddress)
        val updateButton = findViewById<Button>(R.id.btnUpdate)

        CoroutineScope(Dispatchers.IO).launch {
            val user = database.userDao().getUserByEmail(email)
            user?.let {
                currentUserId = it.id
                currentPassword = it.password
                withContext(Dispatchers.Main) {
                    nameEditText.setText(it.name)
                    emailEditText.setText(it.email)
                    phoneEditText.setText(it.phone)
                    addressEditText.setText(it.address)
                    passwordEditText.setText(it.password)
                }
            }
        }

        updateButton.setOnClickListener {
            val updatedPassword = passwordEditText.text.toString()
            val finalPassword = if (updatedPassword.isNotEmpty()) updatedPassword else currentPassword

            val updatedUser = User(
                id = currentUserId,
                name = nameEditText.text.toString(),
                email = emailEditText.text.toString(),
                password = finalPassword,
                phone = phoneEditText.text.toString(),
                address = addressEditText.text.toString()
            )

            CoroutineScope(Dispatchers.IO).launch {
                database.userDao().update(updatedUser)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UpdateProfileActivity, "Update profile sukses!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}