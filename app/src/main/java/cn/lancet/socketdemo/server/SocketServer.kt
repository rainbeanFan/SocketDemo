package cn.lancet.socketdemo.server

import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

/**
 * Created by Lancet at 2022/8/14 19:58
 */
object SocketServer {

    private val TAG = SocketServer::class.java.simpleName

    private const val SOCKET_PORT = 9527

    private var mSocket: Socket?=null
    private var mServerSocket: ServerSocket?=null

    private lateinit var mCallback: ServerCallback
    private lateinit var mOutputStream: OutputStream

    var result = true

    fun startServer(callback: ServerCallback):Boolean {
        mCallback = callback
        thread {
            try {
                mServerSocket = ServerSocket(SOCKET_PORT)
                while (result){
                    mSocket = mServerSocket?.accept()
                    mCallback.receiveMessage("${mSocket?.inetAddress} to connected")
                    ServerThread(mSocket!!,mCallback).start()
                }
            }catch (e: IOException){
                e.printStackTrace()
                result = false
            }
        }
        return result
    }


    fun stopServer(){
        try {
            mSocket?.apply {
                shutdownInput()
                shutdownOutput()
                close()
            }
            mServerSocket?.close()
        }catch (e: IOException){

        }
    }


    fun sendToClient(message:String){
        thread {
            if (mSocket!!.isClosed){
                Log.d(TAG,"sendToClient:Socket is closed")
                return@thread
            }
            mOutputStream = mSocket!!.getOutputStream()
            try {
                mOutputStream.write(message.toByteArray())
                mOutputStream.flush()
                mCallback.receiveMessage("to Client:$message")
                Log.d(TAG,"发送到客户端成功！")
            }catch (e: IOException){
                e.printStackTrace()
                Log.d(TAG,"向客户端发送消息失败")
            }
        }
    }

}