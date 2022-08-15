package cn.lancet.socketdemo.client

/**
 * Created by Lancet at 2022/8/14 20:34
 */
interface ClientCallback {

    fun receiveServerMessage(message:String)

    fun receiveMessage(message:String)

}