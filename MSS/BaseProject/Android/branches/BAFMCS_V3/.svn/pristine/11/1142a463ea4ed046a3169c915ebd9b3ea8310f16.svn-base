package com.adins.mss.base.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.adins.mss.base.GlobalData
import com.adins.mss.base.NewMainActivity
import com.adins.mss.base.R
import com.adins.mss.base.dialogfragments.SettingsDialog
import com.adins.mss.constant.Global
import com.adins.mss.constant.Global.SHOW_USERHELP_DELAY_DEFAULT
import com.adins.mss.foundation.UserHelp.UserHelp
import kotlinx.android.synthetic.main.fragment_setting.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingFragment : Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLanguages.setOnClickListener { languageSetting() }
        mPrinting.setOnClickListener { printSetting() }


        val appName = GlobalData.getSharedGlobalData().application
        if (appName.equals("MC")){
            mPrinting.visibility = View.VISIBLE
        }else{
            mPrinting.visibility = View.GONE
        }
        val handler = Handler()
        handler.postDelayed({
            UserHelp.showAllUserHelp(this@SettingFragment.getActivity(),
                    this@SettingFragment.javaClass.getSimpleName())
        }, SHOW_USERHELP_DELAY_DEFAULT.toLong())
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.title_mn_setting)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId == R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                val handler = Handler()
                handler.postDelayed({
                    UserHelp.showAllUserHelp(this@SettingFragment.getActivity(),
                            this@SettingFragment.javaClass.getSimpleName())
                }, SHOW_USERHELP_DELAY_DEFAULT.toLong())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun languageSetting() {
//        MainMenuActivity.isFromSetting = true
//        val intent = Intent(context, SettingActivity::class.java)
//        startActivityForResult(intent, 199)
        val fragment    = SettingsDialog()
        val transaction = NewMainActivity.fragmentManager.beginTransaction()
//        val transaction = MCMainMenuActivity.fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate)
        transaction.replace(R.id.content_frame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun printSetting() {
        val fragment    = PrintingServiceFragment()
        val transaction = NewMainActivity.fragmentManager.beginTransaction()
//        val transaction = MCMainMenuActivity.fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate)
        transaction.replace(R.id.content_frame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onAttach(context: Context) {
        setHasOptionsMenu(true)
        super.onAttach(context)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 199) {
            activity?.recreate()
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): SettingFragment {
            val fragment = SettingFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}
