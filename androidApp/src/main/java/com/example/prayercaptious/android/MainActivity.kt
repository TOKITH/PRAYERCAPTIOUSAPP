package com.example.prayercaptious.android

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.prayercaptious.android.databinding.ActivityMainBinding
import com.example.prayercaptious.android.databinding.HomeScreenBinding
import com.example.prayercaptious.android.databinding.RegistrationLoginBinding
import com.example.prayercaptious.android.databinding.RegistrationPageBinding
import kotlin.math.round


// suffix ? means the variable can be null
class MainActivity : ComponentActivity(){

    //binding1 is the main activity binding and binding2 is home screen binding
    // Type naming convention is filename with first letters capital and appended Binding at the end
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindinghome: HomeScreenBinding
    private lateinit var bindinglogin: RegistrationLoginBinding
    private lateinit var bindingregister: RegistrationPageBinding

    //It provides methods to access and manage various sensors available on android
    private lateinit var mSensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null
    private var linearaccSensor: Sensor? = null

    //Sensor class
    private lateinit var sensors: sensors
    private var islistening: Boolean = false

    //Registration
    private lateinit var register: VerifyRegistratoin

    //Database class
    private lateinit var db: SQLliteDB
    private lateinit var userData:User



    override fun onCreate(savedInstanceState: Bundle?) {
        //initialization and allows you to proceed with custom logic specific to activity
        // your activity, such as setting the content view, initializing
        // UI components, binding data, or setting up event listeners.
        super.onCreate(savedInstanceState)

        //inflates the xml file layout: shows keybindings and content of specific xml page
        bindinghome = HomeScreenBinding.inflate(layoutInflater)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindinglogin = RegistrationLoginBinding.inflate(layoutInflater)
        bindingregister = RegistrationPageBinding.inflate(layoutInflater)

        //useful classes
        useful_classes_services()

        //shows login or registration page
        register_loginStuff()

        //shows home screen
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

    fun register_loginStuff(){
        setContentView(bindinglogin.root)

        //Redirects to registration page from login
        bindinglogin.registerRdBtn.setOnClickListener(){
            setContentView(bindingregister.root)
        }

        //Redirects to login page from registration
        bindingregister.loginRdBtn.setOnClickListener(){
            setContentView(bindinglogin.root)
        }

        //Properly registering will:
        // 1) store data into DB
        // 2) rd to login page
        // 3) clear register page
        bindingregister.registerBtn.setOnClickListener(){
            if(register.verified_user_data(this)) {
                //User data
                val name = bindingregister.regNameEt.text.toString().trim()
                val email = bindingregister.regEmailEt.text.toString().trim().lowercase()
                val pass = bindingregister.regPassEt.text.toString().trim()
                userData = User(name,email,pass)
                if (!db.insertRegistrationUserData(userData)){
                    MyUtils.showToast(this,"Registration successful")
                    setContentView(bindinglogin.root)
                    register.clear_register()
                    MyUtils.showToast(this,"Log in now :)")
                } else MyUtils.showToast(this,"Something went wrong!!!")
            }
        }
    }
    fun homeStuff(){
        //shows home layout
//        setContentView(bindinghome.root)

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
        //plots data real time and separates xyz axis by color
        sensors.plotSeriesData()
        sensors.seriesColour()

        //Zoom into current graph
        sensors.graphSettings(binding.gyroGraph)
        sensors.graphSettings(binding.linearaccGraph)
    }


    fun useful_classes_services(){

        //getting sensor service as SensorManager
        // activating gyroscope and linear acceleration sensor from SENSOR_SERVICE
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        linearaccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        //sensor class collects sensor x,y,z data and plots sensor data
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

        //Verifies data entered in registration boxes
        register = VerifyRegistratoin(
            bindingregister.regNameEt,
            bindingregister.regEmailEt,
            bindingregister.regPassEt,
            bindingregister.regConfirmPassEt
        )

        //Initalizing Database
        db = SQLliteDB(this)

    }


}

