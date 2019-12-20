package com.julien.findapro

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_activity_nav_header.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{


    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView

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

        this.configureBottomNavigationView()

        supportFragmentManager.inTransaction {
            add(R.id.main_activity_frame_layout,UserListFragment())
        }

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



        if (itemid == R.id.activity_main_drawer_information){
            val intent = Intent(this,InformationForm::class.java)
            intent.putExtra("edit",true)
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
        val hview = navigationView.inflateHeaderView(R.layout.main_activity_nav_header)

        val fullName = hview.findViewById<TextView>(R.id.main_activity_name_header)
        val image = hview.findViewById<ImageView>(R.id.main_activity_photo_header)
        val email = hview.findViewById<TextView>(R.id.main_activity_email_header)

        fullName.text = FirebaseAuth.getInstance().currentUser?.displayName
        email.text = FirebaseAuth.getInstance().currentUser?.email

        Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl).into(image)

    }

    private fun configureBottomNavigationView(){
        activity_main_bottom_navigation.setOnNavigationItemSelectedListener { item ->
            updateFragment(item.itemId)
        }
    }

    private fun updateFragment(item: Int):Boolean{
        if(item == R.id.action_list ){
            Toast.makeText(this,"1",Toast.LENGTH_SHORT).show()
        }
        if (item == R.id.action_planning){
            Toast.makeText(this,"2",Toast.LENGTH_SHORT).show()
        }
        if(item == R.id.action_message){
            Toast.makeText(this,"3",Toast.LENGTH_SHORT).show()
        }
        return true
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }
}
