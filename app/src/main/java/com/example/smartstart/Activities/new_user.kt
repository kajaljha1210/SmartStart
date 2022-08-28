package com.example.smartstart.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.smartstart.R


import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_user.*
import java.util.*


class new_user : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    lateinit var callbackManager: CallbackManager
    
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        database= FirebaseDatabase.getInstance().getReference("Sign Up")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        FirebaseApp.initializeApp(this)

        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
        callbackManager = CallbackManager.Factory.create()
        SignUp.setOnClickListener {
            val firstname = firstname.text.toString()
            val lastname = lastname.text.toString()
            val email = emaill.text.toString()
            val password = passwordd.text.toString()

            val user_id=database.push().key!!
            val user=User(firstname,lastname,email,password)
            database.child(user_id).setValue(user).addOnSuccessListener {
                Toast.makeText(applicationContext, "Sign Up Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Index::class.java)
                startActivity(intent)}.addOnFailureListener {
                Toast.makeText(applicationContext, "Sign Up failed", Toast.LENGTH_SHORT).show()

            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        Signup_Google.setOnClickListener { view: View? ->
            signInGoogle()
        }
        alreadyuser.setOnClickListener {
            startActivity(Intent(this@new_user, Index::class.java))
        }

        logg.setOnClickListener {
            val EMAIL = "email"

            login_button_inv.setReadPermissions(Arrays.asList(EMAIL))
            // If you are using in a fragment, call loginButton.setFragment(this);

            // Callback registration
            // If you are using in a fragment, call loginButton.setFragment(this);

            // Callback registration
            login_button.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                    }

                    override fun onSuccess(result: LoginResult?) {
                        startActivity(Intent(this@new_user, Index::class.java))
                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                    }
                })
            login_button.performClick()
        }
    }

        //Google Sign in

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Logged In Successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Index::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            startActivity(
                Intent(
                    this, Index
                    ::class.java
                )
            )
            finish()
        }
    }
}

private fun AppEventsLogger.Companion.activateApp(application: new_user) {

}
