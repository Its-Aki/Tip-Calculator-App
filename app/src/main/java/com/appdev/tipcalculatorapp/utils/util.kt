package com.appdev.tipcalculatorapp.utils

fun calculateTotalTip(totalBill:Double, sliderPercentage: Int): Double {
    return if(totalBill>1 &&totalBill.toString().isNotEmpty()) (totalBill*sliderPercentage)/100 else 0.0
}

fun calculateTotalPerPerson(
    totalBill:Double,
    splitBy:Int,
    sliderPercentage: Int
):Double{
    val bill= calculateTotalTip(totalBill=totalBill,sliderPercentage=sliderPercentage)+totalBill
    return (bill/splitBy)
}