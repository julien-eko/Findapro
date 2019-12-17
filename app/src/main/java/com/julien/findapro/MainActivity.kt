package com.julien.findapro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{


    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null){
            val intent = Intent(this,FirebaseUIActivity::class.java)
            startActivity(intent)

        }

        this.configureToolBar()

        this.configureDrawerLayout()

        this.configureNavigationView()



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var itemid = item?.itemId

        //if itemid = ....

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var itemid = p0?.itemId

        if (itemid == R.id.activity_main_drawer_map){

            val intent = Intent(this,InformationForm::class.java)
            startActivity(intent)
              }

        if (itemid == R.id.activity_main_drawer_signout){
            AuthUI.getInstance().signOut(this)
            val intent = Intent(this,FirebaseUIActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(p0)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

    }

    private fun configureToolBar() {
        toolbar = findViewById(R.id.main_activity_toolbar)
        setSupportActionBar(toolbar)
    }

    private fun configureDrawerLayout() {
        drawerLayout = findViewById(R.id.main_activity_drawer_layout)
        toolbar = findViewById(R.id.main_activity_toolbar)
        var toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun configureNavigationView() {
        navigationView = findViewById(R.id.main_activity_nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }
}
