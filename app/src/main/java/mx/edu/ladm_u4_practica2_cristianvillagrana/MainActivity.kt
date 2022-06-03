package mx.edu.ladm_u4_practica2_cristianvillagrana

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import mx.edu.ladm_u4_practica2_cristianvillagrana.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Verificar si ya inició sesión
        if(FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this,MainActivity2::class.java))
        }
        //Registrarse
        binding.inscribir.setOnClickListener {
            val autentication = FirebaseAuth.getInstance()
            autentication.createUserWithEmailAndPassword(
                binding.txtcorreo.text.toString(),
                binding.contrasena.text.toString()
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    limpiar()
                    Toast.makeText(this,"Registrado correctamente", Toast.LENGTH_LONG).show()
                    autentication.signOut()
                }else{
                    alerta()
                }
            }
        }//registrarse

        //Iniciar sesión
        binding.autenticar.setOnClickListener {
            val autentication = FirebaseAuth.getInstance()
            val dialogo = ProgressDialog(this)
            dialogo.setMessage("Iniciando sesión")
            dialogo.setCancelable(false)
            dialogo.show()

            autentication.signInWithEmailAndPassword(binding.txtcorreo.text.toString(),binding.contrasena.text.toString())
                .addOnCompleteListener {
                    dialogo.dismiss()
                    if(it.isSuccessful){
                        abrirVentana(binding.txtcorreo.text.toString())
                        return@addOnCompleteListener
                    }
                    alerta()
                }
        }//iniciar sesión

        //Recuperar contraseña
        binding.recuperar.setOnClickListener {
            val autentication = FirebaseAuth.getInstance()
            val dialogo = ProgressDialog(this)
            dialogo.setCancelable(false)
            dialogo.setMessage("Solicitando recuperación")
            dialogo.show()
            autentication.sendPasswordResetEmail(binding.txtcorreo.text.toString())
                .addOnSuccessListener {
                    dialogo.dismiss()
                    AlertDialog.Builder(this).setMessage("Revisa tu correo para recuperar tu contraseña").show()
                }
        }//recuperar
    }//OnCreate-------------------------------------------------------

    private fun abrirVentana(correo:String) {
        startActivity(Intent(this,MainActivity2::class.java).putExtra("correologin",correo))
    }

    fun limpiar(){
        binding.txtcorreo.text.clear()
        binding.contrasena.text.clear()
    }
    fun alerta(){
        AlertDialog.Builder(this).setTitle("Error").setMessage("No se pudo completar la operación")
            .show()
    }
}