package com.example.prayercaptious.android

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlin.math.round


// suffix ? means the variable can be null
class MainActivity : ComponentActivity(), SensorEventListener {
    //Late initialising x,y,z textview (xml ones) (text users cannot edit but displayed to user)
    private lateinit var x_gyroscope: TextView
    private lateinit var y_gyroscope: TextView
    private lateinit var z_gyroscope: TextView
    private lateinit var x_linear_acc: TextView
    private lateinit var y_linear_acc: TextView
    private lateinit var z_linear_acc: TextView

    //Progress bar stuff
    private lateinit var shakeMeter: ProgressBar
    private lateinit var shakeAcceleration: TextView
    //make redundant later
    private var currentAcceleration: Double = 0.0
    private var previousAcceleration: Double = 0.0
    private var deltaAcceleration: Double = 0.0

    //Grpah stuff
    private lateinit var gyroGraph: GraphView
    private lateinit var linearaccGraph: GraphView
    private var gyroXseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var gyroYseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var gyroZseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))

    private var linearaccXseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))

    private var linearaccYseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var linearaccZseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var pointsplottedGyro: Double = 0.0
    private var pointsplottedLinear:Double = 0.0
    private val maxplots:Int = 200

    //It provides methods to access and manage various sensors available on android
    private lateinit var mSensorManager: SensorManager
//    private var SensorsReadingsGraphs:SensorEventListener = SensorsReadingsGraphs()
    private var gyroscopeSensor: Sensor? = null
    private var linearaccSensor: Sensor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        //initialization and allows you to proceed with custom logic specific to activity
        // your activity, such as setting the content view, initializing
        // UI components, binding data, or setting up event listeners.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Activate sensors
        setUpSensorStuff()

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

        //colour change for linear series x,y,z
        linearaccXseries.color=Color.GREEN
        linearaccYseries.color=Color.RED
        linearaccZseries.color= Color.YELLOW

        //colour change for gyroscope series x,y,z
        gyroXseries.color=Color.GREEN
        gyroYseries.color=Color.RED
        gyroZseries.color= Color.YELLOW

        //Graphs view for linear acceleration
        linearaccGraph.viewport.isScrollable = true
        linearaccGraph.viewport.isXAxisBoundsManual = true
        linearaccGraph.viewport.setMaxX(pointsplottedLinear)
        linearaccGraph.viewport.setMinX(pointsplottedLinear-200)
//        linearaccGraph.viewport.isScrollable()
        linearaccGraph.addSeries(linearaccXseries)
        linearaccGraph.addSeries(linearaccYseries)
        linearaccGraph.addSeries(linearaccZseries)


        //Graph view for gyroscoe
        gyroGraph.viewport.isScrollable = true
        gyroGraph.viewport.isXAxisBoundsManual = true
        gyroGraph.viewport.setMaxX(pointsplottedGyro)
        gyroGraph.viewport.setMinX(pointsplottedGyro-200)
//        gyroGraph.viewport.isScrollable()
        gyroGraph.addSeries(gyroXseries)
        gyroGraph.addSeries(gyroYseries)
        gyroGraph.addSeries(gyroZseries)






    }


    //Activating and checking for sensors in device
    private fun setUpSensorStuff(){
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager


        // Linear acceleration sensor management initialised
        linearaccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        if (linearaccSensor == null){
            Toast.makeText(this,"The device has no linear acc",Toast.LENGTH_SHORT).show()
        }

        //Gyroscope sensor management initialised
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (gyroscopeSensor == null){
            Toast.makeText(this,"The device has no gyro",Toast.LENGTH_LONG).show()
        }

    }
    // Part of SensorEventListener interface that tracks delta sensor data
    override fun onSensorChanged(event: SensorEvent?) {
        //linear acceleration sensor numbers
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x: Float = event.values[0]
            val y: Float = event.values[1]
            val z: Float = event.values[2]

            //update text views with real time numbers and change text colour to graph line colour
            x_linear_acc.text = ("x_linear = ${x.toInt()}")
            x_linear_acc.setTextColor(Color.parseColor("#00FF00")) // green
            y_linear_acc.text = ("y_linear = ${y.toInt()}")
            y_linear_acc.setTextColor(Color.parseColor("#FF0000")) //red
            z_linear_acc.text = ("z_linear = ${z.toInt()}")
            z_linear_acc.setTextColor(Color.parseColor("#FFFF00")) //yellow

            shakeMeter(x, y, z)
            //resets graph after a threshold to keep app efficient with saving memory
            if (pointsplottedLinear > maxplots){
                resetGraph(linearaccXseries,linearaccYseries,linearaccZseries,linearaccGraph)
                pointsplottedLinear=0.0
            }
//          points plotted is x axis
            pointsplottedLinear+=1.0

//            x axis
            linearaccXseries.appendData(DataPoint(pointsplottedLinear,round(x.toDouble())),true,pointsplottedLinear.toInt())

//           // y axis
            linearaccYseries.appendData(DataPoint(pointsplottedLinear,round(y.toDouble())),true,pointsplottedLinear.toInt())
//
//            // z axis
            linearaccZseries.appendData(DataPoint(pointsplottedLinear,round(z.toDouble())),true,pointsplottedLinear.toInt())

        }

        //Gyroscope sensor numbers
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val x: Float = event.values[0]
            val y: Float = event.values[1]
            val z: Float = event.values[2]

            //update text view
            x_gyroscope.text = ("x_gyro = ${x.toInt()}")
            x_gyroscope.setTextColor(Color.parseColor("#00FF00")) // green
            y_gyroscope.text = ("y_gyro = ${y.toInt()}")
            y_gyroscope.setTextColor(Color.parseColor("#FF0000")) //red
            z_gyroscope.text = ("z_gyro = ${z.toInt()}")
            z_gyroscope.setTextColor(Color.parseColor("#FFFF00")) //yellow

            shakeMeter(x, y, z)

            //resets graph after a threshold to keep app efficient with saving memory
            if (pointsplottedGyro > maxplots){
                resetGraph(gyroXseries,gyroYseries,gyroZseries,gyroGraph)
                pointsplottedGyro=0.0
            }
            // points plotted is x axis
            pointsplottedGyro+=1.0

            //x axis
            gyroXseries.appendData(DataPoint(pointsplottedGyro,round(x.toDouble())),true,pointsplottedGyro.toInt())

            //y axis
            gyroYseries.appendData(DataPoint(pointsplottedGyro,round(y.toDouble())),true,pointsplottedGyro.toInt())

            // z axis
            gyroZseries.appendData(DataPoint(pointsplottedGyro,round(z.toDouble())),true,pointsplottedGyro.toInt())


        }
    }


    // Part of SensorEventListener interface that manages sensor accuracy
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    private fun resetGraph(seriesx: LineGraphSeries<DataPoint>,seriesy: LineGraphSeries<DataPoint>,seriesz: LineGraphSeries<DataPoint>, graph: GraphView){
        //remove full visual graph plots
        graph.removeAllSeries()

        //empty all array of data points
        seriesx.resetData(emptyArray())
        seriesy.resetData(emptyArray())
        seriesz.resetData(emptyArray())

        //add back series again
        graph.addSeries(seriesx)
        graph.addSeries(seriesy)
        graph.addSeries(seriesz)
    }

    private fun shakeMeter(x:Float,y:Float,z:Float){
        currentAcceleration= Math.sqrt((x*x+y*y+z*z).toDouble())
        deltaAcceleration = Math.abs(currentAcceleration-previousAcceleration)*10
        previousAcceleration = currentAcceleration
        shakeAcceleration.text = ("Rotate/Moving delta acceleration = ${deltaAcceleration.toInt()}")
        shakeMeter.setProgress(deltaAcceleration.toInt())
    }


    //Android life cycle functions onResume, onPause and onDestroy
    override fun onResume() {
        super.onResume()
        // for linear acceleration
        //Get fastest detection senses with low latency
        mSensorManager.registerListener(
            this,
            linearaccSensor,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        //for gyroscope
        //Get fastest detection senses with low latency when app is on run
        mSensorManager.registerListener(
            this,
            gyroscopeSensor,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_NORMAL
        )

    }

    override fun onPause() {
        super.onPause()
        //make this redundant once all the functions are built because
        //the sensors are supposed to be active while the user prays putting app in background
        mSensorManager.unregisterListener(this)
    }


    override fun onDestroy() {
        mSensorManager.unregisterListener(this)
        super.onDestroy()
    }

}

