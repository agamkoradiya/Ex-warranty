package com.example.expiredcalculator.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expiredcalculator.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import kotlinx.android.synthetic.main.activity_add_new_item.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.HashMap


class AddNewItemActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val GALLERY_CODE: Int = 0
    private var itemNo: Int = Random().nextInt()

    var databaseReference: DatabaseReference? = null
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mAuth: FirebaseAuth
    private lateinit var imageUri: Uri

    var calender: Calendar = Calendar.getInstance()
    private var currentYear: Int = 0
    private var currentDay: Int = 0
    private var currentMonth: Int = 0


    private var progressMonth: Int = 0
    private var progressYear: Int = 0

    // Final variable
    private var itemId = itemNo.toString()
    private lateinit var productName: String
    private var productIconUrl: String = "null"
    private var durationMonth: Int = 0
    private var durationYear: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_item)

        // Pre-Requirement
        mStorage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser


        // GET CURRENT CALENDER
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        currentYear = Calendar.getInstance().get(Calendar.YEAR)


        // SET ALL DATA IN UI
        dateOfPurchaseTxt.text = "$currentDay ${getMonth(currentMonth)} $currentYear"
        dateOfExpiryTxt.text = "$currentDay ${getMonth(currentMonth)} $currentYear"

        monthNumberPicker.doOnProgressChanged { numberPicker, progress, formUser ->
            progressMonth = progress
            calculation(progressMonth, progressYear * 12)
        }

        yearNumberPicker.doOnProgressChanged { numberPicker, progress, formUser ->
            progressYear = progress
            calculation(progressMonth, progressYear * 12)
        }
    }

    // Open gallery
    fun pickImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_CODE)
    }

    // Gallery Result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == GALLERY_CODE) {
            imageUri = data?.data!!
            upload()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload() {
        progressBar.visibility = View.VISIBLE
        val mStorageReference: StorageReference =
            mStorage.reference.child(mAuth.currentUser?.uid!! + "---" + itemId)

        mStorageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            progressBar.visibility = View.GONE
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                productIconUrl = it.toString()
                Log.d("TAG", "Url --------------> $productIconUrl")
            }
            Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.d("TAG", "upload: exception -> ")
        }
    }

    @SuppressLint("SetTextI18n", "LogNotTimber")
    private fun calculation(progressMonth: Int, progressYear: Int) {
        Log.i("TAG", "calculation: ->  progressMonth -> $progressMonth")
        Log.i("TAG", "calculation: ->  progressYear -> $progressYear")
        Log.i("TAG", "calculation: ->  currentMonth -> $currentMonth")

        val both = progressMonth + currentMonth + progressYear
        Log.i("TAG", "calculation: ->  both -> $both")

        durationMonth = both % 12
        durationYear = both / 12

        Log.i("TAG", "calculation: ->  durationMonth -> $durationMonth")
        Log.i("TAG", "calculation: ->  durationYear -> $durationYear")

        Log.i("TAG", " \n\n")
        Log.i("TAG", " \n\n")

        dateOfExpiryTxt.text =
            "$currentDay ${getMonth(durationMonth)} ${currentYear + durationYear}"

    }


    @SuppressLint("SetTextI18n")
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        currentDay = dayOfMonth
        currentMonth = month
        currentYear = year
        calculation(progressMonth, progressYear * 12)
        dateOfPurchaseTxt.text = "$currentDay ${getMonth(currentMonth)} $currentYear"
    }

    fun dateOfPurchaseTxt(view: View) {
        val datePickerDialog =
            DatePickerDialog(this@AddNewItemActivity, this, currentYear, currentMonth, currentDay)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun getMonth(month: Int): String? {
        return DateFormatSymbols().months[month]
    }

    fun saveBtn(view: View) {

        // Product Name Validation
        productName = product_name.text.toString().trim()

        if (productName.isEmpty()) {
            filledTextField.error = "Required"
            filledTextField.requestFocus()
        } else {
            progressBar.visibility = View.VISIBLE
            Log.i("TAG", "btn: ------------------->>>>>>        $currentDay  -  $durationMonth  -  ${currentYear + durationYear}   ")
            databaseReference =
                FirebaseDatabase.getInstance().reference.child(mAuth.currentUser?.uid.toString())
                    .child("Item$itemNo")
            databaseReference!!.keepSynced(true)

            val itemMap = HashMap<String, Any>()
            itemMap["itemId"] = itemId
            itemMap["productName"] = productName
            itemMap["productIconUrl"] = productIconUrl
            itemMap["expDate"] = currentDay
            itemMap["expMonth"] = durationMonth
            val expYear = currentYear + durationYear
            itemMap["expYear"] = expYear
            itemMap["purchasedDate"] = currentDay
            itemMap["purchasedMonth"] = currentMonth
            itemMap["purchasedYear"] = currentYear

            databaseReference!!.setValue(itemMap).addOnCompleteListener {
                progressBar.visibility = View.GONE
                if (it.isSuccessful()) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Log.i("TAG", "ERROR ${it.exception}")
                }
            }
        }
    }
}