package com.adins.mss.base.fragments


import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Toast

import com.adins.mss.base.R
import com.adins.mss.constant.Global
import com.adins.mss.foundation.UserHelp.UserHelp
import com.adins.mss.foundation.dialog.NiftyDialogBuilder
import kotlinx.android.synthetic.main.fragment_printing.*
import zj.com.cn.bluetooth.sdk.DeviceListActivity
import zj.com.cn.bluetooth.sdk.Main_Activity1

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PrintingServiceFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var printPrefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        printPrefs = activity!!.getSharedPreferences(PRINT_PREFS, Context.MODE_PRIVATE)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_printing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (printPrefs.contains(EXTRA_PRINTER_NAME)) {
            val address = printPrefs.getString(EXTRA_PRINTER_ADDRESS, "")

            defaultPrintingService.visibility = View.VISIBLE
            printDefault.text = printPrefs.getString(EXTRA_PRINTER_NAME, "Not available")
            printTest.setOnClickListener {
                if (address != null) {
                    printTest(address)
                }
            }
        } else {
            defaultPrintingService.visibility = View.GONE
            printTest.visibility = View.GONE
        }

        choosePrinter.setOnClickListener { selectPrinter() }
        defaultPrintingService.setOnClickListener {
            val dialog = NiftyDialogBuilder.getInstance(context)
            dialog.isCancelable(false)
            dialog.isCancelableOnTouchOutside(false)
            dialog.withTitle(printDefault.text)
            dialog.withMessage(String.format(getString(R.string.message_remove_default_printer), printDefault.text))
//            dialog.withMessage(String.format(getString(R.string.message_remove_default_printer), printDefault.text))
            dialog.withButton1Text(getString(R.string.btnDelete))
            dialog.withButton2Text(getString(R.string.btnCancel))
            dialog.setButton1Click {

                val address = printPrefs.getString(EXTRA_PRINTER_ADDRESS, "")
                val device = bluetoothAdapter.getRemoteDevice(address)
                printPrefs.edit().clear().apply()

                if (device.bondState == BluetoothDevice.BOND_BONDED) {
                    Main_Activity1.unpairDevice(device)
                }

                printDefault.text = getString(R.string.not_available)
                defaultPrintingService.visibility = View.GONE
                printTest.visibility = View.GONE
                dialog.dismiss()

            }
            dialog.setButton2Click {
                dialog.dismiss()
            }

            dialog.show()
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
            val handler = Handler()
            handler.postDelayed({
                if(!UserHelp.isActive)
                UserHelp.showAllUserHelp(this@PrintingServiceFragment.activity,
                        this@PrintingServiceFragment.javaClass.simpleName)
            }, Global.SHOW_USERHELP_DELAY_DEFAULT.toLong())
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId == R.id.mnGuide){
            if(!Global.BACKPRESS_RESTRICTION) {
                val handler = Handler()
                handler.postDelayed({
                    if (!UserHelp.isActive)
                        UserHelp.showAllUserHelp(this@PrintingServiceFragment.getActivity(),
                                this@PrintingServiceFragment.javaClass.getSimpleName())
                }, Global.SHOW_USERHELP_DELAY_DEFAULT.toLong())
                view?.viewTreeObserver?.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
                    if (!UserHelp.isActive)
                        UserHelp.showAllUserHelp(this@PrintingServiceFragment.getActivity(),
                                this@PrintingServiceFragment.javaClass.getSimpleName())
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun printTest(address: String) {
        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
            Toast.makeText(context,getString(R.string.bluetooth_message),Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(context, Main_Activity1::class.java)
            intent.putExtra(BluetoothDevice.EXTRA_DEVICE, address)
            intent.putExtra("PRINT_TEST", true)
            startActivity(intent)
        }
    }

    private fun selectPrinter() {
        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        }
        val serverIntent = Intent(context, DeviceListActivity::class.java)
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE)
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.title_mn_printing)
        activity?.registerReceiver(bluetoothBondDeviceStateChange, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(bluetoothBondDeviceStateChange)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CONNECT_DEVICE) {
            // When DeviceListActivityy returns with a device to connect
            if (resultCode == Activity.RESULT_OK && data != null) {
                // Get the BLuetoothDevice object
                if (data.hasExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) {
                    // Get the device MAC address
                    val address = data.extras!!.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)
                    val device = bluetoothAdapter.getRemoteDevice(address)

                    if (printPrefs.contains(EXTRA_PRINTER_NAME)) {
                        val oldPrinterName = printPrefs.getString(EXTRA_PRINTER_NAME, "")
                        if (oldPrinterName != device.name) {
                            updateUI(device)
                            updatePrinterPrefs(device)
                        }

                        //Update printer Address
                        printPrefs.edit()
                                .putString(EXTRA_PRINTER_ADDRESS, device.address)
                                .apply()
                    } else {
                        updateUI(device)
                        updatePrinterPrefs(device)
                    }

                } else if (data.hasExtra(DeviceListActivity.EXTRA_ACTION_DELETE)) { //Nendi - 2019-01-08 | Add request unPairing bluetooth device
                    if (data.extras!!.getInt(DeviceListActivity.EXTRA_ACTION_DELETE) == 1) {
                        val device = data.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        Main_Activity1.unpairDevice(device)
                    }
                } else { //Nendi - 2019-01-08 | Add request pairing bluetooth device
                    val device = data.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    Main_Activity1.pairDevice(device)
                }
            }
        }
    }

    private fun updatePrinterPrefs(device: BluetoothDevice) {
        // Add printer type to preferences
        val printType: Int = if (device.name.toLowerCase().contains("woosim")) 0 else 1

        printPrefs.edit()
                .putInt(EXTRA_PRINTER_TYPE, printType)
                .putString(EXTRA_PRINTER_NAME, device.name)
                .putString(EXTRA_PRINTER_ADDRESS, device.address)
                .apply()
    }

    private fun updateUI(device: BluetoothDevice) {
        if (printTest.visibility == View.GONE) {
            printTest.visibility = View.VISIBLE
            defaultPrintingService.visibility = View.VISIBLE
        }

        printDefault.text = device.name
        printTest.setOnClickListener { printTest(device.address) }
    }

    //Method for request pairing bluetooth device
    private fun pairDevice(device: BluetoothDevice) {
        try {
            Log.d("pairDevice()", "Start Pairing...")
            val m = device.javaClass.getMethod("createBond", null)
            m.invoke(device, null as Array<Any>?)
            Log.d("pairDevice()", "Pairing finished.")
        } catch (e: Exception) {
            Log.e("pairDevice()", e.message!!)
        }

    }

    private fun unpairDevice(device: BluetoothDevice) {
        try {
            Log.d("unpairDevice()", "Start Un-Pairing...")
            val m = device.javaClass.getMethod("removeBond", null)
            m.invoke(device, null as Array<Any>?)
            Log.d("unpairDevice()", "Un-Pairing finished.")
        } catch (e: Exception) {
            Log.e("unpairDevice()", e.message!!)
        }

    }

    //Nendi: 2019-01-08 | Bond device state change listener
    private val bluetoothBondDeviceStateChange = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == intent.action) {
                val mDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (mDevice?.bondState == BluetoothDevice.BOND_BONDED) {
                    updateUI(mDevice)
                    updatePrinterPrefs(mDevice)
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        val PRINT_PREFS = "printPrefs"
        val EXTRA_PRINTER_TYPE = "printerType"
        val EXTRA_PRINTER_NAME = "printerName"
        val EXTRA_PRINTER_ADDRESS = "printerAddress"
        val REQUEST_CONNECT_DEVICE = 1
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrintingServiceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PrintingServiceFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

}
