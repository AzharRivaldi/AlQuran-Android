package com.azhar.alquran.model.main

import com.google.gson.annotations.SerializedName

/**
 * Created by Azhar Rivaldi on 10-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class ModelAyat {
    @SerializedName("ar")
    lateinit var arab: String

    @SerializedName("id")
    lateinit var indo: String

    @SerializedName("nomor")
    lateinit var nomor: String
}