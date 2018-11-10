package com.goach.simple.adbshellsimple

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.DataOutputStream
import java.io.IOException


/**
 * author:Goach
 * date:2018/11/9
 * des 查看应用包名 adb shell pm list packages
 * 卸载应用msa adb shell pm uninstall -k --user 0 com.miui.systemAdSolution
 */
class MainActivity : AppCompatActivity() {

    val COMMAND_SU = "su"//root权限是这个
    val COMMAND_SH = "sh"
    val COMMAND_EXIT = "exit\n"
    val COMMAND_LINE_END = "\n"
    private val cmd = "pm uninstall -k --user 0 com.dongxin.fts.bzh"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnUninstall.setOnClickListener {
            var result = shellCmd(cmd,true)
            if(TextUtils.isEmpty(result)){
                result = "清除失败"
            }
            tvResult.text = result
            Log.d("tag", "tag=======$result")
        }
    }
    private fun shellCmd(cmd:String,isNeedResultMsg:Boolean):String{
        var result = -1
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(COMMAND_SH)//root的是su
            os = DataOutputStream(process!!.outputStream)
            os.write(cmd.toByteArray())
            os.writeBytes(COMMAND_LINE_END)
            os.flush()
            os.writeBytes(COMMAND_EXIT)
            os.flush()
            result = process.waitFor()
            // get command result
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(InputStreamReader(process.inputStream))
                errorResult = BufferedReader(InputStreamReader(process.errorStream))
                var s = successResult.readLine()
                while (s != null) {
                    successMsg.append(s)
                    s = successResult.readLine()
                }
                s = errorResult.readLine()
                while (s != null) {
                    errorMsg.append(s)
                    s = errorResult.readLine()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
                successResult?.close()
                errorResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            process?.destroy()
        }
        val sb = StringBuilder("返回结果")
        sb.append("\n")
        sb.append(result)
        sb.append("\n")
        if(!TextUtils.isEmpty(successMsg)){
            sb.append("返回成功信息")
            sb.append("\n")
            sb.append(successMsg)
        }
        if(!TextUtils.isEmpty(errorMsg)){
            sb.append("\n")
            sb.append("返回失败信息")
            sb.append("\n")
            sb.append(errorMsg)
        }
        return sb.toString()
    }
}
