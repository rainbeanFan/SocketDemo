package cn.lancet.socketdemo.client

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.nio.charset.Charset

/**
 * Created by Lancet at 2022/8/14 20:47
 */
class ClientThread(private val socket: Socket,private val callback: ClientCallback):Thread() {

    override fun run() {
        val inputStream: InputStream?
        try {
            inputStream = socket.getInputStream()
            val buffer = ByteArray(1024)
            var len : Int
            var receiveStr = ""
            if (inputStream.available() == 0){
                Log.e("ClientSocket","inputStream.available() == 0")
            }
            while (inputStream.read(buffer).also { len = it } !=-1){
                receiveStr += String(buffer,0,len, Charsets.UTF_8)
                if (len < 1024) {
                    callback.receiveServerMessage(receiveStr)
                    receiveStr = ""
                }
            }
        }catch(e: IOException) {
            e.printStackTrace()
            e.message?.let { Log.e("socket error",it) }
            callback.receiveServerMessage("")
        }
    }

}