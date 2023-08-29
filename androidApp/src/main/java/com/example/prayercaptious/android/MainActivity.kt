package com.example.prayercaptious.android

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import com.example.prayercaptious.android.databinding.ActivityMainBinding
import com.example.prayercaptious.android.databinding.HomeScreenBinding
import com.example.prayercaptious.android.databinding.NonFunctionalAppBinding
import com.example.prayercaptious.android.databinding.RegistrationLoginBinding
import com.example.prayercaptious.android.databinding.RegistrationPageBinding
import java.text.DecimalFormat
import kotlin.math.round


// suffix ? means the variable can be null
class MainActivity : ComponentActivity(){

    //binding1 is the main activity binding and binding2 is home screen binding
    // Type naming convention is filename with first letters capital and appended Binding at the end
    private lateinit var bindingmissingsensor:NonFunctionalAppBinding
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindinghome: HomeScreenBinding
    private lateinit var bindinglogin: RegistrationLoginBinding
    private lateinit var bindingregister: RegistrationPageBinding

    //It provides methods to access and manage various sensors available on android
    private lateinit var mSensorManager: SensorManager

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
        bindingmissingsensor = NonFunctionalAppBinding.inflate(layoutInflater)

        //useful classes
        useful_classes_services()

        //Check availability of core sensor for app to function in users phone
        sensorsAvailabilityCheck()

        //shows login or registration page
        register_loginStuff()


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
//        sensors.unregisterListeners()
    }


    override fun onDestroy() {
        super.onDestroy()
        sensors.unregisterListeners()
    }

    fun sensorsAvailabilityCheck() {
        //Sensors from sensor manager
        val linearaccSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val gyroscopeSensor: Sensor? = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        //Check availability of sensors
        val hasLinearAcc = !(linearaccSensor == null)
        val hasGyroscope = !(gyroscopeSensor == null)

        val hasCrucialSensors:Boolean =
            hasLinearAcc
            && hasGyroscope

        if (!hasCrucialSensors){
            setContentView(bindingmissingsensor.root)
            MyUtils.showToast(this,"Has gyroscope manager $hasGyroscope")
            MyUtils.showToast(this,"Has Accelerometer manager $hasLinearAcc")
        }
    }
    fun register_loginStuff(){
        //User has to login first to use the app
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
                val height = bindingregister.regHeightEt.text.toString().toDouble()
                userData = User(name,email,pass,height)
                if (!db.insertRegistrationUserData(userData)){
                    MyUtils.showToast(this,"Registration successful")
                    setContentView(bindinglogin.root)
                    register.clear_register()
                    MyUtils.showToast(this,"Log in now :)")
                } else MyUtils.showToast(this,"Failed: registering data into db!!!")
            }
        }

        bindinglogin.loginBtn.setOnClickListener(){
            val email_entered = bindinglogin.loginEmailEt.text.toString().trim().lowercase()
            val pass_entered = bindinglogin.loginPassEt.text.toString()
            userData = User(email_entered)
            val userDetails_DB:User = db.login_details(userData)
            // user details from DB != null means there are existing user email found for login
            if (!userDetails_DB.email.isNullOrEmpty()){
                if (userDetails_DB.pass == pass_entered){
                    MyUtils.showToast(this,"Login success")
                    //shows home screen for the user
                    homeStuff(userDetails_DB)
                }else MyUtils.showToast(this,"Password does not match")
            } else MyUtils.showToast(this,"Email not found")
        }

        //read or delete data
        bindinglogin.readDataBtn.setOnClickListener(){
            val data:MutableList<User> = db.readRegistrationUserData()
            bindinglogin.databaseTv.text = "ID NAME EMAIL HEIGHT PASS\n"
            for (i in 0 until data.size){
                bindinglogin.databaseTv.append(
                    data[i].id.toString()+" "+
                            data[i].name+" "+
                            data[i].email+" "+
                            data[i].height.toString()+" "+
                            data[i].pass+"\n"
                )
            }
        }

        bindinglogin.deleteDataBtn.setOnClickListener(){
            db.deleteRegistrationUserData()
        }

    }


    fun homeStuff(userDetails:User){
        //only for testing
//        db.checkdb()
        setContentView(bindinghome.root)

        //calling sensor with correct logged user details
        sensors = sensors(
            mSensorManager,
            userDetails,
            db,
            binding.xGyroscope,
            binding.yGyroscope,
            binding.zGyroscope,
            binding.xLinearAcc,
            binding.yLinearAcc,
            binding.zLinearAcc,
            binding.gyroGraph,
            binding.linearaccGraph,
            binding.shakeAcceleration,
            binding.shakeMeter,
            binding.tvTimestamp
        )
        bindinghome.tvWelcomeUser.text =
            "As-Salaam-Alaikum ${userDetails.name} 😁," +
            "\nWelcome back to Prayer Captious App!"
        val countdown = object : CountDownTimer(3000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                bindinghome.tvCountdown.text = round(millisUntilFinished.toDouble() / 1000).toString()
            }

            override fun onFinish() {
                setContentView(binding.root)
                islistening=true
                sensorStuff()
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

        binding.btnStopDataCollection.setOnClickListener(){
            sensors.unregisterListeners()
        }

        binding.btnStartDataCollection.setOnClickListener(){
            sensors.registerListeners()

        }

        binding.btnResetGraphData.setOnClickListener(){
//            sensors.unregisterListeners()
            sensors.resetGraphData()
        }

        binding.btnDeleteCurrentData.setOnClickListener(){
            sensors.unregisterListeners()
            db.deleteCurrentDataCollected()
            sensors.initializePrayerID()
        }

        //initial prayer motion
        binding.tvPrayerMotion.text = sensors.prayerMotion()

        //after clicking to change motion
        binding.overlayButton.setOnClickListener(){
            binding.tvPrayerMotion.text = sensors.prayerMotion()
        }
    }




    fun useful_classes_services(){

        //getting sensor service as SensorManager
        // activating all required sensors in sensor class from SENSOR_SERVICE
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //Verifies data entered in registration boxes
        register = VerifyRegistratoin(
            bindingregister.regNameEt,
            bindingregister.regEmailEt,
            bindingregister.regPassEt,
            bindingregister.regConfirmPassEt,
            bindingregister.regHeightEt
        )

        //Initalizing Database
        db = MyUtils.myDB(this)

    }


}