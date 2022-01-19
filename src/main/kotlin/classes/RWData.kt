package mirai.guyuemochen.chatbot.classes

import java.io.*
import java.nio.file.Path

object RWData {

    fun writeTxtFile(content: String, filePath: Path, fileName: String, append: Boolean): Boolean {
        val flag = true
        val thisFile = File("$filePath/$fileName")
        if (!thisFile.parentFile.exists()) {
            thisFile.parentFile.mkdirs()
        }
        val fw = FileWriter("$filePath/$fileName", append)
        fw.write(content)
        fw.close()
        return flag
    }

    @Throws(Exception::class)
    fun readTxtFile(filePath: Path, fileName: String): String {

        var content = ""
        try{
            val file = File("$filePath/$fileName")
            val inStream: InputStream = FileInputStream(file)

            var line: String?
            val buffReader = BufferedReader(
                InputStreamReader(
                    FileInputStream(file), "UTF-8"
                )
            )
            //分行读取
            while (buffReader.readLine().also { line = it } != null) content += "$line"
            inStream.close()
        }
        catch(e: Exception){
            throw NoSuchFileException(File("$filePath/$fileName"))
        }

        return content
    }

    fun getFiles(path: String): MutableList<String> {
        val fileNames: MutableList<String> = mutableListOf()
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File(path).walk()
        fileTree.maxDepth(1) //需遍历的目录层次为1，即无须检查子目录
            .filter { it.isFile } //只挑选文件，不处理文件夹
            .filter { it.extension in listOf("jpg", "png") }//选择扩展名为txt或者mp4的文件
            .forEach { fileNames.add(it.name) }//循环 处理符合条件的文件
        return fileNames
    }

}