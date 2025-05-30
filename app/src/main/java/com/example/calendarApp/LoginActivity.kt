// LoginActivity.kt
package com.example.calendarApp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.SignInButton
import com.google.firebase.firestore.FirebaseFirestore


/** LoginActivity.kt
 * This file contains the login activity for the calendar app.
 * It is responsible for handling user login and registration.**/
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var googleSigninCliant: GoogleSignInClient
    private var GOOGLE_SIGN_IN_REQUEST_CODE = 101

    // This method is called when the activity is created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        // Initialize the UI elements.
        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        // Set up the login button click listener.
        loginButton.setOnClickListener{
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            login(email, password)
        }

        // Set up the register button click listener.
        registerButton.setOnClickListener{
            registerUpdate()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSigninCliant = GoogleSignIn.getClient(this, gso)

        findViewById<SignInButton>(R.id.googleSignInButton).setOnClickListener{
            val signInIntent = googleSigninCliant.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
        }


    }

//    activity for google sign in adding to firebase. using try catch to handle errors
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        val userId = auth.currentUser?.uid ?: return@addOnSuccessListener
                        val db = FirebaseFirestore.getInstance()

                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // Profile already exists, go to main screen
                                    startActivity(Intent(this, BottomNavActivity::class.java))
                                } else {
                                    // No profile found, go to complete profile screen
                                    startActivity(Intent(this, CompleteProfileActivity::class.java))
                                }
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error checking profile", Toast.LENGTH_SHORT).show()
                            }
                    }
            }catch (e: ApiException){
                Toast.makeText(this, "Google sign-in error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // login is called when the user clicks the login button.
    // It validates the user's credentials and logs them in if they are valid.
    private fun login(email:String, password: String){
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "@String/empty_fields", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { goToMainActivity()}
            .addOnFailureListener{
                Toast.makeText(this@LoginActivity, "@String/error_login_failed", Toast.LENGTH_SHORT).show()}
    }


    private fun registerUpdate() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    // goToMainActivity is called when the user logs in successfully.
    // It starts the main activity.
    private fun goToMainActivity(){
        val intent = Intent(this, BottomNavActivity::class.java)
        startActivity(intent)
        finish()
    }
}