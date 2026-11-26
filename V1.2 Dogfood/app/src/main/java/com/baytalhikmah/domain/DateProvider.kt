package com.baytalhikmah.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

interface DateProvider {
    fun today(): LocalDate
    fun nowMillis(): Long
    fun now(): LocalDateTime
}

object SystemDateProvider : DateProvider {
    override fun today(): LocalDate = LocalDate.now()

    override fun nowMillis(): Long = System.currentTimeMillis()

    override fun now(): LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
}
