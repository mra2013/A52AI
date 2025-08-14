package com.example.a52ai
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(vm: ChatViewModel, onOpenSettings: ()->Unit, onMicClick: ()->Unit){
  val messages by vm.messages.collectAsStateWithLifecycle()
  val input by vm.input.collectAsStateWithLifecycle()
  val streaming by vm.isStreaming.collectAsStateWithLifecycle()
  Scaffold(topBar={ TopAppBar(title={ Text("A52 AI v3.1") }, actions={ IconButton(onClick=onOpenSettings){ Icon(Icons.Filled.Settings, null) } }) },
    bottomBar={ Row(Modifier.padding(8.dp), verticalAlignment=Alignment.CenterVertically){
      OutlinedTextField(value=input, onValueChange=vm::onInputChange, modifier=Modifier.weight(1f), placeholder={ Text("Type a message…") }, enabled=!streaming)
      IconButton(onClick=onMicClick, enabled=!streaming){ Icon(Icons.Filled.Mic, null) }
      IconButton(onClick={ vm.onUserSend() }, enabled=input.isNotBlank() && !streaming){ Icon(Icons.Filled.Send, null) }
    } }
  ){ padding->
    LazyColumn(Modifier.padding(padding).fillMaxSize().padding(horizontal=8.dp, vertical=4.dp)){
      items(messages, key={ it.id + it.ts }){ m->
        val bubble = if(m.fromUser) Color(0xFFDFF7C8) else Color(0xFFEDEDED)
        Row(Modifier.fillMaxWidth(), horizontalArrangement=if(m.fromUser) Arrangement.End else Arrangement.Start){
          Text(text=m.text, modifier=Modifier.padding(6.dp).clip(MaterialTheme.shapes.large).background(bubble).padding(10.dp))
        }
      }
      if(streaming){ item { Text("…", color=Color.Gray, modifier=Modifier.padding(10.dp)) } }
    }
  }
}