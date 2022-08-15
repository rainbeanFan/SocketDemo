package cn.lancet.socketdemo

import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lancet.socketdemo.adapter.MessageAdapter
import cn.lancet.socketdemo.client.ClientCallback
import cn.lancet.socketdemo.client.SocketClient
import cn.lancet.socketdemo.data.Message
import cn.lancet.socketdemo.databinding.ActivityMainBinding
import cn.lancet.socketdemo.server.ServerCallback
import cn.lancet.socketdemo.server.SocketServer

class MainActivity : AppCompatActivity(),ServerCallback,ClientCallback {

    private lateinit var binding: ActivityMainBinding

    private var mIsServer = true
    private var mOpenSocket = false
    private var mConnectSocket = false

    private val messages = mutableListOf<Message>()

    private lateinit var mMessageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.tvIpAddress.text = "Ip地址:${getIp()}"

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            mIsServer = when (checkedId) {
                R.id.radio_server -> true
                R.id.radio_client -> false
                else -> true
            }
            binding.tvIpAddress.visibility = if (mIsServer) View.VISIBLE else View.GONE
            binding.btnStartServer.visibility = if (mIsServer) View.VISIBLE else View.GONE

            binding.tilLayout.visibility = if (mIsServer) View.GONE else View.VISIBLE
            binding.btnConnectServer.visibility = if (mIsServer) View.GONE else View.VISIBLE

            binding.etSendInfo.hint = if (mIsServer) "发送给客户端" else "发送给服务端"
        }

        binding.btnStartServer.setOnClickListener {
            mOpenSocket = if (mOpenSocket){
                SocketServer.stopServer()
                false
            }else{
                SocketServer.startServer(this)
                true
            }
            binding.btnStartServer.text = if (mOpenSocket) "关闭服务" else "开启服务"
        }

        binding.btnConnectServer.setOnClickListener {
            val ip = binding.etIpAddress.text.toString()
            if (ip.isEmpty()){
                showError("请输入Ip地址")
                return@setOnClickListener
            }
            mConnectSocket = if (mConnectSocket){
                SocketClient.closeConnect()
                false
            }else{
                SocketClient.connectServer(ip,this)
                true
            }
            binding.btnConnectServer.text = if (mConnectSocket) "关闭连接" else "连接服务"
        }

        binding.btnSendMessage.setOnClickListener{
            val message = binding.etSendInfo.text.toString()
            if (message.isEmpty()){
                showError("请输入要发送的信息")
                return@setOnClickListener
            }

            val isSend = if (mOpenSocket) mOpenSocket else if (mConnectSocket) mConnectSocket else false
            if (!isSend){
                showError("当前未开启或连接服务")
                return@setOnClickListener
            }
            if (mIsServer) SocketServer.sendToClient(message) else SocketClient.sendToServer(message)
            binding.etSendInfo.setText("")

            updateList(2,message)

        }

        mMessageAdapter = MessageAdapter(messages)
        binding.rvInfo.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mMessageAdapter
        }

    }

    override fun receiveServerMessage(message: String) {
//        type = 1    收到的消息
        val type = if (mIsServer) 2 else 1
        updateList(type,message)
    }

    override fun receiveClientMessage(success: Boolean, message: String) {
        val type = if (mIsServer) 1 else 2
        updateList(type,message)
    }

    override fun receiveMessage(message: String) {
        Log.e("Receive Message err", "$message ")
    }

    private fun updateList(type:Int,message: String){
        if (message.isEmpty()) return
        messages.add(Message(type, message))
        runOnUiThread {
            (if (messages.size == 0) 0 else messages.size - 1).apply {
                mMessageAdapter.notifyItemChanged(this)
                binding.rvInfo.smoothScrollToPosition(this)
            }
        }
    }

    private fun showError(error: String) = Toast.makeText(this,error,Toast.LENGTH_LONG).show()

    private fun getIp() = intToIp(
        ContextCompat.getSystemService(this, WifiManager::class.java)!!.connectionInfo.ipAddress
    )

    private fun intToIp(ip:Int) =
        "${(ip and 0xFF)}.${(ip shr 8 and 0xFF)}.${(ip shr 16 and 0xFF)}.${(ip shr 24 and 0xFF)}"

}