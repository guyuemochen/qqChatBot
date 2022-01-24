package mirai.guyuemochen.chatbot.classes.commands

import mirai.guyuemochen.chatbot.classes.BaseCommand
import mirai.guyuemochen.chatbot.data.BotInfo
import net.mamoe.mirai.message.data.buildMessageChain

class Owner: BaseCommand() {

    override val minLength: Int = 1
    override val maxLength: Int = 1
    override val cmd: String = "owner"
    override val description: String = "返回qq主人"

    override fun runCommand(msgList: List<String>, botInfo: BotInfo): String{

        if (checkCommandSize(msgList.size)){

            return if (botInfo.bot.getFriend(botInfo.owner) != null){
                "当前bot主人为：${botInfo.bot.getFriend(botInfo.owner)!!.nick}(${botInfo.owner})"
            } else{
                "当前bot没有主人呢"
            }

        }

        return errorCommand

    }

    override fun help(msgList: List<String>): String {

        if (msgList.size > 2){
            return commandNotExist
        }

        return description

    }

}