package com.softwaremill.codebrag.dao

import org.bson.types.ObjectId

object ObjectIdTestUtils {

  def oid(number: Long) = new ObjectId(intSuffixToStringId(number))
  def intSuffixToStringId(number: Long) = {

    val fullPrefix = "507f191e810c19729de860e"
    val prefixLength = fullPrefix.length - number.toString.length
    fullPrefix.substring(0, prefixLength + 1) + number
  }

}
