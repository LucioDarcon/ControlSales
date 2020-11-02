package com.example.controlsales.dialogs

import android.Manifest
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.controlsales.R
import com.example.controlsales.business.CustomerBusiness
import com.example.controlsales.databinding.DialogCustomerBinding
import com.example.controlsales.entities.Customer
import kotlinx.android.synthetic.main.dialog_customer.*
import java.io.File


class RegisterCustomerDialog(
    context: Context,
    customer: Customer,
    onEditCustomer: OnEditCustomer.View
) : Dialog(context),
    View.OnClickListener, OnEditCustomer.Dialog {

    private lateinit var mCustomerBusiness: CustomerBusiness
    private var mCustomer: Customer = customer
    private lateinit var mBinding: DialogCustomerBinding
    private var mView = onEditCustomer
    private var mPathImageCustomer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_customer,
            null,
            false
        )
        mCustomerBusiness = CustomerBusiness(context)

        mView.getInstanceDialog(this)
        setContentView(mBinding.root)
        setListeners()
        configureDialog()
        configureWindow()
        setEntityCustomToTextFieldDialog(mCustomer)
    }

    private fun setEntityCustomToTextFieldDialog(customer: Customer) {
        if (customer.id != 0) {
            mBinding.customer = customer
            if (customer.image == "") {
                mBinding.dialogCustomerCameraImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_camera_alt_24))
            } else {
                mBinding.dialogCustomerCameraImageView.setImageURI(Uri.fromFile(
                    File(customer.image)
                ))
            }
            mBinding.executePendingBindings()
        } else {
            mBinding.dialogCustomerCameraImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_camera_alt_24))
        }
    }

    private fun configureWindow() {
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    private fun configureDialog() {
        btnSaveCustomer.animation = AnimationUtils.loadAnimation(context, R.anim.from_bottom)
    }

    private fun validationFields(): Boolean {
        return edtNameCustomer.text.toString() != ""
                && edtCpfCustomer.text.toString() != ""
                && edtTelephoneCustomer.text.toString() != ""
                && edtEmailCustomer.text.toString() != ""
    }

    private fun setListeners() {
        btnSaveCustomer.setOnClickListener(this)
        btnCloseDialogCustomer.setOnClickListener(this)
        mBinding.dialogCustomerCameraImageView.setOnClickListener(this)
    }

    private fun saveCustomer() {
        if (validationFields()) {

            var mCustomer = Customer()
            if (mBinding.customer != null) {
                mCustomer = Customer(
                    mBinding.customer!!.id,
                    name = edtNameCustomer.text.toString(),
                    email = edtEmailCustomer.text.toString(),
                    telephone = edtTelephoneCustomer.text.toString(),
                    cpf = edtCpfCustomer.text.toString(),
                    idAdm = mBinding.customer!!.idAdm,
                    image = mPathImageCustomer
                )
            } else {
                mCustomer = Customer(
                    name = edtNameCustomer.text.toString(),
                    email = edtEmailCustomer.text.toString(),
                    telephone = edtTelephoneCustomer.text.toString(),
                    cpf = edtCpfCustomer.text.toString(),
                    image = mPathImageCustomer
                )
            }

            val result = mCustomerBusiness.insertCustomer(mCustomer)

            if (result > 0) Toast.makeText(context, R.string.salvo_com_sucesso, Toast.LENGTH_LONG).show()
            else Toast.makeText(context, R.string.erro_ao_salvar, Toast.LENGTH_LONG).show()
            mView.updateData()
            dismiss()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnCloseDialogCustomer -> {
                dismiss()
            }
            R.id.btnSaveCustomer -> {
                saveCustomer()
            }
            R.id.dialog_customer_camera_image_view -> {
                mView.getImage()
            }
        }
    }

    override fun showImage(uri: Uri, path: String) {
        mPathImageCustomer = path
        mBinding.dialogCustomerCameraImageView.setImageURI(uri)
    }


}