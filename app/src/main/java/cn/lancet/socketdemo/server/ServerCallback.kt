package cn.lancet.socketdemo.server

/**
 * Created by Lancet at 2022/8/14 19:56
 */
interface ServerCallback {

    fun receiveClientMessage(success: Boolean, message: String)

    fun receiveMessage(message: String)

}