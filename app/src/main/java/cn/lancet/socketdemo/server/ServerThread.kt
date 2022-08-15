package cn.lancet.socketdemo.server

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.Socket

/**
 * Created by Lancet at 2022/8/14 20:04
 */
class ServerThread(private val socket: Socket,private val callback: ServerCallback):Thread() {

    override fun run() {
        val inputStream: InputStream?
        try {
            inputStream = socket.getInputStream()
            val buffer = ByteArray(1024)
            var len: Int
            var receiveStr = ""
            if (inputStream.available() == 0){
                Log.e("SocketServer:", "inputStream.available() == 0")
            }
            while (inputStream.read(buffer).also { len = it } != -1){
                receiveStr += String(buffer,0,len,Charsets.UTF_8)
                if (len < 1024) {
                    callback.receiveClientMessage(true,receiveStr)
                    receiveStr = ""
                }
            }
        }catch(e: IOException) {
            e.printStackTrace()
            e.message?.let { Log.e("SocketServer ERR",it) }
            callback.receiveClientMessage(false,"")
        }
    }

}