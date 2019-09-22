package com.nil_projects.society_app


class UserClass(val UserID : String,val Profile_Pic_url : String,val UserName : String,val email : String,
                val city: String,val societyname : String,val Wing : String,
                val FlatNo : String,val UserRelation : String,val userAuth : String,val MobileNumber : String,
                val registrationTokens : MutableList<String>)
{
    constructor() : this("","","","","","","","",
            "","","", mutableListOf())
}

enum class NatureItem(val title : String,open val userDrawable: Int, open val natureDrawable: Int) {
    NATURE0("Cooks",R.drawable.cooklogo, R.drawable.nature_000),
    NATURE1("Drivers",R.drawable.user_001, R.drawable.nature_001),
    NATURE2("Security Guards",R.drawable.user_019, R.drawable.nature_002),
  NATURE3("Electrician",R.drawable.electrician, R.drawable.elect),
  NATURE4("Plumber",R.drawable.plumber, R.drawable.plumb),
//  NATURE5("Cooks",R.drawable.user_005, R.drawable.nature_005),
//  NATURE6("Cooks",R.drawable.user_006, R.drawable.nature_006),
//  NATURE7("Cooks",R.drawable.user_007, R.drawable.nature_007),
//  NATURE8("Cooks",R.drawable.user_008, R.drawable.nature_008),
//  NATURE9("Cooks",R.drawable.user_009, R.drawable.nature_009),
//  NATURE10("Cooks",R.drawable.user_010, R.drawable.nature_010),
//  NATURE11("Cooks",R.drawable.user_011, R.drawable.nature_011),
//  NATURE12("Cooks",R.drawable.user_012, R.drawable.nature_012),
//  NATURE13("Cooks",R.drawable.user_013, R.drawable.nature_013),
//  NATURE14("Cooks",R.drawable.user_014, R.drawable.nature_014),
//  NATURE15("Cooks",R.drawable.user_015, R.drawable.nature_015),
//  NATURE16("Cooks",R.drawable.user_016, R.drawable.nature_016),
//  NATURE17("Cooks",R.drawable.user_017, R.drawable.nature_017),
//  NATURE18("Cooks",R.drawable.user_018, R.drawable.nature_018),
//  NATURE19("Cooks",R.drawable.user_019, R.drawable.nature_019),
//  NATURE20("Cooks",R.drawable.user_020, R.drawable.nature_020),
}

class AddWorkerClass(val id : String,val name : String,val imageUrl : String,val address : String,
                     val type : String,val mobile : String,val dateofjoining : String,val speciality : String)
{
    constructor() : this("","","","","",",","","")
}

class ComplaintClass(val CompUserID : String,val CompFlatNum: String,val CompheadLine : String,val ComplaintImg : String,
                     val CompUpdatedDate : String,val CompProcess : String,val CompUserMobileNo : String,
                     val CompWingName : String)
{
    constructor() : this("","","","","","","","")
}

class SliderImgClass(val Img1 : String,val Img2: String,val Img3 : String,val Img4 : String,val Img5 : String)
{
    constructor() : this("","","","","")
}

class months(val MonthsPaid0: String,val MonthsPaid1 : String,val MonthsPaid2 : String,val MonthsPaid3: String,val MonthsPaid4: String,
             val MonthsPaid5: String,val MonthsPaid6: String,val MonthsPaid7: String,val MonthsPaid8: String,
             val MonthsPaid9: String,val MonthsPaid10: String,val MonthsPaid11: String,
             val ReceiptNumber : String,val Amount : String,val Fine : String)
{
    constructor() : this("","","","","","",
            "","","","","","","",
            "","")
}

class reportModelClass(val currentTime: String,val buildingnotice : String, val id : String,val imageUrl : String,val wing : String,
                       val userid : String)
{
    constructor() : this("","","","","","")
}

class AddNotifiClass(val id : String,val noti : String,val imageUrl : String,val currentTime : String,val counter : String,
                     val userid : String)
{
    constructor() : this("","","","","","")
}


class FetchWorkerClass(val id : String,val name : String,val imageUrl : String,val address : String,
                       val type : String,val mobile : String,val dateofjoining : String,val speciality : String)
{
    constructor() : this("","","","","",",","","")
}