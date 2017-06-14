package zorail.rohan.com.authentication

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

/**
 * Created by zorail on 13-Jun-17.
 */
class MyApp : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        val config:TwitterConfig = TwitterConfig.Builder(this)
                .logger(object :DefaultLogger(Log.DEBUG){})
                .twitterAuthConfig(object :TwitterAuthConfig(getString(R.string.twitter_Key),getString(R.string.twitter_Secret)){})
                .debug(true)
                .build()
        Twitter.initialize(config)
    }
}
