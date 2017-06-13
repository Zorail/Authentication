package zorail.rohan.com.authentication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_facebook.*


/**
 * Created by zorail on 13-Jun-17.
 */
class FacebookLoginActivity :AppCompatActivity(){
    lateinit var mAuth:FirebaseAuth
    lateinit var mCallbackManager:CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook)
        mAuth = FirebaseAuth.getInstance()
        mCallbackManager = CallbackManager.Factory.create()
        button_facebook_signout.setOnClickListener { signOut() }
        button_facebook_login.setReadPermissions("email","public_profile")
        button_facebook_login.registerCallback(mCallbackManager,object :FacebookCallback<LoginResult>{
            override fun onCancel() {
               Toast.makeText(applicationContext,"Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(p0: LoginResult?) {
               handleFacebookAccessToken(p0!!.accessToken)
            }

            override fun onError(p0: FacebookException?) {
              Toast.makeText(applicationContext,p0!!.message,Toast.LENGTH_LONG).show()
            }

        })

    }
    fun handleFacebookAccessToken(token:AccessToken){
        val credential:AuthCredential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { p0 ->
                    if (p0.isSuccessful){
                        val user:FirebaseUser = mAuth.currentUser!!
                        detail.text = user.displayName
                        button_facebook_login.visibility = View.GONE
                        button_facebook_signout.visibility = View.VISIBLE
                    }
                }
    }
    fun signOut(){
        mAuth.signOut()
        LoginManager.getInstance().logOut()
        button_facebook_signout.visibility = View.GONE
        button_facebook_login.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager.onActivityResult(requestCode,resultCode,data)
    }
}