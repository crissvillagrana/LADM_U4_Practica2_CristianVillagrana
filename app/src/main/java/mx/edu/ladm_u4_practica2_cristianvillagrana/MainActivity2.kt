package mx.edu.ladm_u4_practica2_cristianvillagrana

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import mx.edu.ladm_u4_practica2_cristianvillagrana.databinding.ActivityMain2Binding
import java.io.File
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    var listaNombres = ArrayList<String>()
    var clave = ""
    var cerrado = false
    var oculto = false
    var existe = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnsubir.isEnabled = false
        binding.btnbloquear.isEnabled = false
        binding.btncerrar.isEnabled = false
        binding.btncargar.isEnabled = false

            if(FirebaseAuth.getInstance().currentUser!=null){
                var emailUsuario = FirebaseAuth.getInstance().currentUser?.email.toString()
                var corre = emailUsuario.split("@")
                clave = corre[0]
                binding.txtUsuario.setText("Bienvenido ${clave}")
            }else{
                startActivity(Intent(this,MainActivity::class.java))
            }

        //-----------------------CREAR EVENTO----------------------------
        binding.btncrear.setOnClickListener {
            startActivity(Intent(this,MainActivity3::class.java).putExtra("clave",clave))
        }

        //------------------------------ENCONTRAR EL EVENTO--------------------------------------------
        binding.btnbuscar.setOnClickListener {
            verificarPermisos()
            //directorio(binding.txtcodigo.text.toString(),clave)
            //binding.galeria.setImageBitmap(null)
        }

        //---------------------------Añadir Archivos al Evento-----------------------------------------
        binding.btnsubir.setOnClickListener {
            if(cerrado==false){
                startActivity(Intent(this,MainActivity4::class.java).putExtra("codigo",binding.txtcodigo.text.toString()))
            }else{
                alerta("El evento está cerrado")
            }
        }

        //---------------------------OCULTAR EVENTO--------------------------------------------------------------------------
        binding.btnbloquear.setOnClickListener {
            ocultarEvento()
        }

        //----------------------------CERRAR EVENTO--------------------------------------------------------------------------
        binding.btncerrar.setOnClickListener {
            cerrarEvento()
        }

        //---------------------------CARGAR FOTOS DEL EVENTO--------------------------------------------
        binding.btncargar.setOnClickListener {
            if(!cerrado){
               directorio(binding.txtcodigo.text.toString())
            }
        }


    }//onCreate

    fun verificarPermisos(){
        var codigo = binding.txtcodigo.text.toString()
        val baseRemota = FirebaseFirestore.getInstance()
        val documento = baseRemota.collection("eventos").document(codigo)
        documento.get()
            .addOnSuccessListener { doc ->
                if(doc!=null){
                    if(doc.getBoolean("oculto")==true){
                        oculto = true
                        alerta("No se encontró el evento")
                        negar()
                        return@addOnSuccessListener
                    }else{
                        if(doc.getString("carpeta").equals(codigo)){
                            Toast.makeText(this,"Evento encontrado",Toast.LENGTH_LONG).show()
                            existe=true
                            permitirSubir()
                            //directorio(codigo)
                            if(doc.getString("creador").equals(clave)){
                                darPermisosadmin()
                            }
                            if(doc.getBoolean("cerrado")==false){
                                cerrado = false
                            }else{
                                cerrado = true
                                binding.btnsubir.isEnabled = false
                            }
                        }else{
                            alerta("No se encontró el evento")
                            existe = false
                        }
                        bloquearCarga()
                    }

                }else{
                    alerta("No se encontró el evento")
                }
            }
            .addOnFailureListener {
                alerta("Error de conexión")
            }
    }//verificarPermisos

    private fun negar() {
        binding.btnsubir.isEnabled = false
        binding.btnbloquear.isEnabled = false
        binding.btncerrar.isEnabled = false
    }

    private fun permitir(){
        binding.btnsubir.isEnabled = true
        binding.btnbloquear.isEnabled = true
        binding.btncerrar.isEnabled = true
    }

    fun ocultarEvento(){
        val baseRemota = FirebaseFirestore.getInstance()
        val documento = baseRemota.collection("eventos").document(binding.txtcodigo.text.toString())

        if(cerrado){
            documento.update("oculto",true)
                .addOnSuccessListener {
                    Toast.makeText(this,"Evento ocultado",Toast.LENGTH_LONG).show()
                    oculto = true
                }
        }
        verificarPermisos()
    }

    fun cerrarEvento(){
        if(cerrado){
            cerrado=false
        }else{
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("eventos").document(binding.txtcodigo.text.toString())
                .update("cerrado",true).addOnSuccessListener {
                    alerta("Evento cerrado")
                }
            cerrado=true
        }

        verificarPermisos()
    }

    fun directorio(codigo:String){
        val storageRef = FirebaseStorage.getInstance().reference.child("imagenes/"+codigo)
        storageRef.listAll()
            .addOnSuccessListener {
                listaNombres.clear()
                it.items.forEach {
                    listaNombres.add(it.name)
                }
                binding.txtx.setText("Fotos del album:")
                binding.lista.adapter = ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, listaNombres)
                cargarImagenes(listaNombres.get(0))
                binding.lista.setOnItemClickListener { adapterView, view, i, l ->
                    cargarImagenes(listaNombres.get(i))
                }
            }
            .addOnFailureListener {
                alerta("El evento no tiene fotos")
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menuoculto,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.salir->{
                finish()
            }
            R.id.acerca->{
                alerta("Esta app fue desarrollada por: \nCristian A. Chávez Villagrana" +
                        "\ny El constante miedo de reprobar ")
            }
            R.id.session->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
        return true
    }

    fun alerta(cade : String){
        AlertDialog.Builder(this).setMessage(cade).show()
    }

    fun bloquearCarga(){
        if(existe==true && cerrado==false){
            binding.btncargar.isEnabled = true
        }else{
            binding.btncargar.isEnabled = false
        }
    }



    fun cargarImagenes(nombreArchivo:String){
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("imagenes/"+binding.txtcodigo.text.toString()+"/${nombreArchivo}")

        val archivoTemportal = File.createTempFile("imagenTemp","jpg")
        storageRef.getFile(archivoTemportal)
            .addOnSuccessListener {
                val mapadeBits = BitmapFactory.decodeFile(archivoTemportal.absolutePath)
                binding.galeria.setImageBitmap(mapadeBits)
            }
            .addOnFailureListener{
                alerta("No se pudo cargar la imagen")
            }

    }


    fun darPermisosadmin(){
        binding.btncerrar.isEnabled = true
        binding.btnbloquear.isEnabled = true
    }

    fun negarPermisosadmin(){
        binding.btncerrar.isEnabled = true
        binding.btnbloquear.isEnabled = true
    }

    fun permitirSubir(){
        binding.btnsubir.isEnabled = true
    }

    fun negarSubir(){
        binding.btnsubir.isEnabled = true
    }

}