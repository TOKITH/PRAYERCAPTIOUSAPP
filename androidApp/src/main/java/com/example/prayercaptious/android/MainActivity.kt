package com.example.prayercaptious.android

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import com.example.prayercaptious.android.databinding.ActivityMainBinding
import com.example.prayercaptious.android.databinding.HomeScreenBinding
import kotlin.math.round


// suffix ? means the variable can be null
class MainActivity : ComponentActivity(){
    // lock rotation
    private lateinit var requestedOrientation: ActivityInfo
    //binding1 is the main activity binding and binding2 is home screen binding
    // Type naming convention is filename with first letters capital and appended Binding at the end
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindinghome: HomeScreenBinding

    //It provides methods to access and manage various sensors available on android
    private lateinit var mSensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null
    private var linearaccSensor: Sensor? = null

    //Sensor class
    private lateinit var sensors: sensors
    private var islistening: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        //initialization and allows you to proceed with custom logic specific to activity
        // your activity, such as setting the content view, initializing
        // UI components, binding data, or setting up event listeners.
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo()
        //inflates the xml file layout
        bindinghome = HomeScreenBinding.inflate(layoutInflater)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //shows home screen first
        homeStuff()

        //from home screen moves to sensor stuff
        sensorStuff()
    }

    //Android life cycle functions onResume, onPause and onDestroy
    override fun onResume() {
        super.onResume()
        if (islistening) {
            sensors.registerListeners()
        }
    }

    override fun onPause() { ///////////////////////////////////
        super.onPause()
        //make this redundant once all the functions are built because
        //the sensors are supposed to be active while the user prays putting app in background
        sensors.unregisterListeners()
    }//////////////////////////////////////////////////////////


    override fun onDestroy() {
        super.onDestroy()
        sensors.unregisterListeners()
    }

    fun homeStuff(){
        //shows home layout
        setContentView(bindinghome.root)

        val countdown = object : CountDownTimer(3000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                bindinghome.tvCountdown.text = round(millisUntilFinished.toDouble() / 1000).toString()
            }

            override fun onFinish() {
                setContentView(binding.root)
                sensors.registerListeners()
                islistening=true
            }
        }

        //10 second timer before start collecting data
        bindinghome.btnTimerStart.setOnClickListener() {
            countdown.start()
        }

        //cancel timer if more time is required
        bindinghome.btnTimerStop.setOnClickListener(){
            countdown.cancel()
        }
    }

    fun sensorStuff(){
        //getting sensor service as SensorManager
        // activating gyroscope and linear acceleration sensor from SENSOR_SERVICE
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        linearaccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)


        sensors = sensors(
            mSensorManager,
            gyroscopeSensor,
            linearaccSensor,
            binding.xGyroscope,
            binding.yGyroscope,
            binding.zGyroscope,
            binding.xLinearAcc,
            binding.yLinearAcc,
            binding.zLinearAcc,
            binding.gyroGraph,
            binding.linearaccGraph,
            binding.shakeAcceleration,
            binding.shakeMeter)

        //plots data real time and separates xyz axis by color
        sensors.plotSeriesData()
        sensors.seriesColour()

        //Zoom into current graph
        sensors.graphSettings(binding.gyroGraph)
        sensors.graphSettings(binding.linearaccGraph)
    }


}

