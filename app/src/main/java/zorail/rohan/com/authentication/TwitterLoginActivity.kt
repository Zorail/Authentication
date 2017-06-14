package zorail.rohan.com.authentication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.activity_twitter.*


/**
 * Created by zorail on 14-Jun-17.
 */
class TwitterLoginActivity:AppCompatActivity(){
    lateinit private var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)
        mAuth = FirebaseAuth.getInstance()
        button_twitter_login.callback = object : Callback<TwitterSession>(){
            override fun success(p0: Result<TwitterSession>?) {
                handleTwitterSession(p0!!.data)
            }

            override fun failure(p0: TwitterException?) {
                Toast.makeText(applicationContext,p0!!.message,Toast.LENGTH_LONG).show()
            }

        }
        button_twitter_signout.setOnClickListener { signOut() }

    }
    fun handleTwitterSession(session:TwitterSession){
        val credential:AuthCredential = TwitterAuthProvider.getCredential(session.authToken.token,session.authToken.secret)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { p0 ->
                    if(p0.isSuccessful){
                        val user:FirebaseUser = mAuth.currentUser!!
                        status.text = user.displayName
                        detail.text = user.uid
                        button_twitter_login.visibility = View.INVISIBLE
                        button_twitter_signout.visibility = View.VISIBLE
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        button_twitter_login.onActivityResult(requestCode,resultCode,data)
    }
    fun signOut(){
        mAuth.signOut()
        status.text= "signed Out"
        button_twitter_signout.visibility = View.INVISIBLE
        button_twitter_login.visibility = View.VISIBLE
    }
}