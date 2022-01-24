package mirai.guyuemochen.chatbot.classes

import mirai.guyuemochen.chatbot.data.BotInfo
import mirai.guyuemochen.chatbot.data.CmdDescription

open class BaseCommand {

    open val minLength: Int = -1
    open val maxLength: Int = -1
    open val cmd: String = ""
    open val description: CmdDescription? = null
    open val errorCommand = "输入错误，请重新输入或查看help指令"
    open val commandNotExist = "当前指令不存在"

    /**
     * 运行当前指令
     *
     * @param msgList 指令链
     * @param botInfo 当前bot信息
     *
     * @return 返回指令反馈
     */
    open fun runCommand(msgList: List<String>, botInfo: BotInfo): String {

        if (msgList.size < minLength && minLength != -1){
            return errorCommand
        }
        else if(msgList.size > maxLength && maxLength != -1){
            return errorCommand
        }

        return errorCommand
    }

    /**
     * 当查询该指令时的反馈
     *
     * @param msgList 指令链
     *
     * @return 反馈
     */
    open fun help(msgList: List<String>): String{
        if (description != null && description!!.getDepth() >= (msgList.size - 1)){
            return description!!.getDescription(msgList.size - 1, msgList)
        }
        return errorCommand
    }

    /**
     * 检查command长度是否正确
     *
     * @param commandSize 当前command的大小
     * @return 返回
     */
    open fun checkCommandSize(commandSize: Int): Boolean {

        if (commandSize >= minLength || minLength == -1){
            return true
        }
        else if(commandSize <= maxLength || maxLength == -1){
            return true
        }
        return false

    }

}