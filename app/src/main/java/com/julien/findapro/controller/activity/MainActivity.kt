package com.julien.findapro.controller.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.julien.findapro.R
import com.julien.findapro.utils.CircleTransform
import com.julien.findapro.utils.Communicator
import com.julien.findapro.controller.fragment.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Communicator,
    NavigationView.OnNavigationItemSelectedListener {


    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(getString(R.string.isPro), 0)

        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(
                this,
                FirebaseUIActivity::class.java
            )
            startActivity(intent)

        } else {
            supportFragmentManager.inTransaction {


                if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
                    replace(
                        R.id.main_activity_frame_layout,
                        AssignmentsListFragment()
                    )
                } else {

                    replace(
                        R.id.main_activity_frame_layout,
                        UserListFragment()
                    )
                }
            }

            this.configureToolBar()

            this.configureDrawerLayout()

            this.configureNavigationView()

            this.configureBottomNavigationView()


        }


    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val itemid = p0.itemId

        if (itemid == R.id.activity_main_drawer_notification) {
            val intent = Intent(
                this,
                NotificationListActivity::class.java
            )
            startActivity(intent)
        }

        if (itemid == R.id.activity_main_drawer_planning) {
            val intent = Intent(
                this,
                PlanningActivity::class.java
            )
            startActivity(intent)
        }


        if (itemid == R.id.activity_main_drawer_information) {
            val intent = Intent(
                this,
                InformationForm::class.java
            )
            intent.putExtra("edit", true)
            startActivity(intent)
        }

        if (itemid == R.id.activity_main_drawer_signout) {
            AuthUI.getInstance().signOut(this)
            val intent = Intent(
                this,
                FirebaseUIActivity::class.java
            )
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
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    //display info user
    private fun configureNavigationView() {
        navigationView = findViewById(R.id.main_activity_nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val hview = navigationView.inflateHeaderView(R.layout.main_activity_nav_header)

        val fullName = hview.findViewById<TextView>(R.id.main_activity_name_header)
        val image = hview.findViewById<ImageView>(R.id.main_activity_photo_header)
        val email = hview.findViewById<TextView>(R.id.main_activity_email_header)

        fullName.text = FirebaseAuth.getInstance().currentUser?.displayName
        email.text = FirebaseAuth.getInstance().currentUser?.email

        Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl).transform(
            CircleTransform()
        ).into(image)

    }

    private fun configureBottomNavigationView() {
        activity_main_bottom_navigation.setOnNavigationItemSelectedListener { item ->
            updateFragment(item.itemId)
        }
    }

    //change fragment
    private fun updateFragment(item: Int): Boolean {
        if (item == R.id.action_list) {
            supportFragmentManager.inTransaction {


                if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
                    //Toast.makeText(baseContext,"pro",Toast.LENGTH_SHORT).show()
                    replace(
                        R.id.main_activity_frame_layout,
                        AssignmentsListFragment()
                    )
                } else {

                    replace(
                        R.id.main_activity_frame_layout,
                        UserListFragment()
                    )
                }

            }
        }
        if (item == R.id.action_planning) {
            val user: String = if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
                getString(R.string.proUserId)
            } else {
                getString(R.string.userId)
            }
            supportFragmentManager.inTransaction {
                replace(
                    R.id.main_activity_frame_layout,
                    AssignmentsInProgressFragment(), user
                )

            }

        }
        if (item == R.id.action_message) {
            val user: String = if (sharedPreferences.getBoolean(getString(R.string.isPro), false)) {
                getString(R.string.proUserId)
            } else {
                getString(R.string.userId)
            }
            supportFragmentManager.inTransaction {
                replace(
                    R.id.main_activity_frame_layout,
                    ChatListFragment(), user
                )

            }

        }
        return true
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    //////////////////////////////
    /// COMMUNICATION WITH FRAGMENT
    ///////////////////////////////
    override fun passDataUserList(job: String, maxDistance: Float, rating: Double) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.job), job)
        bundle.putFloat(getString(R.string.maxDistance), maxDistance)
        bundle.putDouble(getString(R.string.rating), rating)

        val transaction = this.supportFragmentManager.beginTransaction()
        val userListFragment = UserListFragment()
        userListFragment.arguments = bundle

        transaction.replace(R.id.main_activity_frame_layout, userListFragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    override fun passDataAssignmentList(maxDistance: Float, rating: Double) {
        val bundle = Bundle()
        bundle.putFloat(getString(R.string.maxDistance), maxDistance)
        bundle.putDouble(getString(R.string.rating), rating)

        val transaction = this.supportFragmentManager.beginTransaction()
        val assignmentListFragment = AssignmentsListFragment()
        assignmentListFragment.arguments = bundle

        transaction.replace(R.id.main_activity_frame_layout, assignmentListFragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    override fun passDataAssignmentInProgressList(status: String) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.status), status)

        val transaction = this.supportFragmentManager.beginTransaction()
        val assignmentInProgressFragment = AssignmentsInProgressFragment()
        assignmentInProgressFragment.arguments = bundle

        transaction.replace(R.id.main_activity_frame_layout, assignmentInProgressFragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }
}
