package com.example.a52ai
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
class ChatViewModel(app: Application): AndroidViewModel(app){
  private val repo=ChatRepository()
  private val _messages=MutableStateFlow<List<Message>>(emptyList()); val messages:StateFlow<List<Message>>=_messages
  private val _input=MutableStateFlow(""); val input:StateFlow<String> = _input
  private val _isStreaming=MutableStateFlow(false); val isStreaming:StateFlow<Boolean> = _isStreaming
  val lastAssistantUtterance=MutableStateFlow("")
  private fun settings(): Settings = SecurePrefs.load(getApplication())
  fun onInputChange(v:String){ _input.value=v }
  fun onUserSend(text:String?=null){
    val content=(text?:_input.value).trim(); if(content.isBlank()||_isStreaming.value) return
    _input.value=""; val id=UUID.randomUUID().toString()
    val cur=_messages.value.toMutableList(); cur.add(Message(id,text=content,fromUser=true)); cur.add(Message(id=id+"-bot",text="",fromUser=false)); _messages.value=cur
    streamReply()
  }
  private fun streamReply(){
    val s=settings(); if(s.apiKey.isBlank()){ appendError("Add your API key in Settings."); return }
    val history = _messages.value.filter{ it.text.isNotBlank() }.map{ if(it.fromUser) "user" to it.text else "assistant" to it.text }
    _isStreaming.value=true
    repo.streamChat(s.baseUrl,s.apiKey,s.model,history,
      onToken={ token-> val list=_messages.value.toMutableList(); val idx=list.indexOfLast{ !it.fromUser }; if(idx>=0){ val m=list[idx]; list[idx]=m.copy(text=m.text+token); _messages.value=list; lastAssistantUtterance.value=token } },
      onError={ msg-> appendError(msg) },
      onDone={ _isStreaming.value=false }
    )
  }
  private fun appendError(msg:String){ val list=_messages.value.toMutableList(); list.add(Message(id=UUID.randomUUID().toString(), text="Error: $msg", fromUser=false)); _messages.value=list }
}