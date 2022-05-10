package com.example.nfc.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nfc.common.Constant
import com.example.nfc.databinding.FragmentPassportDetailsBinding
import com.example.nfc.model.Passport
import com.example.nfc.ui.activities.NfcActivity
import com.example.nfc.util.StringUtils
import org.jmrtd.lds.icao.MRZInfo
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.x500.X500Principal

class PassportDetailsFragment : Fragment() {

    private var _binding: FragmentPassportDetailsBinding? = null
    private val binding get() = _binding!!
    private var simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    private var passport: Passport? = null
    private var mrzInfo: MRZInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassportDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments!!.containsKey(Constant.KEY_PASSPORT)) {
            passport = arguments.getParcelable(Constant.KEY_PASSPORT)
        }

        mrzInfo = (activity as NfcActivity).mrzInfo

        setViewData(passport)
    }

    companion object {
        fun newInstance(passport: Passport): PassportDetailsFragment {
            val myFragment = PassportDetailsFragment()
            val args = Bundle()
            args.putParcelable(Constant.KEY_PASSPORT, passport)
            myFragment.arguments = args
            return myFragment
        }
    }


    private fun setViewData(passport: Passport?) {

        binding.userMrz.text = mrzInfo.toString()

        if (passport!!.face != null) {
            binding.userImage.setImageBitmap(passport.face)
        } else if (passport.portrait != null) {
            binding.userImage.setImageBitmap(passport.portrait)
        }

        val personDetails = passport.personDetails
        if (personDetails != null) {
            val surname = personDetails.primaryIdentifier!!.replace("<", "")
            val name = personDetails.secondaryIdentifier!!.replace("<", "")
            val fullName = "$name $surname"
            binding.userName.text = fullName
            binding.userGender.text = personDetails.gender!!.name
            binding.apply {
                userDocumentNumber.text = personDetails.documentNumber
                userSurname.text = surname
                userNameTwo.text = name
                userNationality.text = personDetails.nationality
                userGenderTwo.text = personDetails.gender!!.name
                userBirthDate.text = personDetails.dateOfBirth
                userIssuingCountry.text = personDetails.issuingState
                binding.userExpiryDate.text = personDetails.dateOfExpiry
            }
        }

        val additionalPersonDetails = passport.additionalPersonDetails
        if (additionalPersonDetails!!.personalNumber != null) {
            binding.identityNumber.text = additionalPersonDetails.personalNumber
        }
        if (additionalPersonDetails.placeOfBirth != null && additionalPersonDetails.placeOfBirth!!.isNotEmpty()) {
            binding.userPlaceBirthDate.text = arrayToString(additionalPersonDetails.placeOfBirth!!)
        }


        val additionalDocumentDetails = passport.additionalDocumentDetails
        if (additionalDocumentDetails != null) {
            if (additionalDocumentDetails.issuingAuthority != null) {
                binding.userIssuingAuthority.text = additionalDocumentDetails.issuingAuthority
            }
        }


        val sodFile = passport.sodFile
        if (sodFile != null) {
            val countrySigningCertificate = sodFile.issuerX500Principal
            val dnRFC2253 = countrySigningCertificate.getName(X500Principal.RFC2253)
            val dnCANONICAL = countrySigningCertificate.getName(X500Principal.CANONICAL)
            val dnRFC1779 = countrySigningCertificate.getName(X500Principal.RFC1779)

            val name = countrySigningCertificate.name
            //new X509Certificate(countrySigningCertificate);

            val docSigningCertificate = sodFile.docSigningCertificate

            if (docSigningCertificate != null) {

                binding.serialNumberValue.text = docSigningCertificate.serialNumber.toString()
                binding.publicKeyValue.text = docSigningCertificate.publicKey.algorithm
                binding.signatureAlgorithmValue.text = docSigningCertificate.sigAlgName

                try {
                    binding.certificateThumbprintValue.text = StringUtils.bytesToHex(
                        MessageDigest.getInstance("SHA-1").digest(
                            docSigningCertificate.encoded
                        )
                    ).uppercase()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.issuerValue.text = docSigningCertificate.issuerDN.name
                binding.subjectValue.text = docSigningCertificate.subjectDN.name
                binding.validFromValue.text =
                    simpleDateFormat.format(docSigningCertificate.notBefore)
                binding.validToValue.text = simpleDateFormat.format(docSigningCertificate.notAfter)
                print("a")
            }
        }
    }

    private fun arrayToString(array: List<String>): String {
        var temp = ""
        val iterator = array.iterator()
        while (iterator.hasNext()) {
            temp += iterator.next() + "\n"
        }
        if (temp.endsWith("\n")) {
            temp = temp.substring(0, temp.length - "\n".length)
        }
        return temp
    }
}
