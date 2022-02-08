package mirai.guyuemochen.chatbot.classes

import mirai.guyuemochen.chatbot.classes.commands.*
import mirai.guyuemochen.chatbot.data.BotInfo
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.buildMessageChain

/**
 * 所有发送message的归类
 */
object Messages {

    object RandomPic{

        /**
         * 发送单张图片
         *
         * @param image 需要发送的图片，须提前获取图片
         * @return 返回mirai可读的message chain
         *
         * @see buildMessageChain
         */
        fun sendPictureOnly(image: Image): net.mamoe.mirai.message.data.MessageChain {
            val chain = buildMessageChain {
                +image
            }
            return chain
        }

    }

    object Command{

        private val commandClasses = listOf(
            Help(),
            Owner(),
            Random(),
            BotStatus(),
        )

        fun receiveCommand(msgList: List<String>, botInfo: BotInfo, isOwnerOrAdmin: Boolean, friendOrGroup: Contact): String {
            for (command in commandClasses){
                if (msgList[0] == ("." + command.description?.cmd) || msgList[0] == ("。" + command.description?.cmd)){
                    return command.runCommand(msgList, botInfo, isOwnerOrAdmin, friendOrGroup)
                }
            }

            return "输入错误，请重新输入或查看help指令"
        }

    }



}