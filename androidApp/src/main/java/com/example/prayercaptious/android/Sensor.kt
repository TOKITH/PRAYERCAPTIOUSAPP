package com.example.prayercaptious.android

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


//Collect data of specific sensors: x,y,z axis of gyroscope, linear acceleration
//Draw charts of the sensors in real time
//Edit text of sensor label data in real time
//Show shake meter progress of sensor acceleration

// class type : SensorEventListener
// class parameters : x,y,z axis of gyroscope sensor and linear acceleration sensor
// parameter continued : graph view  reference of joe for linear and gyroscope graphs
// parameter continued : x,y,z label text reference of graphs
// parameter continued : label data of shake meter, shake meter reference

// class functions : Register sensors
// class functions : onSensorChanged (in built from SensorListenerEvent interface)
// class function : onAccuracyChanged (in built from SensorListenerEvent interface)
// class function : gyroscopeData , linearaccData
// class function : gyroscopeGraph , linearaccData
open class sensors(
    var mSensorManager: SensorManager,
    var user:User,
    val db:SQLliteDB,
    var x_g: TextView,
    var y_g: TextView,
    var z_g: TextView,
    var x_la: TextView,
    var y_la: TextView,
    var z_la: TextView,
    var graphg: GraphView,
    var graphla: GraphView,
    var shakemeteracc: TextView,
    var shakemeter: ProgressBar,
    var timestamp: TextView
): SensorEventListener {

    //Sensors from sensor manager
    val linearaccSensor:Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    val gyroscopeSensor:Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    //shake meter acceleration calculation stuff
    private var currentAcceleration: Double = 0.0
    private var previousAcceleration: Double = 0.0
    private var deltaAcceleration: Double = 0.0


    //gyrograph graph stuff
    private var pointsplottedGyro: Double = 0.0
    private var gyroXseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var gyroYseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var gyroZseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))

    //linear acceleration stuff

    private var pointsplottedLinearacc: Double = 0.0
    private var linearaccXseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var linearaccYseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))
    private var linearaccZseries: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
        DataPoint(0.0,0.0)
    ))

    //reset graph to save memeory
    private var maxplots_gyro: Int = 20000
    private var maxplots_linearacc: Int = 20000

    //prayer motion
    private val motions: ArrayList<String> = arrayListOf("standing","bowing","prostrating","sitting")
    private var motion_num: Int = 0
    private var current_motion: String = ""
    private var collectData:Boolean = true
    private var deleteCurrentData = false
    private var resetPressed:Boolean = false
    private var prayerid:Int = 1
    private var prayeridInitialized:Boolean = false


    private var gyroDataDB:GyroSensorData = GyroSensorData()
    private var linaccDataDB: LinearaccSensorData = LinearaccSensorData()
    fun registerListeners(){
        //Initializing prayerId for data collection
        if (!prayeridInitialized) {
            initializePrayerID()
        }

        //  registering linear accleration sensor
        //  Sampling period is game with normal delay
        mSensorManager.registerListener(
            this,
            linearaccSensor,
            SensorManager.SENSOR_DELAY_GAME,
//            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        //  registering gyroscope
        //  Sampling period is game with normal delay
        mSensorManager.registerListener(
            this,
            gyroscopeSensor,
            SensorManager.SENSOR_DELAY_GAME,
//            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        collectData = true
        resetPressed = false
        deleteCurrentData = false
    }

    fun unregisterListeners(){
        mSensorManager.unregisterListener(this)
        collectData = false
        db.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE){
            gyroData(event)
        }
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION){
            linearaccData(event)
        }

        timeStamp()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStamp():String{
        val timestamp = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        this.timestamp.text = ("Data: $timestamp\nPrayerID: $prayerid | userID: ${user.id}")
        return timestamp
    }

    //gyroData:
    //  1) Extracts x,y,z values of gyroscope sensor
    //  2) Appends x,y,z values of gyroscope data real time into series of data
    //  3) Updates text label of x,y,z
    //  4) Adds shake meter progressbar and shows shake acceleration
    @RequiresApi(Build.VERSION_CODES.O)
    private fun gyroData(event: SensorEvent?){

        val xg: Float = event!!.values[0]
        val yg: Float = event.values[1]
        val zg: Float = event.values[2]

        val x:Double = String.format("%.2f", xg).toDouble()
        val y:Double = String.format("%.2f", yg).toDouble()
        val z:Double = String.format("%.2f", zg).toDouble()


        if (collectData) {

            gyroDataDB.prayerID = prayerid

            Log.d(
                "myTag", "userid: ${gyroDataDB.userID}" +
                        "\tprayerid: ${gyroDataDB.prayerID}" +
                        "\ttimestamp: ${timeStamp()}" +
                        "\txg: $x\tyg: $y\tzg: $z" +
                        "\tmotion: ${current_motion}"
            )
            val insertGyroData= GyroSensorData(user.id,prayerid,timeStamp(),x,y,z,current_motion)
            db.insertGyroData(insertGyroData)
        }

        appendGyroData(x,y,z)

        updateTextAndColourGyro(x,y,z)

        shakeMeter(x,y,z)


    }

    private fun appendGyroData(x:Double,y:Double,z:Double){
        //resets graph after a threshold to keep app efficient with saving memory
        if (pointsplottedGyro > maxplots_gyro){
            resetGraph(gyroXseries,gyroYseries,gyroZseries,graphg)
            pointsplottedGyro=0.0
        }

        //points plotted is x axis
        pointsplottedGyro+=1.0

        //x axis
//        gyroXseries.appendData(DataPoint(pointsplottedGyro,round(x)),true,pointsplottedGyro.toInt())
        gyroXseries.appendData(DataPoint(pointsplottedGyro,(x)),true,pointsplottedGyro.toInt())

        //y axis
        gyroYseries.appendData(DataPoint(pointsplottedGyro,(y)),true,pointsplottedGyro.toInt())

        // z axis
        gyroZseries.appendData(DataPoint(pointsplottedGyro,(z)),true,pointsplottedGyro.toInt())
    }

    //linearaccData:
    //  1) Extracts x,y,z values of linear acceleration sensor
    //  2) Appends x,y,z values of linear acceleration data real time into series of data
    //  3) Updates text label of x,y,z
    //  4) Adds shake meter progressbar and shows shake acceleration
    @RequiresApi(Build.VERSION_CODES.O)
    private fun linearaccData(event: SensorEvent?){

        val xla: Float = event!!.values[0]
        val yla: Float = event.values[1]
        val zla: Float = event.values[2]

        val x:Double = String.format("%.2f", xla).toDouble()
        val y:Double = String.format("%.2f", yla).toDouble()
        val z:Double = String.format("%.2f", zla).toDouble()

        if (collectData) {

            linaccDataDB.prayerID = prayerid

            Log.d(
                "myTag", "userid: ${linaccDataDB.userID}" +
                        "\tprayerid: ${linaccDataDB.prayerID}" +
                        "\ttimestamp: ${timeStamp()}" +
                        "\txla: $x\tyla: $y\tzla: $z" +
                        "\tmotion: ${current_motion}"
            )
            val insertLinearaccSensorData= LinearaccSensorData(user.id,prayerid,timeStamp(),x,y,z,current_motion)
            db.insertLinAccData(insertLinearaccSensorData)
        }

        appendLinearaccData(x,y,z)

        updateTextAndColourLinearacc(x,y,z)

        shakeMeter(x,y,z)
    }

    private fun appendLinearaccData(x:Double,y:Double,z:Double){
        //resets graph after a threshold to keep app efficient with saving memory
        if (pointsplottedLinearacc > maxplots_linearacc){
            resetGraph(linearaccXseries,linearaccYseries,linearaccZseries,graphla)
            pointsplottedLinearacc=0.0
        }
        //points plotted is x axis
        pointsplottedLinearacc+=1.0

        //x axis
        linearaccXseries.appendData(DataPoint(pointsplottedLinearacc,(x)),true,pointsplottedLinearacc.toInt())

        //y axis
        linearaccYseries.appendData(DataPoint(pointsplottedLinearacc,(y)),true,pointsplottedLinearacc.toInt())

        // z axis
        linearaccZseries.appendData(DataPoint(pointsplottedLinearacc,(z)),true,pointsplottedLinearacc.toInt())

    }

    //PlotSeriesData: plots realtime appended data into graph
    fun plotSeriesData(){
        graphg.addSeries(gyroXseries)
        graphg.addSeries(gyroYseries)
        graphg.addSeries(gyroZseries)

        graphla.addSeries(linearaccXseries)
        graphla.addSeries(linearaccYseries)
        graphla.addSeries(linearaccZseries)

    }

    fun graphSettings(graph:GraphView){
        graph.viewport.isScrollable = true
        graph.viewport.setMaxY(12.0)
        graph.viewport.setMinY(-12.0)
        graph.viewport.setMaxX(pointsplottedGyro)
        graph.viewport.setMinX(pointsplottedGyro-200)
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.isYAxisBoundsManual = true

    }

    fun seriesColour(){
        //Gyroscope series
        gyroXseries.color=Color.GREEN
        gyroYseries.color=Color.RED
        gyroZseries.color= Color.YELLOW

        //Linear acceleration series
        linearaccXseries.color=Color.GREEN
        linearaccYseries.color=Color.RED
        linearaccZseries.color= Color.YELLOW
    }

    private fun updateTextAndColourGyro(x:Double, y:Double, z:Double){

        x_g.text = ("x_gyro = $x")
        x_g.setTextColor(Color.parseColor("#00FF00")) // green
        y_g.text = ("y_gyro = $y")
        y_g.setTextColor(Color.parseColor("#FF0000")) //red
        z_g.text = ("z_gyro = $z")
        z_g.setTextColor(Color.parseColor("#FFFF00")) //yellow

    }

    private fun updateTextAndColourLinearacc(x:Double, y:Double, z:Double){

        x_la.text = ("x_lin_acc = $x")
        x_la.setTextColor(Color.parseColor("#00FF00")) // green
        y_la.text = ("y_lin_acc = $y")
        y_la.setTextColor(Color.parseColor("#FF0000")) //red
        z_la.text = ("z_lin_acc = $z")
        z_la.setTextColor(Color.parseColor("#FFFF00")) //yellow
    }
    private fun resetGraph(
       seriesx: LineGraphSeries<DataPoint>,
       seriesy: LineGraphSeries<DataPoint>,
       seriesz: LineGraphSeries<DataPoint>,
       graph: GraphView
    ){
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

    private fun shakeMeter(x:Double,y:Double,z:Double){
        currentAcceleration= Math.sqrt((x*x+y*y+z*z).toDouble())
        deltaAcceleration = Math.abs(currentAcceleration-previousAcceleration)*10
        previousAcceleration = currentAcceleration
        shakemeteracc.text = ("Welcome ${user.name}\nRotate/Moving delta acceleration = ${deltaAcceleration.toInt()}")
        shakemeter.setProgress(deltaAcceleration.toInt())
    }

    fun resetGraphData(){

        if (!collectData && deleteCurrentData){
            resetPressed = true
        }

        //resetting data while collecting a set of data
        if (collectData){
            prayerid+= 1
            this.unregisterListeners()
            resetPressed = true
        }

        //incrementing prayer id by 1 while data collection is at pause
        // and the number stays incremented by 1 until data collection starts again meaning pressing reset multiple times will not increment again
        if (!collectData && !resetPressed){
            prayerid+=1
            resetPressed = true
        }
        //resets data --- make redundant once complete collection data.
        resetGraph(linearaccXseries,linearaccYseries,linearaccZseries,graphla)
        pointsplottedLinearacc=0.0
        resetGraph(gyroXseries,gyroYseries,gyroZseries,graphg)
        pointsplottedGyro=0.0
        this.timestamp.text = ("PrayerID: $prayerid | userID: ${user.id}")
    }

    fun prayerMotion():String{
        if (motion_num < motions.size){
            current_motion = motions[motion_num]
            motion_num += 1
        }
        else{
            motion_num = 0
            current_motion = motions[motion_num]
            motion_num += 1
        }

        return current_motion

    }

    fun initializePrayerID(){

        //+1 to ensure last prayerid is not used to collect data
        prayerid = db.getPrayerID(user)+1
        gyroDataDB.userID = user.id
        linaccDataDB.userID = user.id
        gyroDataDB.prayerID = prayerid
        linaccDataDB.prayerID = prayerid
        prayeridInitialized = true
        this.timestamp.text = ("PrayerID: $prayerid | userID: ${user.id}")

    }

    fun deleteCurrentData(){
        db.deleteCurrentDataCollected(user.id,prayerid)
        //resets data -- make redundant once complete collection data.
        resetGraph(linearaccXseries,linearaccYseries,linearaccZseries,graphla)
        pointsplottedLinearacc=0.0
        resetGraph(gyroXseries,gyroYseries,gyroZseries,graphg)
        pointsplottedGyro=0.0
        deleteCurrentData = true
        this.timestamp.text = ("PrayerID: $prayerid | userID: ${user.id}")
    }


}