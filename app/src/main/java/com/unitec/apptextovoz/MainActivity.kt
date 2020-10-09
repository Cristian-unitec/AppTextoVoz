package com.unitec.apptextovoz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    //Este objeto es el intermediario entre nuestra app y TestToSpeech
    private var tts: TextToSpeech?=null
    //El siguiente codigo de peticion es un entero, que nos va a ayudar a garantizar el objeto TextToSpeech
    //Se inicio completamente
    private val CODIGO_PETICION=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //INICIALIZAMOS LA VARIABLE tts
        tts= TextToSpeech(this,this)

        //Boton para hablar
        btn_hablar.setOnClickListener {
            val intent= Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            try {
                startActivityForResult(intent,CODIGO_PETICION)
            }catch (e:Exception){}
        }
        //Boton para leer el texto
        btn_interpretar.setOnClickListener {
            if (etxt_frase.text.isEmpty()){
                Toast.makeText(this,"Debes escribir algo para que lo hable",Toast.LENGTH_LONG).show()
            }else{
                hablarTexto(etxt_frase.text.toString())
            }
        }


        Timer("Bienvenida",false).schedule(1000){
            tts!!.speak(
                    "Hola, bienvenido a la aplicación UNITEC, oprime el botón para hablar!!",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    ""
            )
        }
    }

    private fun hablarTexto(texto: String) {
        tts!!.speak(texto,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    override fun onInit(status: Int) {
        //ESTE METODO NOS AYUDA A INICIALIZAR LA CONFIGURACION AL ARRANCAR LA APP.(IDIOMA)
        if(status==TextToSpeech.SUCCESS){
            var local=Locale("spa","MEX")
            //ESTA VARIABLE NOS INDICA QUE LA APP VA BIEN
            val resultado=tts!!.setLanguage(local)
            if(resultado==TextToSpeech.LANG_MISSING_DATA){
                Log.i("MALO","NO Funciono el lenguaje")
            }
        }
    }

    //Metodo Opcional para limpiar memoria
    override fun onDestroy() {
        super.onDestroy()
        if(tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CODIGO_PETICION->{
                if(resultCode== RESULT_OK && null!=data) {
                    val result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    txt_interpretado.setText(result!![0])
                }
            }
        }
    }
}