package zorail.rohan.com.authentication

import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ResultCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.firebaseui_phone.*

/**
 * Created by zorail on 16-Jun-17.
 */
class AuthUILogin:AppCompatActivity(){
   lateinit var mAuth:FirebaseAuth
    companion object{
        const val RC_SIGN_IN = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firebaseui_phone)
        mAuth = FirebaseAuth.getInstance()
        button_start_verification.setOnClickListener { signIn() }
        sign_out_button.setOnClickListener { signOut() }

    }
    fun signIn():Unit{
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder().setTheme(AuthUI.getDefaultTheme())
                .setLogo(AuthUI.NO_LOGO)
                .setAvailableProviders(getProvider())
                .build(), RC_SIGN_IN)
    }
    fun getProvider():List<AuthUI.IdpConfig>{
        val selectedProviders = ArrayList<AuthUI.IdpConfig>()
        selectedProviders.add(AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())
        return selectedProviders
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data)
        }
    }
    @MainThread
    fun handleSignInResponse(resultCode: Int,data:Intent?){
        val user:FirebaseUser? = mAuth.currentUser
        if(resultCode == ResultCodes.OK)
            if(user!=null) {
                detail.text = user.uid
                status.text = getString(R.string.signed_in)
                button_start_verification.visibility = View.GONE
                signed_in_buttons.visibility = View.VISIBLE
            }


    }
    @MainThread
    fun signOut(){
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener {
                    status.text = getString(R.string.signed_out)
                    detail.text = null
                    signed_in_buttons.visibility = View.GONE
                    button_start_verification.visibility =View.VISIBLE
                }
    }
}