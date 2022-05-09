package com.example.nfc.ui.fragment


import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.nfc.common.Constant
import com.example.nfc.databinding.FragmentNfcBinding
import com.example.nfc.model.Passport
import ercanduman.cardreader.utils.KeyStoreUtils
import ercanduman.cardreader.utils.NFCDocumentTag
import io.reactivex.disposables.CompositeDisposable
import net.sf.scuba.smartcards.CardServiceException
import net.sf.scuba.smartcards.ISO7816
import org.jmrtd.AccessDeniedException
import org.jmrtd.BACDeniedException
import org.jmrtd.MRTDTrustStore
import org.jmrtd.PACEException
import org.jmrtd.lds.icao.MRZInfo
import java.security.Security


class NfcFragment : Fragment() {


    private var _binding: FragmentNfcBinding? = null
    private val binding get() = _binding!!

    private var mrzInfo: MRZInfo? = null
    private var nfcFragmentListener: NfcFragmentListener? = null


    private var mHandler = Handler(Looper.getMainLooper())
    private var disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNfcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments!!.containsKey(Constant.MRZ_RESULT)) {
            mrzInfo = arguments.getSerializable(Constant.MRZ_RESULT) as MRZInfo
        }
    }

    fun handleNfcTag(intent: Intent?) {
        if (intent == null || intent.extras == null) {
            return
        }
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return

        val folder = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
        val keyStore = KeyStoreUtils().readKeystoreFromFile(folder)

        val mrtdTrustStore = MRTDTrustStore()
        if (keyStore != null) {
            val certStore = KeyStoreUtils().toCertStore(keyStore = keyStore)

            mrtdTrustStore.addAsCSCACertStore(certStore)
        }


        val subscribe = NFCDocumentTag().handleTag(
            requireContext(),
            tag,
            mrzInfo!!,
            mrtdTrustStore,
            object : NFCDocumentTag.PassportCallback {

                override fun onPassportReadStart() {
                    onNFCSReadStart()
                }

                override fun onPassportReadFinish() {
                    onNFCReadFinish()
                }

                override fun onPassportRead(passport: Passport?) {
                    this@NfcFragment.onPassportRead(passport)

                }

                override fun onAccessDeniedException(exception: AccessDeniedException) {
                    Toast.makeText(
                        context,
                        "Authenaction has failed! Please try to scan the documnet.",
                        Toast.LENGTH_SHORT
                    ).show()
                    exception.printStackTrace()
                    this@NfcFragment.onCardException(exception)

                }

                override fun onBACDeniedException(exception: BACDeniedException) {
                    Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                    this@NfcFragment.onCardException(exception)
                }

                override fun onPACEException(exception: PACEException) {
                    Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                    this@NfcFragment.onCardException(exception)
                }

                override fun onCardException(exception: CardServiceException) {
                    val sw = exception.sw.toShort()
                    when (sw) {
                        ISO7816.SW_CLA_NOT_SUPPORTED -> {
                            Toast.makeText(
                                context,
                                "Impossible to read the document. Passport doesn\\'t support CLA.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    this@NfcFragment.onCardException(exception)
                }

                override fun onGeneralException(exception: Exception?) {
                    Toast.makeText(context, exception!!.toString(), Toast.LENGTH_SHORT).show()
                    this@NfcFragment.onCardException(exception)
                }
            })

        disposable.add(subscribe)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity
        if (activity is NfcFragment.NfcFragmentListener) {
            nfcFragmentListener = activity
        }
    }

    override fun onDetach() {
        nfcFragmentListener = null
        super.onDetach()
    }


    override fun onResume() {
        super.onResume()

        binding.dateOfBirth.text = mrzInfo!!.dateOfBirth
        binding.documentNumber.text = mrzInfo!!.documentNumber
        binding.expiryDate.text = mrzInfo!!.dateOfExpiry

        if (nfcFragmentListener != null) {
            nfcFragmentListener!!.onEnableNfc()
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcFragmentListener != null) {
            nfcFragmentListener!!.onDisableNfc()
        }
    }

    override fun onDestroyView() {
        if (!disposable.isDisposed) {
            disposable.dispose();
        }
        super.onDestroyView()
        _binding = null
    }

    private fun onNFCSReadStart() {
        Log.d(TAG, "onNFCSReadStart")
        mHandler.post { binding.progressbar.isVisible = true }

    }

    private fun onNFCReadFinish() {
        Log.d(TAG, "onNFCReadFinish")
        mHandler.post { binding.progressbar.isVisible = false }
    }

    private fun onCardException(cardException: Exception?) {
        mHandler.post {
            if (nfcFragmentListener != null) {
                nfcFragmentListener!!.onCardException(cardException)
            }
        }
    }

    private fun onPassportRead(passport: Passport?) {
        mHandler.post {
            if (nfcFragmentListener != null) {
                nfcFragmentListener!!.onPassportRead(passport)
            }
        }
    }

    interface NfcFragmentListener {
        fun onEnableNfc()
        fun onDisableNfc()
        fun onPassportRead(passport: Passport?)
        fun onCardException(cardException: Exception?)
    }

    companion object {
        private val TAG = NfcFragment::class.java.simpleName

        init {
            Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        }

        fun newInstance(mrzInfo: MRZInfo): NfcFragment {
            val myFragment = NfcFragment()
            val args = Bundle()
            args.putSerializable(Constant.MRZ_RESULT, mrzInfo)
            myFragment.arguments = args
            return myFragment
        }
    }
}
