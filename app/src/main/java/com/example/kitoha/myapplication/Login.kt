package com.example.kitoha.myapplication

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.database.*


class Login:AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    lateinit var mGoogleApiClient:GoogleApiClient
    private val mStatusTextView:TextView get() = findViewById(R.id.status)
    var mProgressDialog:ProgressDialog ? = null
    private val sign_in_btn:SignInButton get() = findViewById(R.id.sign_in_button)
    private val sign_out_btn:Button get() =  findViewById(R.id.sign_out_button)
    private val sign_out_disconnect:LinearLayout get() = findViewById(R.id.sign_out_and_disconnect)
    private val start_btn:Button get() = findViewById(R.id.start)
    lateinit var mAuth:FirebaseAuth
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.getReference()
    val user: FirebaseUser = FirebaseAuth.getInstance().getCurrentUser() as FirebaseUser

    lateinit var dialog:AlertDialog

     override fun onCreate(savedInstanceState:Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_login)

         val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                 .requestEmail()
                 .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
         val signInButton:SignInButton ? = findViewById<SignInButton?>(R.id.sign_in_button)
         if(signInButton!=null) {
             signInButton.setSize(SignInButton.SIZE_STANDARD)
             signInButton.setScopes(gso.scopeArray)
         }

         mAuth=FirebaseAuth.getInstance()

         sign_in_btn?.setOnClickListener {
             signIn()
         }
         sign_out_btn?.setOnClickListener {
             signOut()
         }
         start_btn.setOnClickListener {

             val id_query:Query=myRef.child("users").orderByChild("user_id").equalTo(user.uid.toString())

             id_query.addListenerForSingleValueEvent(object : ValueEventListener {
                 override fun onCancelled(p0: DatabaseError?) {}
                 override fun onDataChange(dataSnapshot: DataSnapshot) {
                     val id_child:Iterator<DataSnapshot> = dataSnapshot.getChildren().iterator()

                     while(id_child.hasNext()){
                         val intent:Intent=Intent(applicationContext,MainActivity::class.java)
                         startActivity(intent)
                         id_query.removeEventListener(this)
                         return
                     }
                     ShowDialog()
                 }
             })


            /* val intent:Intent=Intent(this,MainActivity::class.java)
             startActivity(intent)*/
         }
     }
    fun ShowDialog(){
        val ad:AlertDialog.Builder=AlertDialog.Builder(this)
        ad.setTitle("닉네임 설정")
        ad.setTitle("닉네임을 입력하세요.")

        val nickname:EditText= EditText(this)
        ad.setView(nickname)

        ad.setPositiveButton("확인", DialogInterface.OnClickListener{dialog, whichButton ->
        })

        ad.setNegativeButton("취소", DialogInterface.OnClickListener{dialog, whichButton ->
            dialog.dismiss()
        })

        dialog=ad.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(object :  View.OnClickListener{
            override fun onClick(p0: View?) {
                var chk:Int=0
                val nick:String = nickname.text.toString()
                val log_data:LoginData= LoginData()
                log_data.user_id=user.uid.toString()
                log_data.user_name=nick
                //myRef.child("users").push().setValue(log_data)
                val query:Query=myRef.child("users").orderByChild("user_name").equalTo(nick)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Toast.makeText(applicationContext, "아이디가 존재합니다.", Toast.LENGTH_SHORT).show()
                        val child:Iterator<DataSnapshot> = dataSnapshot.getChildren().iterator()

                        while(child.hasNext()){
                            Toast.makeText(applicationContext, "아이디가 존재합니다.", Toast.LENGTH_SHORT).show()
                            nickname.setText("")
                            query.removeEventListener(this)
                            return
                        }
                        myRef.child("users").push().setValue(log_data)
                        Toast.makeText(applicationContext, "아이디가 생성됬습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                })

            }
        })


    }

     public override fun onStart() {
         super.onStart()
         val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
         if (opr.isDone)
         {
            Log.d(TAG, "Got cached sign-in")
             val result = opr.get()
             handleSignInResult(result)
         }
         else
         {
             showProgressDialog()
             opr.setResultCallback { googleSignInResult ->
                 hideProgressDialog()
                 handleSignInResult(googleSignInResult)
             }
         }
     }


    public override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent) {
     super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN)
        {
            val result:GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
 }

     private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) { // data transmit from android to goole

         val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
         mAuth.signInWithCredential(credential)
                 .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                     if (task.isSuccessful) {
                         // Sign in success, update UI with the signed-in user's information
                         val user = mAuth.getCurrentUser()
                     } else {
                         Toast.makeText(this@Login, "Authentication failed.",
                                 Toast.LENGTH_SHORT).show()
                     }

                 })
     }

    private fun handleSignInResult(result:GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess)
        {
            val acct = result.signInAccount
            mStatusTextView!!.text = acct!!.displayName
            firebaseAuthWithGoogle(acct)
            updateUI(true)
        }
        else
        {
            updateUI(false)
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            updateUI(false)
        }
    }
    private fun revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                object:ResultCallback<Status> {
                    override fun onResult(status:Status) {
                        updateUI(false)
                    }
                })
    }

    override fun onConnectionFailed(connectionResult:ConnectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult)
    }
     private fun showProgressDialog() {
         if (mProgressDialog == null)
         {
             mProgressDialog = ProgressDialog(this)
             mProgressDialog!!.setMessage(getString(R.string.loading))
             mProgressDialog!!.isIndeterminate = true
         }
         mProgressDialog!!.show()
     }

     private fun hideProgressDialog() {
         if (mProgressDialog != null && mProgressDialog!!.isShowing)
         {
             mProgressDialog!!.hide()
         }
     }

     private fun updateUI(signedIn:Boolean) {
         if (signedIn)
         {
             sign_in_btn.setVisibility(View.GONE)
             sign_out_disconnect.setVisibility(View.VISIBLE)
             start_btn.setVisibility(View.VISIBLE)
         }
         else
         {
             mStatusTextView!!.setText(R.string.signed_out)
             sign_in_btn.setVisibility(View.VISIBLE)
             sign_out_disconnect.setVisibility(View.GONE)
             start_btn.setVisibility(View.GONE)
         }
     }

     companion object {
         private val TAG = "LoinActivity"
         private val RC_SIGN_IN = 9001
     }
}
