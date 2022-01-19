package mirai.guyuemochen.chatbot

import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.name
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.utils.info

import kotlin.io.path.Path

// 导入本地class
import mirai.guyuemochen.chatbot.classes.Constants
import mirai.guyuemochen.chatbot.data.BotInfo

import net.mamoe.mirai.event.events.BotOnlineEvent

object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "mirai.guyuemochen.chatbot.plugin",
        name = "聊天工具",
        version = "0.2.0",
    ) {
        author("古月漠尘")
    }
) {
    private var bots: MutableList<BotInfo> = mutableListOf()
    // 当bot启动时
    override fun onEnable() {
        logger.info{ "Plugin:${this.name}, id:${this.id}, version:${this.version} enabled." }
        logger.info{ Constants.loadMessage }

        // 导入事件库
        val eventChannel = GlobalEventChannel.parentScope(this)

        // 机器人登录事件
        eventChannel.subscribeAlways<BotOnlineEvent> {
            // 加载json文件
            // 获取储存该bot的文件夹
            val botPath = Path("${dataFolderPath}/${this.bot.id}")
            // 获取数据
            bots.add(BotInfo(botPath, dataFolderPath, this.bot))
        }
    }
}