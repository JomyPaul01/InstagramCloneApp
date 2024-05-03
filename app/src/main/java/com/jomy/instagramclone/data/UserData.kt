package com.jomy.instagramclone.data

import com.jomy.instagramclone.viewmodel.Constants



data class UserData(
    var userId : String? = null,
    var userName:String? = null,
    var name:String? = null,
    var imageUrl:String? = null,
    var bio:String? = null,
    var following:List<String>? = null
)
{
    fun toMap(): Map<String, Any?> {
        val mapOf = mapOf(
            Constants.USERID to userId,
            Constants.NAME to name,
            Constants.USERNAME to userName,
            Constants.IMAGEURL to imageUrl,
            Constants.BIO to bio,
            Constants.FOLLOWING to following
        )
        return mapOf
    }
}