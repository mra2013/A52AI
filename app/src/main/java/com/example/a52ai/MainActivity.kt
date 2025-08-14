package com.example.a52ai
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale
class MainActivity : ComponentActivity(){
  private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
    if(result.resultCode==RESULT_OK){
      val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
      if(text.isNotBlank()) _pendingSpeechText?.invoke(text)
    }
  }
  private var _pendingSpeechText: ((String)->Unit)? = null
  private val requestMicPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ _-> }
  override fun onCreate(savedInstanceState: Bundle?){ super.onCreate(savedInstanceState)
    setContent{
      MaterialTheme{
        var showSettings by remember{ mutableStateOf(false) }
        val vm: ChatViewModel = viewModel()
        if(showSettings){ SettingsScreen(onBack={ showSettings=false }, onSaved={ showSettings=false }) }
        else { ChatScreen(vm=vm, onOpenSettings={ showSettings=true }, onMicClick={ startSpeechToText{ vm.onUserSend(it) } }) }
      }
    }
  }
  private fun startSpeechToText(onResult:(String)->Unit){
    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
      requestMicPermission.launch(Manifest.permission.RECORD_AUDIO); return
    }
    if(!SpeechRecognizer.isRecognitionAvailable(this)) return
    _pendingSpeechText = onResult
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
      putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now…")
    }
    speechLauncher.launch(intent)
  }
}