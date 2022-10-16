package com.example.client

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    var Acc: Sensor? = null
    var accvalue = ""

    var SERVER_PORT = 1111
    var SERVER_IP = ""
    var clientThread: ClientThread? = null
    var thread: Thread? = null
    var msgList: LinearLayout? = null
    var handler: Handler? = null
    var clientTextColor = 0
    var IP_text: EditText? = null
    var PORT_text: EditText? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        msgList = findViewById(R.id.msgList)

        IP_text = findViewById(R.id.serverIPText)
        PORT_text = findViewById(R.id.serverPortText)

        clientTextColor = ContextCompat.getColor(this, R.color.green)
        setTitle("Client")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        Log.d("sensors", deviceSensors.toString())

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Log.d("sensors", "Linear accelerometer found!")
            Acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        } else {
            Log.d("sensors", "No linear accelerometer found!")
        }

        sensorManager.registerListener(this, Acc, SensorManager.SENSOR_DELAY_GAME);

        handler = Handler()

    }

    fun textView(message: String?, color: Int, value: Boolean): TextView {
        var message = message
        if (null == message || message.trim { it <= ' ' }.isEmpty()) {
            message = "<Empty Message>"
        }
        val tv = TextView(this)
        tv.setTextColor(color)
        tv.text = "$message"
        tv.textSize = 20f
        tv.setPadding(0, 5, 0, 0)
        tv.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            0F
        )
        if (value) {
            tv.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        }
        return tv
    }

    fun showMessage(message: String?, color: Int, value: Boolean) {
        handler!!.post { msgList!!.addView(textView(message, color, value)) }
    }

    fun onClick(view: View) {
        if (view.id == R.id.connect_server) {
            SERVER_IP = IP_text?.getText().toString()
            SERVER_PORT = PORT_text?.getText().toString().toInt()
            Log.d("STRING", SERVER_IP.toString())
            Log.d("STRING", SERVER_PORT.toString())

            msgList!!.removeAllViews()
            clientThread = ClientThread()
            thread = Thread(clientThread)
            thread!!.start()
            return
        }
        if (view.id == R.id.RightClick) {
            if (null != clientThread) {
                clientThread!!.sendMessage("Right click")
            }
            return
        }
        if (view.id == R.id.LeftClick) {
            if (null != clientThread) {
                clientThread!!.sendMessage("Left click")
            }
            return
        }

    }

    /* clientThread class defined to run the client connection to the socket network using the server ip and port
     * and send message */
    inner class ClientThread : Runnable {
        private var socket: Socket? = null
        private var input: BufferedReader? = null
        override fun run() {
            try {

                val serverAddr: InetAddress = InetAddress.getByName(SERVER_IP)
                showMessage("Connecting to Server...", clientTextColor, true)
                socket = Socket(serverAddr, SERVER_PORT)
                if (socket!!.isBound) {
                    showMessage("Connected to Server...", clientTextColor, true)
                }
                while (!Thread.currentThread().isInterrupted) {
                    input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    var message = input!!.readLine()
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted()
                        message = "Server Disconnected..."
                        showMessage(message, Color.RED, false)
                        break
                    }
                    showMessage("Server: $message", clientTextColor, true)
                }
            } catch (e1: UnknownHostException) {
                e1.printStackTrace()
            } catch (e1: IOException) {
                showMessage(
                    "Problem Connecting to server... Check your server IP and Port and try again",
                    Color.RED,
                    false
                )
                Thread.interrupted()
                e1.printStackTrace()
            } catch (e3: NullPointerException) {
                showMessage("error returned", Color.RED, true)
            }
        }

        fun sendMessage(message: String?) {
            Thread {
                try {
                    if (null != socket) {
                        val out = PrintWriter(
                            BufferedWriter(
                                OutputStreamWriter(socket!!.getOutputStream())
                            ),
                            true
                        )
                        out.println(message)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }


    protected override fun onDestroy() {
        super.onDestroy()
        if (null != clientThread) {
            clientThread!!.sendMessage("Disconnect")
            clientThread = null
        }
    }

    override fun onSensorChanged(event: SensorEvent) {

        var val1 = event.values[0].toString()
        var val2 = event.values[1].toString()
        var val3 = event.values[2].toString()
        var len1 =  val1.length
        var len2 =  val2.length
        var len3 =  val3.length

        if(len1 >5){len1 = 5}
        else if(len1< 5){// Use own len
            }
        if(len2 >5){len2 = 5}
        else if(len2< 5){// Use own len
        }
        if(len3 >5){len3 = 5}
        else if(len3< 5){// Use own len
        }


        accvalue = event.values[0].toString().substring(0,len1) + "," + event.values[1].toString().substring(0,len2)+","+event.values[2].toString().substring(0,len3)
        Log.d("ACC", accvalue )
        if (null != clientThread) {
            clientThread!!.sendMessage(accvalue)
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // Do something here if sensor accuracy changes.
    }


}