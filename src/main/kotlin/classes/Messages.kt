package mirai.guyuemochen.chatbot.classes

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

}