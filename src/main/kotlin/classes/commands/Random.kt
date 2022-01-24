package mirai.guyuemochen.chatbot.classes.commands

import mirai.guyuemochen.chatbot.classes.BaseCommand
import mirai.guyuemochen.chatbot.data.BotInfo

class Random: BaseCommand() {

    override val cmd = "random"
    override val minLength: Int = 3
    override val maxLength: Int = 3

    override fun runCommand(msgList: List<String>, botInfo: BotInfo): String {
        if (checkCommandSize(msgList.size)){

        }
        return errorCommand
    }
}