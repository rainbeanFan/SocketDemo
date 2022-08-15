package cn.lancet.socketdemo.client

import android.util.Log
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

/**
 * Created by Lancet at 2022/8/14 20:55
 */
object SocketClient {

    private val TAG = SocketClient::class.java.simpleName

    private var mSocket:Socket?=null

    private var mOutputStream: OutputStream? = null

    private var mInputStreamReader: InputStreamReader? = null

    private lateinit var mCallback: ClientCallback

    private const val SOCKET_PORT = 9527

    fun connectServer(ipAddress:String, callback: ClientCallback){
        mCallback = callback
        thread {
            try {
                mSocket = Socket(ipAddress,SOCKET_PORT)
                ClientThread(mSocket!!,mCallback).start()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun closeConnect(){
        try {
            mInputStreamReader?.close()
            mOutputStream?.close()
            mSocket?.apply {
                shutdownInput()
                shutdownOutput()
                close()
            }
        }catch (e: Exception){

        }

        Log.e(TAG, "closeConnect")
    }

    fun sendToServer(message:String){
        thread {
            if (mSocket!!.isClosed){
                Log.e(TAG, "sendToServer:Socket is closed")
                return@thread
            }
            mOutputStream = mSocket?.getOutputStream()
            try {
                mOutputStream?.write(message.toByteArray())
                mOutputStream?.flush()
                mCallback.receiveMessage("to Server:$message")
            }catch (e: IOException){
                e.printStackTrace()
                Log.e(TAG, "向服务端发送消息失败")
            }
        }
    }

}