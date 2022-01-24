package mirai.guyuemochen.chatbot.classes.commands

import mirai.guyuemochen.chatbot.classes.BaseCommand
import mirai.guyuemochen.chatbot.data.BotInfo
import mirai.guyuemochen.chatbot.data.CmdDescription
import mirai.guyuemochen.chatbot.data.Time
import net.mamoe.mirai.contact.Contact

class Random: BaseCommand() {

    override val cmd = "random"
    override val minLength: Int = 3
    override val maxLength: Int = 3
    override val description = CmdDescription(
        cmd="random",
        description="更改发图信息\n查看详细信息请输入分支：\n  .help random time\n  .help random type",
        nextArgs = listOf(
            CmdDescription(
                cmd="time",
                description="更改随机发图时间\n方式为：.help random time [时间]"
            )
        )
    )

    override fun runCommand(msgList: List<String>, botInfo: BotInfo, isOwnerOrAdmin: Boolean, friendOrGroup: Contact): String {
        if (checkCommandSize(msgList.size)){
            if (isOwnerOrAdmin && botInfo.containGroup(friendOrGroup.id)){
                if (msgList[2] == "time"){
                    botInfo.changeGroupRandomTime(friendOrGroup.id, Time.changeStringToTime(msgList[3]))
                }
            }
            else{
                return noPermission
            }
        }
        return errorCommand
    }
}