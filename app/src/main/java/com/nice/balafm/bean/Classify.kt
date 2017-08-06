package com.nice.balafm.bean

data class Classify(val classifyID: Int = 0, val classify: String = "", val children: List<Classify> = listOfNotNull())