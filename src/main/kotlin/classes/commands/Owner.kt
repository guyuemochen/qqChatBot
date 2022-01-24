package mirai.guyuemochen.chatbot.classes.commands

import mirai.guyuemochen.chatbot.classes.BaseCommand
import mirai.guyuemochen.chatbot.data.BotInfo
import mirai.guyuemochen.chatbot.data.CmdDescription
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.buildMessageChain

class Owner: BaseCommand() {

    override val minLength: Int = 1
    override val maxLength: Int = 1
    override val cmd: String = "owner"
    override val description = CmdDescription(
        cmd="owner",
        description=".owner 返回机器人主人信息"
    )

    override fun runCommand(msgList: List<String>, botInfo: BotInfo, isOwnerOrAdmin: Boolean, friendOrGroup: Contact): String{

        if (checkCommandSize(msgList.size)){

            return if (botInfo.bot.getFriend(botInfo.owner) != null){
                "当前bot主人为：${botInfo.bot.getFriend(botInfo.owner)!!.nick}(${botInfo.owner})"
            } else{
                "当前bot没有主人呢"
            }

        }

        return errorCommand

    }

}