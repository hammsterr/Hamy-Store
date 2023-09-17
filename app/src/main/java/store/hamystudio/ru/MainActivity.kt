package store.hamystudio.ru

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val deeplinkUrl = "store.hamystudio.ru://hamystore.web.app/-NdL8LNMMBFvLmgGiTQA"
        val button = findViewById<Button>(R.id.homeCheckUpdates)
        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(deeplinkUrl)
            startActivity(intent)
        }

        val ButtonApps = findViewById<Button>(R.id.goToApps)
        ButtonApps.setOnClickListener {
            val intent = Intent(this, ApplicationsActivity::class.java)
            startActivity(intent)
        }

    }
}
