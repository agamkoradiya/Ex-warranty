package com.example.expiredcalculator.model

data class Model(
    var itemId : String = "",
    var productName : String = "",
    var productIconUrl : String = "",
    var expDate : Int = 0,
    var expMonth : Int = 0,
    var expYear : Int = 0,
    var purchasedDate : Int = 0,
    var purchasedMonth : Int = 0,
    var purchasedYear : Int = 0
)