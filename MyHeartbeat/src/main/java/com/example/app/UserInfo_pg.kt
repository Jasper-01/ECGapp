package com.example.app
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.SignInMethodQueryResult

private lateinit var auth: FirebaseAuth
@SuppressLint("StaticFieldLeak")
private lateinit var  googleSignInClient: GoogleSignInClient

class UserInfo_pg :AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_pg)
        auth = FirebaseAuth.getInstance()
        val google_sign = findViewById<Button>(R.id.google_signin)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE)
        val TF = sharedPreferences.getInt("T/F", 0)
        val email = sharedPreferences.getString("email", "0").toString()
        val full_name = sharedPreferences.getString("full_name", "0").toString()

        if(TF == 0){
            google_sign.setOnClickListener {
                signInGoogle()
            }
        }
        else{
            changes_F(full_name, email)
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        Log.d("DKMOBILE", "STARTING SIGN IN GOOGLE PASSED")
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            Log.d("DKMOBILE", "LAUNCHER PASSED")
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount = task.result
            if (account != null){
                Log.d("DKMOBILE", "HANDLERESULT_ACCOUNT PASSED")
                updateUI(account)
            }
        }
        else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        Log.d("DKMOBILE", "UPDATEUI PASSED")
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                changes_F(account.displayName.toString(), account.email.toString())
                sending_tomain(account)
                val intent = Intent(this, Main_pg::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changes_F(name: String, email: String){
        val nameDisplay = findViewById<TextView>(R.id.userNameDisplay)
        val email_display = findViewById<TextView>(R.id.userEmailDisplay)
        val device_name = findViewById<TextView>(R.id.deviceNameDisplay)
        val google_signin = findViewById<Button>(R.id.google_signin)
        nameDisplay.text = name
        email_display.text = email
        device_name.text = getDeviceName().toString()
        val layout = google_signin.parent as ViewGroup?
        layout?.removeView(google_signin)
    }

    private fun sending_tomain(account: GoogleSignInAccount){
        val sharedPref = getSharedPreferences("myKey", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("email", account.email.toString())
        editor.putString("full_name", account.displayName.toString())
        editor.putString("device_name", getDeviceName().toString())
        editor.putInt("T/F", 1)
        editor.apply()
    }

    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }


}