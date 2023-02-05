package com.glion.howlstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var googleSigninClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    lateinit var email_editText: EditText
    lateinit var password_editText: EditText
    lateinit var emailLogin_button: Button
    lateinit var google_sign_in_Button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        email_editText = findViewById(R.id.email_editText)
        password_editText = findViewById(R.id.password_editText)
        emailLogin_button = findViewById(R.id.emailLogin_Button)
        google_sign_in_Button = findViewById(R.id.google_sign_in_Button)

        emailLogin_button.setOnClickListener {
            signinAndSignUp()
        }
        google_sign_in_Button.setOnClickListener{
            // 구글 로그인은 3단계로 이루어져 있음. 그중 첫번째 단계
            googleLogin()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSigninClient = GoogleSignIn.getClient(this,gso)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                val account = result.signInAccount
                // 첫번쨰 단계 이후 가져온 결과값을 파이어베이스에 넘겨주기 위함.
                // 두번쨰 단계 : 파이어베이스에 구글 로그인 결과값을 넘겨줌
                firebaseWithAuthGoogle(account)
            }
        }
    }
    private fun firebaseWithAuthGoogle(account : GoogleSignInAccount?){
        val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Login
                    moveMainPage(it.result.user)
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun googleLogin(){
        val googleSignInIntent = googleSigninClient?.signInIntent
        if (googleSignInIntent != null) {
            startActivityForResult(googleSignInIntent,GOOGLE_LOGIN_CODE)
        }
    }

    private fun signinAndSignUp() {
        // addOnCompleteListener : 회원가입한 결과값을 받아오기 위함
        auth?.createUserWithEmailAndPassword(
            email_editText.text.toString(),
            password_editText.text.toString()
        )
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // 유저 정보 생성 완료
                    moveMainPage(it.result.user)
                } else if (!it.exception?.message.isNullOrEmpty()) { // 로그인 에러 났을때 발생
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                } else {
                    // 회원가입도 아니고 에러메세지도 아닐때 로그인 화면으로 빠지는 부분
                    signinEmail()
                }
            }
    }

    private fun signinEmail() {
        auth?.signInWithEmailAndPassword(
            email_editText.text.toString(),
            password_editText.text.toString()
        )
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Login
                    moveMainPage(it.result.user)
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun moveMainPage(user: FirebaseUser?) { // 로그인 성공시 다음 페이지로 넘어가는 함수
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}