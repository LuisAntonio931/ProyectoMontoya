package com.example.mione_app

import PostAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MIONE_APP.R
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var passwordET: EditText
    lateinit var emailET: EditText
    lateinit var passwordIcon: ImageView
    lateinit var localStorage: LocalStorage
    lateinit var btnLogin: Button
    lateinit var txtCreate: TextView

    lateinit var email_login: String
    lateinit var password_login: String

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        localStorage = LocalStorage(this@MainActivity)

        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)
        passwordIcon = findViewById(R.id.passwordIcon)
        btnLogin = findViewById(R.id.logButton)
        txtCreate = findViewById(R.id.iniciarSesionButton)

        val crearCuentaTextView: TextView = findViewById(R.id.iniciarSesionButton)
        crearCuentaTextView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        passwordET = findViewById(R.id.passwordET)
        passwordIcon = findViewById(R.id.passwordIcon)

        // Configuración para mostrar y ocultar la contraseña
        passwordIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible // Cambiar el estado

            if (isPasswordVisible) {
                passwordET.inputType = InputType.TYPE_CLASS_TEXT
                passwordIcon.setImageResource(R.drawable.eye_open)
            } else {
                passwordET.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordIcon.setImageResource(R.drawable.hide_password)
            }

            // Para mover el cursor al final después de cambiar el tipo de entrada
            passwordET.setSelection(passwordET.text.length)
        }

        btnLogin.setOnClickListener {
            // Obtener referencias a los EditText
            emailET = findViewById(R.id.emailET)
            passwordET = findViewById(R.id.passwordET)

            // Obtener el texto de los EditText
            email_login = emailET.text.toString()
            password_login = passwordET.text.toString()

            // Verificar si los campos están llenos
            if (email_login.isNotEmpty() && password_login.isNotEmpty()) {
                // Todos los campos están llenos, muestra el mensaje de "Sesión iniciada"
                showMessage("Sesión iniciada")
                sendLogin()
            } else {
                // Faltan campos por llenar, muestra el mensaje correspondiente
                showMessage("Faltan campos por llenar")
            }
        }

        // Configurar el RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val posts = listOf(
            Post("User1", "Developer", "https://example.com/profile1.jpg", "https://example.com/post1.jpg"),
            Post("User2", "Designer", "https://example.com/profile2.jpg", "https://example.com/post2.jpg")
            // Add more posts
        )

        val adapter = PostAdapter(posts)
        recyclerView.adapter = adapter
    }

    private fun showMessage(message: String) {
        // Muestra el mensaje usando Toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun sendLogin(){
        val parans = JSONObject()
        try {
            parans.put("email", email_login)
            parans.put("password", password_login)
        } catch (e: JSONException){
            e.printStackTrace()
        }
        val data: String = parans.toString()
        val url: String = "http://127.0.0.1:8000/api"+"/login";

        Thread(Runnable {
            fun run(){
                val http = Http(this@MainActivity, url)
                http.setMethod("post")
                http.setData(data)
                http.send()

                runOnUiThread(Runnable {
                    fun run(){
                        val code = http.statusCode
                        if(code==200){
                            try {
                                val response = JSONObject(http.response)
                                val token = response.getString("token")
                                println("TOKEEEN: "+ token)
                                localStorage.setToken(token)
                                println("Token almacenado en localStorage: "+ localStorage.getToken())

                            }catch (e: JSONException){
                                e.printStackTrace()
                            }
                        } else if(code==402){
                            try{
                                val response = JSONObject(http.response)
                                val msg = response.getString("message")
                                alertFail(msg, context = this)
                            }catch (e: JSONException){
                                e.printStackTrace()
                            }
                        }else if(code==401){
                            try{
                                val response = JSONObject(http.response)
                                val msg = response.getString("message")
                                alertFail(msg, context = this)
                            }catch (e: JSONException){
                                e.printStackTrace()
                            }
                        }else{
                            Toast.makeText(this@MainActivity, "Error "+ code, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }).start()
    }
}

fun alertFail(msg: String, context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Error")
        .setMessage(msg)
        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        .show()
}
