package mirai.guyuemochen.chatbot.classes.commands

import mirai.guyuemochen.chatbot.classes.BaseCommand
import mirai.guyuemochen.chatbot.data.BotInfo
import mirai.guyuemochen.chatbot.data.CmdDescription
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.buildMessageChain
import kotlin.math.min

/**
 * .help指令
 *
 * @see BaseCommand
 */
class Help: BaseCommand() {

    override val minLength: Int = 1
    override val cmd: String = "help"
    override val description = CmdDescription(
        cmd="help",
        description=".help 查询指令用法"
    )

    /**
     * 运行help类型语句
     *
     * @param msgList 拆分成List的指令
     * @return 返回反馈
     */
    override fun runCommand(msgList: List<String>, botInfo: BotInfo, isOwnerOrAdmin: Boolean, friendOrGroup: Contact): String  {
        val commandClasses: List<BaseCommand> = listOf(
            Help(),
            Owner(),
            Random(),
        )

        if (checkCommandSize(msgList.size)){

            if (msgList.size == 1){
                return listAllCommands(1)
            }
            else if (msgList.size > 1){
                for (command in commandClasses){
                    if (msgList[1] == command.cmd){
                        return command.help(msgList)
                    }
                }
                try{
                    if (msgList.size == 2){
                        buildMessageChain { listAllCommands(msgList[1].toInt()) }
                    }
                    else{
                        return errorCommand
                    }
                }
                catch (e: NumberFormatException){
                    return errorCommand
                }

            }

            return errorCommand
        }

        return errorCommand
    }

    /**
     * 列出所有在该页的命令
     *
     * @param page 需要获取的页码
     * @return 返回当前页码所有代码的信息
     */
    private fun listAllCommands(page: Int): String{

        val commandClasses: List<BaseCommand> = listOf(
            Help(),
            Owner(),
        )
        val pageSize: Int = commandClasses.size / 5 + 1

        var message = ""
        return if (pageSize < page){
            commandNotExist
        } else{
            for (index in (page - 1) * 5 until min(commandClasses.size, page * 5)){
                val command: BaseCommand = commandClasses[index]
                message = "${message}${command.cmd} ${command.description}\n"
            }

            message += "page $page of $pageSize"
            message
        }

    }

}