package mirai.guyuemochen.chatbot.classes

import mirai.guyuemochen.chatbot.commands.BaseCommand
import mirai.guyuemochen.chatbot.commands.Help
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChain
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

        private val commandClasses = BaseCommand().commandClasses

        fun recieveCommand(msgList: List<String>): MessageChain? {
            for (command in commandClasses){
                if (msgList[0] == ("." + command.cmd) || msgList[0] == ("。" + command.cmd)){
                    return command.runCommand(msgList)
                }
            }

            return null
        }

    }



}