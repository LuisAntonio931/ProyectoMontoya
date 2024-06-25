package com.example.mione_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.NetworkResponse
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.MIONE_APP.R
import org.json.JSONException
import org.json.JSONObject

var txtNombre:EditText?=null
var txtEmail:EditText?=null
var txtPassword:EditText?=null
var txtConfirmPassword:EditText?=null

class Register : AppCompatActivity() {

    private var isPasswordVisible = false
    private var isPasswordConfirmVisible = false

    lateinit var nameET: EditText

    lateinit var name: String
    lateinit var email: String
    lateinit var password: String
    lateinit var passwordConfirm: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        val iniciarSesionTextView: TextView = findViewById(R.id.iniciarSesionButton)
        iniciarSesionTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val passwordET: EditText = findViewById(R.id.passwordET)
        val passwordIcon: ImageView = findViewById(R.id.passwordIcon)

        val passwordConfirmET: EditText = findViewById(R.id.passwordConfirmET)
        val passwordConfirmIcon: ImageView = findViewById(R.id.passwordConfirmIcon)

        // Configuración para mostrar y ocultar la contraseña
        passwordIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordET.inputType = InputType.TYPE_CLASS_TEXT
                passwordIcon.setImageResource(R.drawable.eye_open)
            } else {
                passwordET.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordIcon.setImageResource(R.drawable.hide_password)
            }
            passwordET.setSelection(passwordET.text.length)
        }

        // Configuración para mostrar y ocultar la confirmación de contraseña
        passwordConfirmIcon.setOnClickListener {
            isPasswordConfirmVisible = !isPasswordConfirmVisible
            if (isPasswordConfirmVisible) {
                passwordConfirmET.inputType = InputType.TYPE_CLASS_TEXT
                passwordConfirmIcon.setImageResource(R.drawable.eye_open)
            } else {
                passwordConfirmET.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordConfirmIcon.setImageResource(R.drawable.hide_password)
            }
            passwordConfirmET.setSelection(passwordConfirmET.text.length)
        }

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            // Obtener referencias a los EditText
            val nameET: EditText = findViewById(R.id.nameET)
            val emailET: EditText = findViewById(R.id.emailET)
            val passwordET: EditText = findViewById(R.id.passwordET)
            val passwordConfirmET: EditText = findViewById(R.id.passwordConfirmET)

            // Obtener el texto de los EditText
            name = nameET.text.toString().trim()
            email = emailET.text.toString().trim()
            password = passwordET.text.toString().trim()
            passwordConfirm = passwordConfirmET.text.toString().trim()

            // Verificar si todos los campos están llenos
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty()) {
                // Verificar si las contraseñas coinciden
                if (password == passwordConfirm) {
                    // Todas las validaciones pasaron, muestra el mensaje de "Registro exitoso"
                    showMessage("Registro exitoso")
                    //sendRegister()
                    //Volley
                    val url = "http://192.168.84.170:8000/api/register"
                    val body = JSONObject().apply {
                        put("data", JSONObject().apply {
                            put("attributes", JSONObject().apply {
                                put("name", "$name")
                                put("email", "$email")
                                put("password", "$password")
                                put("password_confirmation", "$passwordConfirm")
                            })
                        })
                    }
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        body,
                        Response.Listener { response ->
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        },
                        Response.ErrorListener { error ->
                            handleVolleyError(error)
                        }
                    ) {
                        override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                            return try {
                                val jsonString = String(response!!.data, charset(HttpHeaderParser.parseCharset(response.headers)))
                                Log.d("API_RESPONSE", "Response: $jsonString")
                                if (jsonString.trim().startsWith("{")) {
                                    Response.success(JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response))
                                } else {
                                    Log.e("API_ERROR", "La respuesta no es un JSON válido")
                                    Response.error(ParseError(JSONException("La respuesta no es un JSON válido")))
                                }
                            } catch (e: JSONException) {
                                Log.e("API_ERROR", "JSON Exception: $e")
                                Response.error(ParseError(e))
                            }
                        }
                    }
                    val requestQueue = Volley.newRequestQueue(this)
                    requestQueue.add(jsonObjectRequest)

                } else {
                    showMessage("Las contraseñas no coinciden")
                }
            } else {
                // Faltan campos por llenar, muestra el mensaje correspondiente
                showMessage("Faltan campos por llenar")
            }
        }
    }

    private fun showMessage(message: String) {
        // Muestra el mensaje usando Toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleVolleyError(error: VolleyError) {
        if (error is NoConnectionError || error is TimeoutError) {
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        } else if (error.networkResponse != null) {
            val statusCode = error.networkResponse.statusCode
            val errorMessage = String(error.networkResponse.data)
            Toast.makeText(this, "Error $statusCode: $errorMessage", Toast.LENGTH_SHORT).show()
            Log.e("API_ERROR", "Error $statusCode: $errorMessage")
        } else {
            Toast.makeText(this, "Error desconocido: ${error.message}", Toast.LENGTH_SHORT).show()
            Log.e("API_ERROR", "Error desconocido: ${error.message}")
        }
    }

    /*private fun sendRegister() {
        val nameET: EditText = findViewById(R.id.nameET)
        val emailET: EditText = findViewById(R.id.emailET)
        val passwordET: EditText = findViewById(R.id.passwordET)
        val passwordConfirmET: EditText = findViewById(R.id.passwordConfirmET)

        name = nameET.text.toString()
        email = emailET.text.toString()
        password = passwordET.text.toString()
        passwordConfirm = passwordConfirmET.text.toString()
        val params = JSONObject()
        try {
            val data = JSONObject()
            data.put("type", "users")  // El tipo de recurso
            val attributes = JSONObject()
            attributes.put("name", name)
            attributes.put("email", email)
            attributes.put("password", password)
            attributes.put("password_confirmation", passwordConfirm)
            data.put("attributes", attributes)
            params.put("data", data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val requestData: String = params.toString()
        val url: String = "http://10.0.2.2:8000/api/register"

        Thread(Runnable {
            val http = Http(this@Register, url)
            http.setMethod("POST")
            http.setData(requestData)
            http.send()

            runOnUiThread {
                val code = http.getStatusCode()
                if (code == 201 || code == 200) {
                    alertSuccess("Register Complete", this)
                } else if (code == 422) {
                    try {
                        val response = JSONObject(http.getResponse())
                        val msg = response.getString("message")
                        alertFail(msg, this)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this@Register, "Error $code", Toast.LENGTH_SHORT).show()
                }
            }
        }).start()
    }

    private fun alertSuccess(msg: String, context: Context) {
        AlertDialog.Builder(context)
            .setTitle("SUCCESS")
            .setMessage(msg)
            .setPositiveButton("Login") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun alertFail(msg: String, context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(msg)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }*/

}

