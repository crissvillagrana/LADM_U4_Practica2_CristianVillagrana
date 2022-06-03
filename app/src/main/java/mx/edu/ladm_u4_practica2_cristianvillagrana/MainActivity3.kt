package mx.edu.ladm_u4_practica2_cristianvillagrana

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mx.edu.ladm_u4_practica2_cristianvillagrana.databinding.ActivityMain3Binding
import java.util.*

class MainActivity3 : AppCompatActivity() {
    lateinit var binding : ActivityMain3Binding
    var clave = ""
    lateinit var imagen : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        clave = intent.extras!!.getString("clave")!!

        binding.btnsubir.setOnClickListener {
            val evento = FirebaseFirestore.getInstance().collection("eventos")
            var data = hashMapOf(
                "creador" to clave,
                "carpeta" to binding.txtalbum.text.toString(),
                "cerrado" to false,
                "oculto" to false
            )
            evento.document(binding.txtalbum.text.toString()).set(data)
                .addOnSuccessListener {
                    Toast.makeText(this,"Se creó el evento",Toast.LENGTH_LONG).show()
                    binding.txtalbum.text.clear()
                }
        }

        binding.btnvolver.setOnClickListener { finish() }
    }//onCreate


}