package com.example.prayercaptious.android

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.prayercaptious.android.databinding.ActivityMainBinding


// suffix ? means the variable can be null
class MainActivity : ComponentActivity(){

    //contains all the ids of text view, graph view etc as keybinding ;)
    private lateinit var binding: ActivityMainBinding

    //It provides methods to access and manage various sensors available on android
    private lateinit var mSensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null
    private var linearaccSensor: Sensor? = null

    //Sensor class
    private lateinit var sensors: sensors


    override fun onCreate(savedInstanceState: Bundle?) {
        //initialization and allows you to proceed with custom logic specific to activity
        // your activity, such as setting the content view, initializing
        // UI components, binding data, or setting up event listeners.
        super.onCreate(savedInstanceState)

        //inflates the xml file layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        //shows the layout as ContentView
        setContentView(binding.root)

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

    //Android life cycle functions onResume, onPause and onDestroy
    override fun onResume() {
        super.onResume()
        sensors.registerListeners()


    }

    override fun onPause() {
        super.onPause()
        //make this redundant once all the functions are built because
        //the sensors are supposed to be active while the user prays putting app in background
        sensors.unregisterListeners()
    }


    override fun onDestroy() {
        super.onDestroy()
        sensors.unregisterListeners()
    }



}

