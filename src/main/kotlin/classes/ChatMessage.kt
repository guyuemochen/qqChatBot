package mirai.guyuemochen.chatbot.classes

object ChatMessage {

    val at: (Long) -> String = { botId: Long -> "[mirai:at:$botId]"}

}