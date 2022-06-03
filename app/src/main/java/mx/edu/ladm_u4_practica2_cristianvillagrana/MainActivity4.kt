package mx.edu.ladm_u4_practica2_cristianvillagrana

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.storage.FirebaseStorage
import mx.edu.ladm_u4_practica2_cristianvillagrana.databinding.ActivityMain4Binding
import java.util.*

class MainActivity4 : AppCompatActivity() {
    lateinit var binding : ActivityMain4Binding
    lateinit var imagen : Uri
    var codigo = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        codigo = intent.extras!!.getString("codigo")!!

        binding.btnelegir.setOnClickListener {
            val galeria = Intent(Intent.ACTION_GET_CONTENT)
            galeria.type = "image/*"
            startActivityForResult(galeria,100)
        }//elegir archivo


        binding.btnsubir.setOnClickListener {
            var nombreArchivo = ""
            val cal = GregorianCalendar.getInstance()
            val dialogo = ProgressDialog(this)

            dialogo.setMessage("Subiendo archivo...")
            dialogo.setCancelable(false)
            dialogo.show()
            nombreArchivo=cal.get(Calendar.YEAR).toString()+
                    cal.get(Calendar.MONTH).toString()+
                    cal.get(Calendar.DAY_OF_MONTH).toString()+
                    cal.get(Calendar.HOUR).toString()+
                    cal.get(Calendar.MINUTE).toString()+
                    cal.get(Calendar.SECOND).toString()+
                    cal.get(Calendar.MILLISECOND).toString()

            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("imagenes/${codigo}/${nombreArchivo}")

            storageRef.putFile(imagen)
                .addOnSuccessListener {
                    Toast.makeText(this,"Se subió", Toast.LENGTH_LONG).show()
                    binding.imagenup.setImageBitmap(null)
                    dialogo.dismiss()
                }
                .addOnFailureListener{
                    dialogo.dismiss()
                    AlertDialog.Builder(this).setMessage(it.message).show()
                }
        }//subir

        binding.btnvolver.setOnClickListener {
            finish()
        }
    }//onCreate

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            imagen = data!!.data!!
            binding.imagenup.setImageURI(imagen)
        }
    }

}//class