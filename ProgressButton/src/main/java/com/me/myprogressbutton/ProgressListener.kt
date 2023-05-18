package com.me.myprogressbutton

interface ProgressListener {
    fun normal(status: Int)
    fun startDownLoad()
    fun download(progress: Int)
}