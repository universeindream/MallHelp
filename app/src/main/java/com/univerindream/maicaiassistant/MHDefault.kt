package com.univerindream.maicaiassistant

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.Utils
import com.elvishew.xlog.XLog

object MHDefault {

    val githubSolutions = arrayListOf<MCSolution>()

    val defaultMCSolutions: List<MCSolution> by lazy {
        val res = arrayListOf<MCSolution>()
        res.add(MCSolution("自定义", arrayListOf()))

        try {
            val solutions = Utils.getApp().assets.list("solutions")
            solutions?.forEach {
                val json = ResourceUtils.readAssets2String("solutions/$it")
                res.add(GsonUtils.fromJson(json, MCSolution::class.java))
            }
        } catch (e: Exception) {
            XLog.e(e)
        }

        res
    }

}