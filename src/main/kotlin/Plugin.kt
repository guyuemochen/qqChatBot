package mirai.guyuemochen.chatbot

import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.name
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.utils.info

// 导入本地class
import mirai.guyuemochen.chatbot.classes.Constants
import mirai.guyuemochen.chatbot.data.BotInfo
import net.mamoe.mirai.event.events.BotOfflineEvent

import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.GroupMessageEvent

/**
 * plugin的main文件
 *
 * @author 古月漠尘
 */
object Plugin : KotlinPlugin(
    JvmPluginDescription(
        id = "mirai.guyuemochen.chatbot.plugin",
        name = "聊天工具",
        version = "0.2.0",
    ) {
        author("古月漠尘")
    }
) {
    private var botInfoList: MutableList<BotInfo> = mutableListOf() // 所有bot所在的info

    // 插件启动时
    override fun onEnable() {
        // 后台输出所有信息
        logger.info{ "Plugin:${this.name}, id:${this.id}, version:${this.version} enabled." }
        logger.info{ Constants.loadMessage }

        // 导入事件库
        val eventChannel = GlobalEventChannel.parentScope(this)

        // 机器人登录事件
        eventChannel.subscribeAlways<BotOnlineEvent> {
            // 获取数据
            botInfoList.add(BotInfo(this.bot, dataFolderPath))
        }

        eventChannel.subscribeAlways<BotOfflineEvent> {
            val botInfo = getBotInfoById(this.bot.id, botInfoList)
            if (botInfo != null){
                botInfo.saveData()
                // 将数据移除
                botInfoList.remove(botInfo)
            }
        }

        eventChannel.subscribeAlways<GroupMessageEvent> {
            val botInfoIndex = getBotInfoIndexById(this.bot.id, botInfoList)
            // 更新当前群组task，若为定时发送则无需更新
            botInfoList[botInfoIndex].refreshTask(this.group)
        }
    }

    /**
     * 插件卸载时
     */
    override fun onDisable() {
        super.onDisable()
        logger.info{ "Plugin:${this.name}, id:${this.id}, version:${this.version} disabled." }
        logger.info{ Constants.disableMessage }

        for (bot in botInfoList){
            bot.saveData()
        }
    }

    /**
     * 根据bot的qq号获取信息,运用二叉树寻找法，将复杂度变为log(n)
     *
     * @param botId 寻找的bot的qq号
     * @param botInfoList 所有bot的信息
     *
     * @return 返回对应的bot，若不存在则返回null
     */
    private fun getBotInfoById(botId : Long, botInfoList: MutableList<BotInfo>): BotInfo?{

        if (botInfoList.size == 1){
            if(botInfoList[0].botId == botId){
                return botInfoList[0]
            }
        }
        else if (botInfoList.size > 1){
            val left = getBotInfoById(botId, botInfoList.subList(0, botInfoList.size/2))
            if (left != null){
                return left
            }

            val right = getBotInfoById(botId, botInfoList.subList(botInfoList.size/2, botInfoList.size))
            if(right != null){
                return right
            }
        }
        return null
    }

    /**
     * 根据bot的qq号获取信息,运用二叉树寻找法，将复杂度变为log(n)
     *
     * @param botId 寻找的bot的qq号
     * @param botInfoList 所有bot的信息
     *
     * @return 返回对应的bot，若不存在则返回null
     */
    private fun getBotInfoIndexById(botId : Long, botInfoList: MutableList<BotInfo>): Int{
        if (botInfoList.size == 1){
            if(botInfoList[0].botId == botId){
                return 0
            }
        }
        else if (botInfoList.size > 1){
            val leftIndex = getBotInfoIndexById(botId, botInfoList.subList(0, botInfoList.size/2))
            if (leftIndex != -1){
                return leftIndex
            }

            val rightIndex = getBotInfoIndexById(botId, botInfoList.subList(botInfoList.size/2, botInfoList.size))
            if(rightIndex != -1){
                return rightIndex + botInfoList.size/2
            }
        }
        return -1
    }
}