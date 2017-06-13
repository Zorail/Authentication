package zorail.rohan.com.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener{

    lateinit private var mAuth:FirebaseAuth
    lateinit var mGoogleApiClient:GoogleApiClient
    lateinit var dialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.defaultid))
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build()
        mAuth = FirebaseAuth.getInstance()
        sign_in_button.setOnClickListener { singIn() }
        sign_out_button.setOnClickListener { signOut() }
        disconnect_button.setOnClickListener { revokeAccess() }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        toast("Google Play Services Error" + p0.errorMessage)
    }

    fun singIn(){
        val intent:Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(intent,9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 9001){
            dialog = indeterminateProgressDialog(message = "Please wait a bit",title = "Fetching Data")
            val result:GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                val account:GoogleSignInAccount = result.signInAccount!!
                fireBaseAuthWithGoogle(account)
            }
        }
    }
    fun fireBaseAuthWithGoogle(acct:GoogleSignInAccount){
        val credential:AuthCredential = GoogleAuthProvider.getCredential(acct.idToken,null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { p0 ->
                    if(p0.isSuccessful){
                        dialog.dismiss()
                        sign_in_button.visibility = View.GONE
                        sign_out_and_disconnect.visibility = View.VISIBLE
                        val user:FirebaseUser = mAuth.currentUser!!
                        status.text = user.displayName
                    }
                }
    }
    fun signOut(){
        mAuth.signOut()
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            sign_in_button.visibility = View.VISIBLE
            sign_out_and_disconnect.visibility = View.GONE
            status.text = "Signed Out"
        }
    }
    fun revokeAccess(){
        mAuth.signOut()

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback {
            sign_in_button.visibility = View.VISIBLE
            sign_out_and_disconnect.visibility = View.GONE
            status.text = "Signed Out" }
    }
}
