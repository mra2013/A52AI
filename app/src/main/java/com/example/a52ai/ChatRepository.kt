package com.example.a52ai
import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSource
import java.io.IOException
class ChatRepository(private val client: OkHttpClient = defaultClient()) {
  data class ChatDelta(val content: String?)
  data class ChoiceDelta(val delta: ChatDelta?)
  data class StreamChunk(val choices: List<ChoiceDelta> = emptyList())
  companion object {
    private fun defaultClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply{ level=HttpLoggingInterceptor.Level.BASIC }).build()
  }
  fun streamChat(baseUrl:String, apiKey:String, model:String, history:List<Pair<String,String>>, onToken:(String)->Unit, onError:(String)->Unit, onDone:()->Unit){
    val url= if(baseUrl.endsWith("/")) baseUrl + "v1/chat/completions" else baseUrl + "/v1/chat/completions"
    val messages = history.map{ (role,content)-> mapOf("role" to role, "content" to content) }
    val bodyJson = mapOf("model" to model, "stream" to true, "messages" to messages, "temperature" to 0.7)
    val body = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(bodyJson))
    val req = Request.Builder().url(url).post(body).addHeader("Authorization","Bearer $apiKey").addHeader("Content-Type","application/json").build()
    client.newCall(req).enqueue(object: Callback {
      override fun onFailure(call: Call, e: IOException){ onError(e.localizedMessage?:"Network error"); onDone() }
      override fun onResponse(call: Call, response: Response){
        if(!response.isSuccessful){ onError("HTTP ${response.code()}"); onDone(); return }
        try{
          val source: BufferedSource = response.body()!!.source()
          while(!source.exhausted()){
            val line = source.readUtf8Line() ?: continue
            if(line.isBlank()) continue
            if(line.startsWith("data:")){
              val payload = line.removePrefix("data:").trim()
              if(payload=="[DONE]") break
              try{
                val chunk = Gson().fromJson(payload, StreamChunk::class.java)
                val token = chunk.choices.firstOrNull()?.delta?.content
                if(token!=null) onToken(token)
              }catch(ex: Exception){ Log.w("A52AI","Parse error: $payload") }
            }
          }
        }catch(ex: Exception){ onError(ex.localizedMessage?:"Stream error") } finally { onDone() }
      }
    })
  }
}