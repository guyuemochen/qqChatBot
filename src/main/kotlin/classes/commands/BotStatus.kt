package mirai.guyuemochen.chatbot.classes.commands

import mirai.guyuemochen.chatbot.classes.BaseCommand
import mirai.guyuemochen.chatbot.data.BotInfo
import mirai.guyuemochen.chatbot.data.CmdDescription
import net.mamoe.mirai.contact.Contact

class BotStatus: BaseCommand() {

    override val minLength: Int = 2
    override val maxLength: Int = 2
    override val description = CmdDescription(
        cmd="bot",
        description="开启或关闭此机器人",
        detailedDescription="输入.bot on以开启机器人，或输入.bot off以关闭机器人",
    )

    override fun runCommand(
        msgList: List<String>,
        botInfo: BotInfo,
        isOwnerOrAdmin: Boolean,
        friendOrGroup: Contact
    ): String {
        if (checkCommandSize(msgList.size)){

            return when(botInfo.changeBotStatus(friendOrGroup.id, msgList[1])){
                1 ->
                    "来啦来啦"
                2 ->
                    "睡觉去了"
                -1 ->
                    "是不是走错地方了"
                -2 ->
                    "别来烦我！"
                -3 ->
                    "你在说什么我听不懂"
                else ->
                    errorCommand
            }
        }

        return errorCommand
    }
}