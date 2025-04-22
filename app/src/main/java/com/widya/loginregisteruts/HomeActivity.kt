package com.widya.loginregisteruts.com.widya.loginregisteruts

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.widya.loginregisteruts.AppDatabase
import com.widya.loginregisteruts.LoginActivity
import com.widya.loginregisteruts.R
import com.widya.loginregisteruts.SessionManager
import com.widya.loginregisteruts.UpdateProfileActivity
import kotlinx.coroutines.*

class HomeActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var addressTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        SessionManager.init(applicationContext)

        val email = SessionManager.getUserEmail()
        if (email == null) {
            finish()
            return
        }

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).build()

        // Inisialisasi View
        nameTextView = findViewById(R.id.textName)
        emailTextView = findViewById(R.id.textEmail)
        phoneTextView = findViewById(R.id.textPhone)
        addressTextView = findViewById(R.id.textAddress)
        val updateButton = findViewById<Button>(R.id.btnUpdateProfile)
        val logoutIcon = findViewById<ImageView>(R.id.ivLogout)

        // Navigasi ke Update Profile
        updateButton.setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        // Logout kemudian kembali ke LoginActivity
        logoutIcon.setOnClickListener {
            SessionManager.setLoginState(false)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        val email = SessionManager.getUserEmail() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val user = database.userDao().getUserByEmail(email)
            withContext(Dispatchers.Main) {
                user?.let {
                    nameTextView.text = "Nama    : ${it.name}"
                    emailTextView.text = "Email   : ${it.email}"
                    phoneTextView.text = "No Hp   : ${it.phone}"
                    addressTextView.text = "Alamat : ${it.address}"
                }
            }
        }
    }
}