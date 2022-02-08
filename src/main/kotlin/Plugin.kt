package mirai.guyuemochen.chatbot

import mirai.guyuemochen.chatbot.classes.ChatMessage.at
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.name
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.event.GlobalEventChannel

// 导入本地class
import mirai.guyuemochen.chatbot.classes.Constants
import mirai.guyuemochen.chatbot.classes.Messages
import mirai.guyuemochen.chatbot.data.BotInfo
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.*

import net.mamoe.mirai.utils.info
import java.lang.IllegalArgumentException

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
        // 机器人下线事件
        eventChannel.subscribeAlways<BotOfflineEvent> {
            val botInfo = getBotInfoById(this.bot.id, botInfoList)
            if (botInfo != null){
                botInfo.saveData()
                // 将数据移除
                botInfoList.remove(botInfo)
            }
        }
        // 群组消息事件
        eventChannel.subscribeAlways<GroupMessageEvent> { // 群组消息
            val botInfoIndex = getBotInfoIndexById(this.bot.id, botInfoList)
            val botInfo = botInfoList[botInfoIndex]
            val isOwnerOrAdmin = ((this.sender.id == botInfo.owner) || (this.sender.permission != MemberPermission.MEMBER))

            var message = this.message.serializeToMiraiCode()
            if (message.startsWith(at(this.bot.id))){
                message = message.replaceFirst(at(this.bot.id), "")
            }
            else if(message.contains(at(this.bot.id))){
                return@subscribeAlways
            }

            if (message.startsWith(".") || message.startsWith("。")){
                val msgList = message.split(" ")
                if (testIsCommand(msgList[0])){
                    val send = Messages.Command.receiveCommand(msgList, botInfo, isOwnerOrAdmin, this.group)
                    if (send != null){
                        group.sendMessage(send)
                    }
                }
            }

            // 更新当前群组task，若为定时发送则无需更新
            botInfo.refreshTask(this.group)
        }

        // 好友消息事件
        eventChannel.subscribeAlways<FriendMessageEvent> {
            // 获取当前bot全部信息
            val botInfoIndex = getBotInfoIndexById(this.bot.id, botInfoList)
            val botInfo = botInfoList[botInfoIndex]
            val isOwner = (botInfo.owner == this.friend.id)

            val message = this.message.serializeToMiraiCode()
            if (message.startsWith(".") || message.startsWith("。")){
                val msgList = message.split(" ").toMutableList()

                val send = Messages.Command.receiveCommand(msgList, botInfo, isOwner, this.friend)
                logger.info{ send.toString() }
                if (send != null){
                    try{
                        friend.sendMessage(send)
                    }
                    catch (e: IllegalArgumentException){
                        logger.info{ "出现错误" }
                    }
                }
            }
        }

        // 机器人加群事件
        eventChannel.subscribeAlways<BotJoinGroupEvent> {

            val botInfoIndex = getBotInfoIndexById(this.bot.id, botInfoList)
            val botInfo = botInfoList[botInfoIndex]

            // 将新群导入至库中
            botInfo.addNewGroup(this.group)

            // 向机器人主人发送消息
            bot.getFriend(botInfo.owner)?.sendMessage("已加入新群${this.group.name}(${this.group.id})")

        }
        // 机器人退群事件
        eventChannel.subscribeAlways<BotLeaveEvent> {
            val botInfoIndex = getBotInfoIndexById(this.bot.id, botInfoList)
            val botInfo = botInfoList[botInfoIndex]

            botInfo.removeGroup(this.group)
            bot.getFriend(botInfo.owner)?.sendMessage("已离开群${this.group.name}(${this.group.id})")
        }

        eventChannel.subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            this.accept()
        }
    }

    // 插件卸载时
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

    private fun testIsCommand(cmd: String): Boolean{
        val num = countChar(cmd, '.') + countChar(cmd, '。')
        if (num == cmd.length){
            return false
        }
        return true
    }

    private fun countChar(cmd: String, target: Char): Int{
        var count = 0
        for (char in cmd){
            if (char == target){
                count++
            }
        }

        return count
    }
}