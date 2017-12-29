package com.apps.elliotgrin.cryptobot.aiml

import Main.mainFunction
import android.os.Environment
import java.io.*
import android.content.Context
import org.alicebot.ab.*


/**
 * Created by elliotgrin on 29.12.2017.
 */
class Aiml(private val context: Context) {

    lateinit var bot: Bot
    lateinit var chat: Chat

    fun setupAiml() {
        //checking SD card availablility
        val a = isSdCardAvailable()

        //receiving the assets from the app directory
        val assets = context.getResources().getAssets()
        val jayDir = File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari")
        val b = jayDir.mkdirs()
        if (jayDir.exists()) {

            //Reading the file
            try {
                for (dir in assets.list("Hari")) {
                    val subdir = File(jayDir.getPath() + "/" + dir)
                    val subdir_check = subdir.mkdirs()
                    for (file in assets.list("Hari/" + dir)) {
                        val f = File(jayDir.getPath() + "/" + dir + "/" + file)
                        if (f.exists()) {
                            continue
                        }
                        var `in`: InputStream? = null
                        var out: OutputStream? = null
                        `in` = assets.open("Hari/$dir/$file")
                        out = FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file)

                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(`in`, out)
                        `in`!!.close()
                        out.flush()
                        out.close()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari"
        println("Working Directory = " + MagicStrings.root_path)
        AIMLProcessor.extension = PCAIMLProcessorExtension()

        //Assign the AIML files to bot for processing
        bot = Bot("Hari", MagicStrings.root_path, "chat")
        chat = Chat(bot)
        val args: Array<String>? = null
        mainFunction()
    }

    fun mainFunction(/*args: Array<String>*/) {
        MagicBooleans.trace_mode = false
        println("trace mode = " + MagicBooleans.trace_mode)
        Graphmaster.enableShortCuts = true
        val timer = Timer()
        val request = "Hello."
        val response = chat.multisentenceRespond(request)

        println("Human: " + request)
        println("Robot: " + response)
    }

    //check SD card availability
    fun isSdCardAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    //copying the file
    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer) != -1) {
            read = `in`.read(buffer)
            out.write(buffer, 0, read)
        }
    }

}