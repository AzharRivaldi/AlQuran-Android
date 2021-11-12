package com.azhar.alquran.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Azhar Rivaldi on 10-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class ModelSurah : Serializable {
    @SerializedName("arti")
    lateinit var arti: String

    @SerializedName("asma")
    lateinit var asma: String

    @SerializedName("ayat")
    lateinit var ayat: String

    @SerializedName("nama")
    lateinit var nama: String

    @SerializedName("type")
    lateinit var type: String

    @SerializedName("audio")
    lateinit var audio: String

    @SerializedName("nomor")
    lateinit var nomor: String

    @SerializedName("keterangan")
    lateinit var keterangan: String
}