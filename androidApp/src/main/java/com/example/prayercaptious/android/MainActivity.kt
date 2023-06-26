package com.example.prayercaptious.android

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.jjoe64.graphview.GraphView


// suffix ? means the variable can be null
class MainActivity : ComponentActivity(){

    //Late initialising x,y,z textview (xml ones) (text users cannot edit but displayed to user)
    //late initialising because they need to be initialised onCreated method()
    private lateinit var x_gyroscope: TextView
    private lateinit var y_gyroscope: TextView
    private lateinit var z_gyroscope: TextView
    private lateinit var x_linear_acc: TextView
    private lateinit var y_linear_acc: TextView
    private lateinit var z_linear_acc: TextView

    //Progress bar
    private lateinit var shakeMeter: ProgressBar
    private lateinit var shakeAcceleration: TextView
    //Graph view
    private lateinit var gyroGraph: GraphView
    private lateinit var linearaccGraph: GraphView

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
        setContentView(R.layout.activity_main)

        //getting sensor service as SensorManager
        // activating gyroscope and linear acceleration sensor from SENSOR_SERVICE
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        linearaccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        //getting text view ids that will show delta sensor data
        x_linear_acc = findViewById(R.id.x_linear_acc)
        y_linear_acc = findViewById(R.id.y_linear_acc)
        z_linear_acc = findViewById(R.id.z_linear_acc)
        x_gyroscope = findViewById(R.id.x_gyroscope)
        y_gyroscope = findViewById(R.id.y_gyroscope)
        z_gyroscope = findViewById(R.id.z_gyroscope)

        //getting progress bar ids that will show progress bar stuff
        shakeMeter = findViewById(R.id.shakeMeter)
        shakeAcceleration = findViewById(R.id.shakeAcceleration)

        //getting graph view ids to show graphs
        linearaccGraph = findViewById(R.id.linearaccGraph)
        gyroGraph = findViewById(R.id.gyroGraph)

        sensors = sensors(
            mSensorManager,
            gyroscopeSensor,
            linearaccSensor,
            x_gyroscope,
            y_gyroscope,
            z_gyroscope,
            x_linear_acc,
            y_linear_acc,
            z_linear_acc,
            gyroGraph,
            linearaccGraph,
            shakeAcceleration,
            shakeMeter)

        sensors.plotSeriesData()
        sensors.seriesColour()
        sensors.graphSettings(gyroGraph)
        sensors.graphSettings(linearaccGraph)
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

