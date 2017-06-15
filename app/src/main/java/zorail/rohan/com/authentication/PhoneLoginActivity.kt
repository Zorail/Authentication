package zorail.rohan.com.authentication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.phone_auth.*
import java.util.concurrent.TimeUnit

/**
 * Created by zorail on 14-Jun-17.
 */
class PhoneLoginActivity:AppCompatActivity(){

    lateinit private var mAuth:FirebaseAuth
    lateinit private var mResendToken:PhoneAuthProvider.ForceResendingToken
    lateinit private var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit private var mVerificationId:String
    companion object{
        const val STATE_INITIALIZED = 1
        const val STATE_CODE_SENT = 2
        const val STATE_VERIFY_FAILED = 3
        const val STATE_VERIFY_SUCCESS = 4
        const val STATE_SIGNIN_FAILED = 5
        const val STATE_SIGNIN_SUCCESS = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.phone_auth)
        if (savedInstanceState!=null) {
            onRestoreInstanceState(savedInstanceState)
        }
        mAuth = FirebaseAuth.getInstance()
        mCallbacks = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                updateUI(STATE_VERIFY_SUCCESS, p0)
                signInWithPhoneAuthCredential(p0!!)
            }

            override fun onVerificationFailed(p0: FirebaseException?) {
                Toast.makeText(applicationContext,p0!!.message,Toast.LENGTH_LONG).show()
                updateUI(STATE_VERIFY_FAILED)
            }

            override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(p0, p1)
                mVerificationId = p0!!
                mResendToken = p1!!
                updateUI(STATE_CODE_SENT)
            }

        }
        button_start_verification.setOnClickListener {
            if(validatePhoneNumber())
            startPhoneNumberVerification(field_phone_number.text.toString())
        }
        button_verify_phone.setOnClickListener { verifyPhoneNumberWithCode(mVerificationId,field_verification_code.text.toString()) }
        sign_out_button.setOnClickListener { signOut() }
        button_resend.setOnClickListener { resendVerificationCode(field_phone_number.text.toString(),mResendToken) }

    }
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { p0 ->
                    if(p0.isSuccessful){
                        val user:FirebaseUser = p0.result.user
                        updateUI(STATE_SIGNIN_SUCCESS, user)
                    }
                    else{
                        updateUI(STATE_SIGNIN_FAILED)
                    }
                }
    }


    fun startPhoneNumberVerification(phoneNumber:String) = PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber
    ,60,TimeUnit.SECONDS,this,mCallbacks)


    fun verifyPhoneNumberWithCode(verificationId:String,code:String){
        val credential:PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(credential)
    }
     private fun validatePhoneNumber():Boolean{
        val phoneNumber:String = field_phone_number.text.toString()
        if(TextUtils.isEmpty(phoneNumber)){
            field_phone_number.error = "Invalid Phone Number"
            return@validatePhoneNumber false
        }
         return true
    }

    private fun enableViews(vararg views:View):Unit{
        for(v:View in views)
            v.isEnabled = true

    }

    private fun disableViews(vararg views:View):Unit{
        for(v:View in views)
            v.isEnabled = false
    }
    private fun signOut():Unit{
        mAuth.signOut()
        updateUI(STATE_INITIALIZED)
    }

    private fun updateUI(uiState:Int) {
        updateUI(uiState, mAuth.currentUser, null)
    }
    private fun updateUI(user:FirebaseUser?) {
        if (user != null)
        {
            updateUI(STATE_SIGNIN_SUCCESS, user)
        }
        else
        {
            updateUI(STATE_INITIALIZED)
        }
    }
    private fun updateUI(uiState:Int, user:FirebaseUser) {
        updateUI(uiState, user, null)
    }
    private fun updateUI(uiState:Int, cred:PhoneAuthCredential?) {
        updateUI(uiState, null, cred)
    }
    private fun updateUI(uiState:Int,user:FirebaseUser?,cred:PhoneAuthCredential?){
        when(uiState){
            STATE_INITIALIZED ->{
                enableViews(button_start_verification,field_phone_number)
                disableViews(button_verify_phone,button_resend,field_verification_code)
                detail.text = null
            }
            STATE_CODE_SENT ->{
                enableViews(button_verify_phone,button_resend,field_phone_number,field_verification_code)
                disableViews(button_start_verification)
                detail.text = getString(R.string.status_code_sent)
            }
            STATE_VERIFY_FAILED ->{
                enableViews(button_start_verification,button_resend,button_verify_phone,field_verification_code,field_phone_number)
                detail.text = getString(R.string.status_verification_failed)
            }
            STATE_VERIFY_SUCCESS ->{
                disableViews(button_start_verification,button_verify_phone,button_resend,field_phone_number,field_verification_code)
                detail.text = getString(R.string.status_verification_succeeded)
                if(cred!=null){
                    if(cred.smsCode!=null)
                        field_verification_code.setText(cred.smsCode)
                    else
                        field_verification_code.setText(getString(R.string.instant_validation))
                }
            }
            STATE_SIGNIN_FAILED ->detail.text = getString(R.string.status_sign_in_failed)
            STATE_SIGNIN_SUCCESS->Unit
        }
        if (user == null){
            phone_auth_fields.visibility = View.VISIBLE
            signed_in_buttons.visibility = View.GONE
            status.text = getString(R.string.signed_out)
        }
        else{
            phone_auth_fields.visibility = View.GONE
            signed_in_buttons.visibility = View.VISIBLE
            enableViews(field_phone_number,field_verification_code)
            field_verification_code.text = null
            field_phone_number.text = null
            status.text = getString(R.string.signed_in)
            detail.text = getString(R.string.firebaseui_status_fmt,user.uid)
        }
    }
    private fun resendVerificationCode(phoneNumber:String,
                                       token:PhoneAuthProvider.ForceResendingToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token)
    }
}