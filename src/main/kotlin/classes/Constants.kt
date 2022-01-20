package mirai.guyuemochen.chatbot.classes

/**
 * 所有常量
 *
 * @author 古月漠尘
 */
object Constants {

    const val loadMessage = "古月出品必属精品。" // 插件加载发送词
    const val disableMessage = "欢迎下次再见" // 插件卸载发送词

    object GroupDataDefault {

        const val randomDelay: Long = 3600000 // 一小时
        const val randomType = RandomType.answerAfterDelay // 默认为跟在最后
        const val welcomeText = "欢迎@入群" // 默认欢迎词

    }

    object RandomType {

        const val answerAfterDelay = 1 // 跟在最后
        const val repeatConstantDelay = 2 // 固定时间说话

    }

    object BotStatus {

        const val on = 1 // 机器人开启状态
        const val off = 2 // 机器人关闭状态

    }

}