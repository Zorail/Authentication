package zorail.rohan.com.authentication

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Created by zorail on 13-Jun-17.
 */
class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
    }
}
