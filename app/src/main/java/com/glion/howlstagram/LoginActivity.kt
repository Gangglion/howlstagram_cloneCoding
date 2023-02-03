package com.glion.howlstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    lateinit var email_editText: EditText
    lateinit var password_editText: EditText
    lateinit var emailLogin_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        email_editText = findViewById(R.id.email_editText)
        password_editText = findViewById(R.id.password_editText)
        emailLogin_button = findViewById(R.id.emailLogin_Button)

        emailLogin_button.setOnClickListener{
            signinAndSignUp()
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

    private fun signinEmail(){
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
    private fun moveMainPage(user: FirebaseUser?){ // 로그인 성공시 다음 페이지로 넘어가는 함수
        if(user!=null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}