package mx.edu.ittepic.ladm_u1_p3_dealbaperez

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    private val vector: Array<Int> = Array(10, {0})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)

        btnAsignar.setOnClickListener{
            if(validarAsignacion()) {
                vector[posicion.text.toString().toInt()] = valor.text.toString().toInt()
                valor.setText("")
                posicion.setText("")
                resultado.text = ""
            }
        }

        btnMostrar.setOnClickListener {
            resultado.text = cadenaArreglo()
        }

        btnGuardar.setOnClickListener {
            if(validarNombreArchivo(nombreArchivoG, true)) {
                guardarArchivoSD()
                nombreArchivoG.setText("")
            }
        }

        btnLeer.setOnClickListener {
            if(validarNombreArchivo(nombreArchivoL, false)){
                if(leerArchivoSD()) {
                    var strAr = resultado.text.toString()
                    var lista: List<String> = strAr.substring(1, strAr.length-1).split(",")
                    (0..9).forEach {
                        if (it in 1..9)
                            vector[it] = lista[it].substring(1, lista[it].length).toInt()
                        else
                            vector[it] = lista[it].toInt()
                    }
                    nombreArchivoL.setText("")
                }
            }
        }
    }

    private fun validarNombreArchivo(textoEditable: EditText, guardar: Boolean):Boolean{
        var texto = textoEditable.text.toString()
        if(texto.isEmpty()){
            if(guardar)
                mensaje("Faltó introducir un nombre de archivo para guardar")
            else
                mensaje("Faltó introducir un nombre de archivo para leer")
            return false
        }
        if(texto.contains(" ")){
            if(guardar)
                mensaje("El nombre del archivo a guardar contiene espacios, favor de eliminarlos")
            else
                mensaje("El nombre del archivo a leer contiene espacios, favor de eliminarlos")
            return false
        }

        return true
    }

    private fun validarAsignacion():Boolean{
        if(valor.text.toString().isEmpty()){
            mensaje("Falta introducir un valor")
            return false
        }
        var pos = posicion.text.toString()
        if(pos.isEmpty()){
            mensaje("Falta introducir la posición")
            return false
        }
        if(pos.toInt()<0 || pos.toInt()>9){
            mensaje("La posición está fuera del rango del 0 a 9")
            return false
        }


        return true
    }

    private fun leerArchivoSD():Boolean{
        if(noSD()){
            mensaje("No hay memoria externa disponible :(")
            return false
        }
        try {
            val rutaSD = Environment.getExternalStorageDirectory()
            val flujo = File(rutaSD.absolutePath, nombreArchivoL.text.toString())
            val flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(flujo)))
            val data = flujoEntrada.readLine()
            resultado.text = data
            flujoEntrada.close()
        } catch (error: Exception) {
            mensaje("ERROR: "+error.message.toString())
            return false
        }
        return true
    }

    private fun cadenaArreglo():String{
        var cadAr = ""

        (0..9).forEach{col->
            if(col<9)
                cadAr += vector[col].toString()+", "
            else
                cadAr += vector[col]
        }
        return "[$cadAr]"

    }

    private fun guardarArchivoSD(){
        if(noSD()){
            mensaje("No hay memoria externa disponible :(")
            return
        }
        try {
            val rutaSD = Environment.getExternalStorageDirectory()
            var data = cadenaArreglo()
            val flujo = File(rutaSD.absolutePath, nombreArchivoG.text.toString())
            val flujoSalida = OutputStreamWriter(FileOutputStream(flujo))
            flujoSalida.write(data)
            flujoSalida.close()
            mensaje("¡El archivo ha sido creado con éxito!")
            valor.setText("")
            posicion.setText("")
            resultado.text = ""
            nombreArchivoG.setText("")
            nombreArchivoL.setText("")
        } catch (error: Exception) {
            mensaje("ERROR: "+error.message.toString())
        }
    }

    private fun mensaje(m: String){
        AlertDialog.Builder(this).
            setTitle("ATENCIÓN").
            setMessage(m).
            setPositiveButton("ACEPTAR"){d, i->}.
            show()
    }

    private fun noSD() : Boolean{
        return Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED
    }
}
