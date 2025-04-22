package com.example.prayercaptious.android

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue


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
    var timestamp: TextView,
    var motion: Spinner,
    var placement: AutoCompleteTextView,
    var side: Spinner,
    var elevation: Spinner,
    var myUtils: MyUtils
): SensorEventListener {

    //Sensors from sensor manager
    val linearaccSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    val gyroscopeSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    val rotationvectorSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    val magnometerSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    val acceleremetorSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val lightSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    //quaternions to euler angle to avoid gimbal lock
    var R: FloatArray = FloatArray(9)
    val rotation_vector: FloatArray = FloatArray(4)


    //gyrograph graph stuff
    private var pointsplottedGyro: Double = 0.0
    private var gyroXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf(
            DataPoint(0.0, 0.0)
        )
    )
    private var gyroYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf(
            DataPoint(0.0, 0.0)
        )
    )
    private var gyroZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf(
            DataPoint(0.0, 0.0)
        )
    )

    //linear acceleration stuff

    private var pointsplottedLinearacc: Double = 0.0
    private var linearaccXseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf(
            DataPoint(0.0, 0.0)
        )
    )
    private var linearaccYseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf(
            DataPoint(0.0, 0.0)
        )
    )
    private var linearaccZseries: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf(
            DataPoint(0.0, 0.0)
        )
    )

    //reset graph to save memeory
    private var maxplots_gyro: Int = 20000
    private var maxplots_linearacc: Int = 20000

    private var collectData: Boolean = true
    private var deleteCurrentData = false
    private var resetPressed: Boolean = false
    private var prayerid: Int = 1
    private var prayeridInitialized: Boolean = false


    private var gyroDataDB: GyroSensorData = GyroSensorData()
    private var linaccDataDB: LinearaccSensorData = LinearaccSensorData()

    //prayer motions and phone placements
    private var current_motion: String = ""
    private var current_placement: String = ""
    private var current_side: String = ""
    private var current_elevation = ""

    private var la_remapped = FloatArray(3)
    private var g_remapped = FloatArray(3)


    private var threshold: Double = 0.0
    private var reset_threshold = 0.0
    private var mistake_threshold = 0.0
    private var mistake_threshold_bow = 0.0
    private var mistake_threshold_prostration = 0.0
    private var standing_time = 0.0

    //Prayer position alerts
    //standing
    private var standing = false
    //bow alerts
    private var bow_prostrate_detection:Boolean = false
    private var bow_init:Boolean = false
    private var bow_verification:Boolean = false
    private var bow_completion_decision:Boolean = false
    private var bow_half_complete:Boolean = false
    private var bow_complete:Boolean = false
    private var ruku_missed = false
    //prostration alerts
    private var prostation_init:Boolean = false
    private var prostrationComplete:Boolean = false
    private var prostration_one_init:Boolean = false
    private var prostrationOneComplete:Boolean = false
    private var prostration_one_complete_not_sitting = false
    private var prostration_two_init:Boolean = false
    private var prostrationTwoComplete:Boolean = false
    private var prostration_three_init:Boolean = false
    private var prostration_excess = false

    //position_init
    private var position_init: Boolean = false
    //rakat status
    private var rakat_performed: Int = 0
    private val rakat_requested: Int = 1

    fun registerListeners() {
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
            SensorManager.SENSOR_DELAY_NORMAL
        )
        //  registering gyroscope
        //  Sampling period is game with normal delay
        mSensorManager.registerListener(
            this,
            gyroscopeSensor,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        //registering rotation vector sensor
        mSensorManager.registerListener(
            this,
            rotationvectorSensor,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        //  registering magnetic field
        //  Sampling period is game with normal delay
//        mSensorManager.registerListener(
//            this,
//            magnometerSensor,
//            SensorManager.SENSOR_DELAY_GAME,
//            SensorManager.SENSOR_DELAY_NORMAL
//        )

        //  registering accelerametor sensor
        //  Sampling period is game with normal delay
//        mSensorManager.registerListener(
//            this,
//            acceleremetorSensor,
//            SensorManager.SENSOR_DELAY_GAME,
//            SensorManager.SENSOR_DELAY_NORMAL
//        )


        //  registering light sensor
//        mSensorManager.registerListener(
//            this,
//            lightSensor,
//            SensorManager.SENSOR_DELAY_FASTEST,
//            SensorManager.SENSOR_DELAY_FASTEST
//        )

        collectData = true
        resetPressed = false
        deleteCurrentData = false

    }

    fun unregisterListeners() {
        mSensorManager.unregisterListener(this)
        collectData = false
        db.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            System.arraycopy(event.values, 0, rotation_vector, 0, 4)
        }
        //Timestamp of data collected
        timeStamp()

        //Alert the user of their prayer position
        prayerPositionAlert(g_remapped, la_remapped)

        //Gyroscope sensor
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            g_remapped = adjustedSensorData(event, rotation_vector)
            gyroData(event, g_remapped)
        }

        //Linear acceleration sensor
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            la_remapped = adjustedSensorData(event, rotation_vector)
            linearaccData(event, la_remapped)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun timeStamp(): String {
        val timestamps = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        timestamp.text = ("Date: $timestamps\nPrayerID: $prayerid | userID: ${user.id}")
        return timestamps
    }

    //gyroData:
    //  1) Extracts x,y,z values of gyroscope sensor
    //  2) Appends x,y,z values of gyroscope data real time into series of data
    //  3) Updates text label of x,y,z
    //  4) Adds shake meter progressbar and shows shake acceleration
    @RequiresApi(Build.VERSION_CODES.O)
    private fun gyroData(event: SensorEvent?, g_remapped_values: FloatArray) {

        val xg: Float = event!!.values[0]
        val yg: Float = event.values[1]
        val zg: Float = event.values[2]

//        val x:Double = String.format("%.2f", xg).toDouble()
//        val y:Double = String.format("%.2f", yg).toDouble()
//        val z:Double = String.format("%.2f", zg).toDouble()

        val x: Double = String.format("%.2f", g_remapped_values[0]).toDouble()
        val y: Double = String.format("%.2f", g_remapped_values[1]).toDouble()
        val z: Double = String.format("%.2f", g_remapped_values[2]).toDouble()


        if (collectData) {

            gyroDataDB.prayerID = prayerid

            Log.d(
                "gyroData", "userid: ${gyroDataDB.userID}" +
                        "\tprayerid: ${gyroDataDB.prayerID}" +
                        "\ttimestamp: ${timeStamp()}" +
                        "\txg: $x\tyg: $y\tzg: $z" +
                        //alternative for realtime change
//                        "\tmotion: ${initialise_motion()}" +
//                        "\tplacement: ${initialise_placement()}" +
//                        "\tside: ${initialise_side()}" +
//                        "\televation: ${initialise_elevation()}"+
                        "\tmotion: ${current_motion}" +
                        "\tplacement: ${current_placement}" +
                        "\tside: ${current_side}" +
                        "\televation: ${current_elevation}"
            )
            val insertGyroData = GyroSensorData(
                user.id,
                prayerid,
                timeStamp(),
                x,
                y,
                z,
                current_motion,
                current_placement,
                current_side,
                current_elevation
            )
            db.insertGyroData(insertGyroData)
        }

        appendGyroData(x, y, z)

        updateTextAndColourGyro(x, y, z)

    }

    private fun appendGyroData(x: Double, y: Double, z: Double) {
        //resets graph after a threshold to keep app efficient with saving memory
        if (pointsplottedGyro > maxplots_gyro) {
            resetGraph(gyroXseries, gyroYseries, gyroZseries, graphg)
            pointsplottedGyro = 0.0
        }

        //points plotted is x axis
        pointsplottedGyro += 1.0

        //x axis
        gyroXseries.appendData(DataPoint(pointsplottedGyro, (x)), true, pointsplottedGyro.toInt())

        //y axis
        gyroYseries.appendData(DataPoint(pointsplottedGyro, (y)), true, pointsplottedGyro.toInt())

        // z axis
        gyroZseries.appendData(DataPoint(pointsplottedGyro, (z)), true, pointsplottedGyro.toInt())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun linearaccData(event: SensorEvent?, remapped_event: FloatArray) {

        val xla: Float = event!!.values[0]
        val yla: Float = event.values[1]
        val zla: Float = event.values[2]

//        val x:Double = String.format("%.2f", xla).toDouble()
//        val y:Double = String.format("%.2f", yla).toDouble()
//        val z:Double = String.format("%.2f", zla).toDouble()

        val x: Double = String.format("%.2f", remapped_event[0]).toDouble()
        val y: Double = String.format("%.2f", remapped_event[1]).toDouble()
        val z: Double = String.format("%.2f", remapped_event[2]).toDouble()

//        val x:Double = String.format("%.2f", remapped_event).toDouble() // for magnitude only

        if (collectData) {

            linaccDataDB.prayerID = prayerid

//            Log.d(
//                "linearData", "userid: ${linaccDataDB.userID}" +
//                        "\tprayerid: ${linaccDataDB.prayerID}" +
//                        "\ttimestamp: ${timeStamp()}" +
//                        "\txla: $x\tyla: $y\tzla: $z" +
//                        "\tmotion: ${current_motion}" +
//                        "\tplacement: ${current_placement}" +
//                        "\tside: ${current_side}" +
//                        "\televation: ${current_elevation}"
//            )

            Log.d(
                "linearData",
                " x: ${x}" +
                        " y: ${y}" +
                        " z: ${z}"
            )
            val insertLinearaccSensorData = LinearaccSensorData(
                user.id,
                prayerid,
                timeStamp(),
                x,
                y,
                z,
                current_motion,
                current_placement,
                current_side,
                current_elevation
            )
            db.insertLinAccData(insertLinearaccSensorData)
        }

        appendLinearaccData(x, y, z)

        updateTextAndColourLinearacc(x, y, z)
    }

    private fun appendLinearaccData(x: Double, y: Double, z: Double) {
        //resets graph after a threshold to keep app efficient with saving memory
        if (pointsplottedLinearacc > maxplots_linearacc) {
            resetGraph(linearaccXseries, linearaccYseries, linearaccZseries, graphla)
            pointsplottedLinearacc = 0.0
        }
        //points plotted is x axis
        pointsplottedLinearacc += 1.0

        //x axis
        linearaccXseries.appendData(
            DataPoint(pointsplottedLinearacc, (x)),
            true,
            pointsplottedLinearacc.toInt()
        )

        //y axis
        linearaccYseries.appendData(
            DataPoint(pointsplottedLinearacc, (y)),
            true,
            pointsplottedLinearacc.toInt()
        )

        // z axis
        linearaccZseries.appendData(
            DataPoint(pointsplottedLinearacc, (z)),
            true,
            pointsplottedLinearacc.toInt()
        )

    }

    //PlotSeriesData: plots realtime appended data into graph
    fun plotSeriesData() {
        graphg.addSeries(gyroXseries)
        graphg.addSeries(gyroYseries)
        graphg.addSeries(gyroZseries)

        graphla.addSeries(linearaccXseries)
        graphla.addSeries(linearaccYseries)
        graphla.addSeries(linearaccZseries)

    }

    fun graphSettings(graph: GraphView) {
        graph.viewport.isScrollable = true
        graph.viewport.setMaxY(3.0)
        graph.viewport.setMinY(-3.0)
        graph.viewport.setMaxX(pointsplottedGyro)
        graph.viewport.setMinX(pointsplottedGyro - 200)
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.isYAxisBoundsManual = true

    }

    fun seriesColour() {
        //Gyroscope series
        gyroXseries.color = Color.GREEN
        gyroYseries.color = Color.RED
        gyroZseries.color = Color.YELLOW

        //Linear acceleration series
        linearaccXseries.color = Color.GREEN
        linearaccYseries.color = Color.RED
        linearaccZseries.color = Color.YELLOW
    }

    private fun updateTextAndColourGyro(x: Double, y: Double, z: Double) {

        x_g.text = ("x_gyro = $x")
        x_g.setTextColor(Color.parseColor("#00FF00")) // green
        y_g.text = ("y_gyro = $y")
        y_g.setTextColor(Color.parseColor("#FF0000")) //red
        z_g.text = ("z_gyro = $z")
        z_g.setTextColor(Color.parseColor("#FFFF00")) //yellow

    }

    private fun updateTextAndColourLinearacc(x: Double, y: Double, z: Double) {

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
    ) {
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

    fun resetGraphData() {

        if (!collectData && deleteCurrentData) {
            resetPressed = true
        }

        //resetting data while collecting a set of data
        if (collectData) {
            prayerid += 1
            this.unregisterListeners()
            resetPressed = true
        }

        //incrementing prayer id by 1 while data collection is at pause
        // and the number stays incremented by 1 until data collection starts again meaning pressing reset multiple times will not increment again
        if (!collectData && !resetPressed) {
            prayerid += 1
            resetPressed = true
        }
        //resets data --- make redundant once complete collection data.
        resetGraph(linearaccXseries, linearaccYseries, linearaccZseries, graphla)
        pointsplottedLinearacc = 0.0
        resetGraph(gyroXseries, gyroYseries, gyroZseries, graphg)
        pointsplottedGyro = 0.0
        this.timestamp.text = ("PrayerID: $prayerid | userID: ${user.id}")
    }


    fun initializePrayerID() {

        //+1 to ensure last prayerid is not used to collect data
        prayerid = db.getPrayerIDSensors(user) + 1
        gyroDataDB.userID = user.id
        linaccDataDB.userID = user.id
        gyroDataDB.prayerID = prayerid
        linaccDataDB.prayerID = prayerid
        prayeridInitialized = true
        this.timestamp.text = ("PrayerID: $prayerid | userID: ${user.id}")

    }

    fun deleteCurrentData() {
        db.deleteCurrentDataCollected(user.id, prayerid)
        //resets data -- make redundant once complete collection data.
        resetGraph(linearaccXseries, linearaccYseries, linearaccZseries, graphla)
        pointsplottedLinearacc = 0.0
        resetGraph(gyroXseries, gyroYseries, gyroZseries, graphg)
        pointsplottedGyro = 0.0
        deleteCurrentData = true
        this.timestamp.text = ("PrayerID: $prayerid | userID: ${user.id}")
    }

    fun initialise_motion(): String {
        current_motion = motion.selectedItem.toString()
        return current_motion

    }

    fun initialise_placement(): String {
        current_placement = placement.text.toString()
        return current_placement

    }

    fun initialise_side(): String {
        current_side = side.selectedItem.toString()
        return current_side

    }

    fun initialise_elevation(): String {
        current_elevation = elevation.selectedItem.toString()
        return current_elevation

    }

    fun adjustedSensorData(event: SensorEvent?, rotation_vector: FloatArray): FloatArray {
        //Sensor raw euler x,y,z values (gyroscope data in rad/s)
        val x: Float = event?.values!![0]
        val y: Float = event.values[1]
        val z: Float = event.values[2]

        // Rotation vector of the phone (orientation in quaternions)
        val i: Float = rotation_vector[0] //x quat
        val j: Float = rotation_vector[1] //y quat
        val k: Float = rotation_vector[2] //z quat
        val w: Float = rotation_vector[3] //Scaler

        // Transformed linear acceleration data through R.sensor_data
        val transformedData: FloatArray = FloatArray(3)

//         Reference_orientation = [0,0,1] represents phone
//        R = floatArrayOf(
//            1 - 2 * (k * k + w * w), 2 * (j * k - i * w), 2 * (j * w + i * k),
//            2 * (j * k + i * w), 1 - 2 * (j * j + w * w), 2 * (k * w - i * j),
//            2 * (j * w - i * k), 2 * (k * w + i * j), 1 - 2 * (j * j + k * k)
//        )
        R = floatArrayOf(
            1 - 2 * (j * j + k * k), 2 * (i * j - w * k), 2 * (i * k + w * j),
            2 * (i * j + w * k), 1 - 2 * (i * i + k * k), 2 * (j * k - w * i),
            2 * (i * k - w * j), 2 * (j * k + w * i), 1 - 2 * (i * i + j * j)
        )

        //3 by 3
//        R = floatArrayOf(
//            w * w + i * i - j * j - k * k, 2 * (i * j - w * k), 2 * (i * k + w * j),
//            2 * (i * j + w * k), w * w - i * i + j * j - k * k, 2 * (j * k - w * i),
//            2 * (i * k - w * j), 2 * (j * k + w * i), w * w - i * i - j * j + k * k
//        )


        transformedData[2] = (R[0] * x) + (R[1] * y) + (R[2] * z)
        transformedData[0] = (R[3] * x) + (R[4] * y) + (R[5] * z) //good y
        transformedData[1] = (R[6] * x) + (R[7] * y) + (R[8] * z)

        return transformedData
    }

    private fun prayerPositionAlert(gyroValues: FloatArray, linaccValues: FloatArray) {
        //6points/sec for light
        //50points/sec for gyro/lin acc
        val xg = gyroValues[0]
        val yg = gyroValues[1]
        val zg = gyroValues[2]

        val xla = linaccValues[0]
        val yla = linaccValues[1]
        val zla = linaccValues[2]

//        mistakeAlert()
//        rukuAlerts(gyroValues, linaccValues)
//        prostrationAlerts(gyroValues,linaccValues)


        // Checks if bow is initialized
        // Bow initialization happens during prostration initialization ( hence checking if bow is missed or performed )
        if(!bow_prostrate_detection &&
            (yg.absoluteValue > 0.25 && yg.absoluteValue < 0.9)
            && (xg.absoluteValue > 0.25 && xg.absoluteValue < 0.9)
            && (xla.absoluteValue > 0.9 && xla.absoluteValue < 2)
            ){
            bow_prostrate_detection = true
            mistake_threshold = 0.0
            mistake_threshold = pointsplottedGyro + 50.0
        }

        //Bow performed after bow initialization
        if (bow_prostrate_detection && pointsplottedGyro > mistake_threshold && !prostation_init && !position_init){
            bow_init = true
            myUtils.bowInitializedAudio()

            position_init = true

            reset_threshold = 0.0
            reset_threshold = pointsplottedGyro + 2000.0
        }

        //Bow missed and prostration is initialized after bow initialization
        if (bow_prostrate_detection && pointsplottedGyro < mistake_threshold && !prostation_init &&
            (xg.absoluteValue > 1.1 && yg.absoluteValue > 1.1) || (xg.absoluteValue > 1.1 && zg.absoluteValue > 1.1)
            && (zla.absoluteValue > 1.5 && yla.absoluteValue > 1.5)
            && !position_init
            ){
            prostation_init = true
            mistake_threshold_prostration = pointsplottedGyro + 100.0
            myUtils.rukuMissed()

            position_init = true

            reset_threshold = 0.0
            reset_threshold = pointsplottedGyro + 2000.0

            prostrationAlerts(gyroValues, linaccValues) // the person is prostrating

        }


        //calls ruku alert once initiated
        if (position_init && bow_init){
            rukuAlerts(gyroValues, linaccValues) // the person is bowing
        }

        //calls prostration alert once initiated
        if (position_init && prostation_init){
            prostrationAlerts(gyroValues, linaccValues) // the person is bowing
        }

        //resets for another round of testing
        if (position_init && pointsplottedGyro > reset_threshold){
            myUtils.beginPrayerAudio()
            //initial movement from standing detection
            bow_prostrate_detection = false
            //initializing reset
            position_init = false
            bow_init = false
            prostation_init = false
            //completion reset
            bow_complete = false
            bow_half_complete = false
            bow_completion_decision = false
            //completion reset prostration
            prostation_init = false
            prostrationOneComplete = false
            prostration_one_complete_not_sitting = false
            prostration_two_init = false
            prostrationTwoComplete = false
            prostration_excess = false
            //standing
            standing = false
        }

    }

    private fun rukuAlerts(gyroValues: FloatArray, linaccValues: FloatArray):Boolean {
        //unpacking values
        val xg = gyroValues[0]
        val yg = gyroValues[1]
        val zg = gyroValues[2]

        val xla = linaccValues[0]
        val yla = linaccValues[1]
        val zla = linaccValues[2]

        //calls prostration algorithm once bow is complete or half complete
        if (bow_complete || bow_half_complete){
            prostrationAlerts(gyroValues,linaccValues)
        }

        //half bow and full bow decision threshold time creation
        if (!bow_complete  && !bow_half_complete && yg < -0.25 && xla.absoluteValue > 0.9 && !bow_completion_decision) {
            mistake_threshold_bow = pointsplottedGyro + 25.0
            bow_completion_decision = true
        }
        // detects if bow is half complete
        if (bow_init && !bow_complete && !bow_half_complete
            && (pointsplottedGyro < mistake_threshold_bow && bow_completion_decision)
            && ((xg.absoluteValue > 0.8 && yg.absoluteValue > 0.8) ||
                    (xg.absoluteValue > 0.8 && zg.absoluteValue > 0.8))
            && (zla.absoluteValue > 1.5 && yla.absoluteValue > 1.5)){
            bow_half_complete = true
            prostation_init = true
            mistake_threshold_prostration = pointsplottedGyro + 25.0
            myUtils.rukuMissed() // bow half complete audio
        }

        // detects if bow is fully complete
        if (bow_init && !bow_complete && !bow_half_complete
            && (pointsplottedGyro > mistake_threshold_bow && bow_completion_decision)
            ){
            bow_complete = true
            myUtils.rukuPerformedAudio()
        }

        return bow_complete
    }

    private fun prostrationAlerts(gyroValues: FloatArray, linaccValues: FloatArray):Boolean {
        //unpacking values
        val xg = gyroValues[0]
        val yg = gyroValues[1]
        val zg = gyroValues[2]

        val xla = linaccValues[0]
        val yla = linaccValues[1]
        val zla = linaccValues[2]

        // runs only after bow is complete
        if (!prostation_init
            && ((xg.absoluteValue > 1 && yg.absoluteValue > 1) || (xg.absoluteValue > 1 && zg.absoluteValue > 1))
            && (xla.absoluteValue > 2.5 || yla.absoluteValue > 2.5)) {
            prostation_init = true
            myUtils.prostrationInitializedAudio()

            mistake_threshold_prostration = pointsplottedGyro + 75.0 //time it takes to prostrate
        }

        //might have to change algorithms here//

        // checking if going to prostration or coming back up from prostration

        //first prostration completion
        if (prostation_init && !prostrationOneComplete
            && pointsplottedGyro > mistake_threshold_prostration
            && (xg < -0.7)
            ){
            prostrationOneComplete = true
            myUtils.prostrationOnePerformed()
            mistake_threshold_prostration = pointsplottedGyro + 50.0
        }

        //if the person is not sitting he is probably standing up, so need to add flags to identify mistake //
        if (prostrationOneComplete && !prostrationTwoComplete && !prostration_one_complete_not_sitting &&
            (zg.absoluteValue > 1.7 && xg.absoluteValue > 1.7)){
            prostration_one_complete_not_sitting = true
            standing = true
            myUtils.prostrationMissedOne()
        }
//        //second prostration complete
//        if (prostrationOneComplete && !prostration_one_complete_not_sitting && !prostrationTwoComplete && pointsplottedGyro > mistake_threshold_prostration
//            && (xg < -0.7)){
//            prostrationTwoComplete = true
//            myUtils.prostrationTwoPerformed()
//            mistake_threshold_prostration = pointsplottedGyro + 50.0
//        }
//
//        //standing up after prostration two
//        if (prostrationTwoComplete && !standing && (zg.absoluteValue > 1.5)){
//            standing = true
//            standing_time = pointsplottedGyro + 200
//            myUtils.bowVerifiedAudio() //standing audio
//        }
//
//        //doing excess prostration detection
//        if (prostrationTwoComplete && !standing && pointsplottedGyro > mistake_threshold_prostration && !prostration_excess &&
//            (xg < -0.7)){
//            prostration_excess = true
//            myUtils.prostrationExcess()
//        }
//
//        //standing up after prostration two
//        if (prostration_excess && !standing && (zg.absoluteValue > 1.5)){
//            standing = true
//            standing_time = pointsplottedGyro + 200
//            myUtils.bowVerifiedAudio() //standing audio
//        }

        return prostrationComplete
    }
}