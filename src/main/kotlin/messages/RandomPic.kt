package mirai.guyuemochen.chatbot.messages

import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.buildMessageChain

object RandomPic {

    fun randomPicture(image: Image): net.mamoe.mirai.message.data.MessageChain {
        val chain = buildMessageChain {
            +image
        }

        return chain
    }

}