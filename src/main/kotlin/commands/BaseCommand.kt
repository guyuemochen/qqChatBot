package mirai.guyuemochen.chatbot.commands

import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain

open class BaseCommand {

    open val minLength: Int = -1
    open val maxLength: Int = -1
    open val cmd: String = ""
    open val description: String = "this is a base description"
    open val errorCommand = buildMessageChain {
        + "输入错误，请重新输入或查看help指令"
    }
    open val helpMessage = ""
    open val commandNotExist = "当前指令不存在"

    open val commandClasses: List<BaseCommand> = listOf(
        Help(),
    )

    open fun runCommand(msgList: List<String>): MessageChain? {

        if (msgList.size < minLength && minLength != -1){
            return errorCommand
        }
        else if(msgList.size > maxLength && maxLength != -1){
            return errorCommand
        }

        return null
    }

    open fun help(msgList: List<String>): MessageChain{
        return buildMessageChain {
            "this is a base command helper"
        }
    }

    /**
     * 检查command长度是否正确
     *
     * @parama commandSize 当前command的大小
     * @return 返回
     */
    open fun checkCommandSize(commandSize: Int): Boolean {

        if (commandSize < minLength && minLength != -1){
            return false
        }
        else if(commandSize > maxLength && maxLength != -1){
            return false
        }
        return true

    }

}