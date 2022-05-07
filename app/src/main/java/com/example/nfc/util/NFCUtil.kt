package com.example.nfc.util

import android.content.Context
import android.graphics.Bitmap
import android.nfc.tech.IsoDep
import android.util.Log
import com.example.nfc.model.AdditionalPersonDetails
import com.example.nfc.model.DocType
import com.example.nfc.model.EDocument
import com.example.nfc.model.PersonDetails
import net.sf.scuba.smartcards.CardService
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.CardSecurityFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.icao.*
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.lds.iso19794.FingerImageInfo
import java.security.PublicKey

object NFCUtil {


    private var additionalPersonDetails = AdditionalPersonDetails()
    private var personDetails = PersonDetails()
    private var docPublicKey: PublicKey? = null
    private var personalFaceImage: Bitmap? = null
    private var personalFaceImageBase64: String? = null
    private var portraitImage: Bitmap? = null
    private var portraitImageBase64: String? = null
    private var signatureImage: Bitmap? = null
    private var signatureImageBase64: String? = null
    private var fingerprints: MutableList<Bitmap?> = arrayListOf()

    fun readNfcData(context: Context) {
        val isoDep: IsoDep? = null
        val bacKey: BACKeySpec? = null
        var docType: DocType = DocType.OTHER

        try {
            val cardService = CardService.getInstance(isoDep)
            cardService.open()
            val service = PassportService(
                cardService,
                PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                PassportService.DEFAULT_MAX_BLOCKSIZE,
                true,
                false
            )
            service.open()
            var paceSucceeded = false
            try {
                val cardSecurityFile =
                    CardSecurityFile(service.getInputStream(PassportService.EF_CARD_SECURITY))
                val securityInfoCollection = cardSecurityFile.securityInfos
                for (securityInfo in securityInfoCollection) {
                    if (securityInfo is PACEInfo) {
                        service.doPACE(
                            bacKey,
                            securityInfo.objectIdentifier,
                            PACEInfo.toParameterSpec(securityInfo.parameterId),
                            null
                        )
                        paceSucceeded = true
                    }
                }
            } catch (e: Exception) {
                Log.w("TAG", e)
            }
            service.sendSelectApplet(paceSucceeded)
            if (!paceSucceeded) {
                try {
                    service.getInputStream(PassportService.EF_COM).read()
                } catch (e: Exception) {
                    service.doBAC(bacKey)
                }
            }

            // -- Personal Details -- //
            val dg1In = service.getInputStream(PassportService.EF_DG1)
            val dg1File = DG1File(dg1In)
            val mrzInfo = dg1File.mrzInfo

            val personalName = mrzInfo.secondaryIdentifier.replace("<", " ").trim { it <= ' ' }
            val personalSurname = mrzInfo.primaryIdentifier.replace("<", " ").trim { it <= ' ' }
            val personalNumber = mrzInfo.personalNumber
            val personalGender = mrzInfo.gender.toString()
            val personalDateOfBirth = mrzInfo.dateOfBirth
            val personalDateOfExpiry = mrzInfo.dateOfExpiry
            val personalSerialNumber = mrzInfo.documentNumber
            val personalNationality = mrzInfo.nationality
            val personalIssuerAuthority = mrzInfo.issuingState

            if ("I" == mrzInfo.documentCode) {
                docType = DocType.ID_CARD
            } else if ("P" == mrzInfo.documentCode) {
                docType = DocType.PASSPORT
            }

            // -- Face Image -- //
            val dg2In = service.getInputStream(PassportService.EF_DG2)
            val dg2File = DG2File(dg2In)
            val faceInfos = dg2File.faceInfos
            val allFaceImageInfos: MutableList<FaceImageInfo> = ArrayList()
            for (faceInfo in faceInfos) {
                allFaceImageInfos.addAll(faceInfo.faceImageInfos)
            }
            if (allFaceImageInfos.isNotEmpty()) {
                val faceImageInfo = allFaceImageInfos.iterator().next()
                val image = ImageUtil.getImage(context, faceImageInfo)

                personalFaceImage = image.getBitmapImage()
                personalFaceImageBase64 = image.getBase64Image()
            }

            // -- Fingerprint (if exist)-- //
            try {
                val dg3In = service.getInputStream(PassportService.EF_DG3)
                val dg3File = DG3File(dg3In)
                val fingerInfos = dg3File.fingerInfos
                val allFingerImageInfos: MutableList<FingerImageInfo> = ArrayList()
                for (fingerInfo in fingerInfos) {
                    allFingerImageInfos.addAll(fingerInfo.fingerImageInfos)
                }
                val fingerprintsImage: MutableList<Bitmap?> = ArrayList()
                if (allFingerImageInfos.isNotEmpty()) {
                    for (fingerImageInfo in allFingerImageInfos) {
                        val image = ImageUtil.getImage(context, fingerImageInfo)
                        fingerprintsImage.add(image.getBitmapImage())
                    }
                    fingerprints = fingerprintsImage
                }
            } catch (e: Exception) {
                Log.w("TAG", e)
            }

            // -- Portrait Picture -- //
            try {
                val dg5In = service.getInputStream(PassportService.EF_DG5)
                val dg5File = DG5File(dg5In)
                val displayedImageInfos = dg5File.images
                if (displayedImageInfos.isNotEmpty()) {
                    val displayedImageInfo = displayedImageInfos.iterator().next()
                    val image = ImageUtil.getImage(context, displayedImageInfo)

                    portraitImage = image.getBitmapImage()
                    portraitImageBase64 = image.getBase64Image()
                }
            } catch (e: Exception) {
                Log.w("TAG", e)
            }

            // -- Signature (if exist) -- //
            try {
                val dg7In = service.getInputStream(PassportService.EF_DG7)
                val dg7File = DG7File(dg7In)
                val signatureImageInfos = dg7File.images
                if (signatureImageInfos.isNotEmpty()) {
                    val displayedImageInfo = signatureImageInfos.iterator().next()
                    val image = ImageUtil.getImage(context, displayedImageInfo)

                    signatureImage = image.getBitmapImage()
                    signatureImageBase64 = image.getBase64Image()
                }
            } catch (e: Exception) {
                Log.w("TAG", e)
            }

            // -- Additional Details (if exist) -- //
            try {
                val dg11In = service.getInputStream(PassportService.EF_DG11)
                val dg11File = DG11File(dg11In)
                if (dg11File.length > 0) {

                    val custodyInformation = dg11File.custodyInformation
                    val nameOfHolder = dg11File.nameOfHolder
                    val fullDateOfBirth = dg11File.fullDateOfBirth
                    val otherNames = dg11File.otherNames
                    val otherValidTDNumbers = dg11File.otherValidTDNumbers
                    val permanentAddress = dg11File.permanentAddress
                    val additionalPersonDetailsPersonalNumber = dg11File.personalNumber
                    val personalSummary = dg11File.personalSummary
                    val placeOfBirth = dg11File.placeOfBirth
                    val profession = dg11File.profession
                    val proofOfCitizenship = dg11File.proofOfCitizenship
                    val tag = dg11File.tag
                    val tagPresenceList = dg11File.tagPresenceList
                    val telephone = dg11File.telephone
                    val title = dg11File.title

                    additionalPersonDetails = AdditionalPersonDetails(
                        custodyInformation,
                        nameOfHolder,
                        fullDateOfBirth,
                        otherNames,
                        otherValidTDNumbers,
                        permanentAddress,
                        additionalPersonDetailsPersonalNumber,
                        personalSummary,
                        placeOfBirth,
                        profession,
                        listOf(proofOfCitizenship),
                        tag,
                        tagPresenceList,
                        telephone,
                        title
                    )
                }
            } catch (e: Exception) {
                Log.w("TAG", e)
            }

            // -- Document Public Key -- //
            try {
                val dg15In = service.getInputStream(PassportService.EF_DG15)
                val dg15File = DG15File(dg15In)
                docPublicKey = dg15File.publicKey
            } catch (e: Exception) {
                Log.w("TAG", e)
            }

            personDetails = PersonDetails(
                personalName,
                personalSurname,
                personalNumber,
                personalGender,
                personalDateOfBirth,
                personalDateOfExpiry,
                personalSerialNumber,
                personalNationality,
                personalIssuerAuthority,
            )
        } catch (e: Exception) {
            Log.w("TAG", e)
        }
        EDocument(docType, personDetails, additionalPersonDetails, docPublicKey)
    }
}