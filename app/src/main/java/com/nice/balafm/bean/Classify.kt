package com.nice.balafm.bean

data class Classify(val channelID:Int = 0, val classify: String = "", val children: List<Classify> = listOfNotNull())