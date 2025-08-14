package com.example.a52ai
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
@Composable
fun SettingsScreen(onBack: ()->Unit, onSaved: ()->Unit){
  val ctx = LocalContext.current
  var base by remember { mutableStateOf(SecurePrefs.load(ctx).baseUrl) }
  var key by remember { mutableStateOf(SecurePrefs.load(ctx).apiKey) }
  var model by remember { mutableStateOf(SecurePrefs.load(ctx).model) }
  Scaffold(topBar={ TopAppBar(title={ Text("Settings") }) }){ padding->
    Column(Modifier.padding(padding).padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
      OutlinedTextField(value=base, onValueChange={ base=it }, label={ Text("Base URL") }, singleLine=true)
      OutlinedTextField(value=key, onValueChange={ key=it }, label={ Text("API Key") }, singleLine=true, visualTransformation=PasswordVisualTransformation())
      OutlinedTextField(value=model, onValueChange={ model=it }, label={ Text("Model") }, singleLine=true)
      Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
        Button(onClick={ SecurePrefs.save(ctx, Settings(base,key,model)); onSaved() }){ Text("Save") }
        OutlinedButton(onClick=onBack){ Text("Back") }
      }
    }
  }
}