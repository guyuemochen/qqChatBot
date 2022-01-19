package mirai.guyuemochen.chatbot.data

import kotlinx.coroutines.*

import mirai.guyuemochen.chatbot.classes.RWData
import mirai.guyuemochen.chatbot.messages.RandomPic
import mirai.guyuemochen.chatbot.classes.Constants

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group

import java.io.FileInputStream
import java.nio.file.Path

import kotlin.io.path.exists

class BotInfo(
    botPath: Path,
    private val pluginPath: Path,
    private val bot: Bot
) {
    private var botId: Long = 0
    private var owner: Long = 0
    private val groupInfoList: MutableList<GroupInfo> = mutableListOf()

    // 初始化：载入数据
    init {
        if(!botPath.exists()){
            // 若不存在则新建文件
        }
        else{
            // 若存在则从现有文件中读取
            readJson(botPath)
        }
    }

    /**
     * 获取当前json文件并转化为class形式
     *
     * @param botPath 当前bot所在文件地址
     */
    private fun readJson(botPath: Path){
        val botInfo = BotJson().getInfo(botPath)
        this.botId = botInfo.id
        this.owner = botInfo.owner
        for (groupInfo in botInfo.groups){
            try{
                // 获取group
                val group = bot.getGroupOrFail(groupInfo.groupId)
                groupInfoList.add(
                    GroupInfo(
                        groupInfo.groupId,
                        newTask(groupInfo.randomDelay, group, groupInfo.randomType),
                        groupInfo.randomDelay,
                        groupInfo.randomType,
                    )
                )
            }
            catch (e: NoSuchElementException){
                // 当出现群组不存在时跳过
                continue
            }
        }
    }

    /**
     * 创建新的task
     *
     * @param delayTime 延迟时间
     * @param group 当前bot所在组
     * @param type 防冷群类型
     *
     * @return 设定好的task
     */
    private fun newTask(delayTime: Long, group: Group, type: Int): Job? {
        // 检测设定类型
        if (type == Constants.RandomType.answerAfterDelay){
            return GlobalScope.launch{
                delay(delayTime)
                val pictures = RWData.getFiles("$pluginPath/image/randoms")
                // 根据指定路径寻找随机图片
                val image = group.uploadImage(
                    FileInputStream(
                        "$pluginPath/image/randoms/" + pictures.shuffled()[0]
                    )
                )
                group.sendMessage(RandomPic.sendPictureOnly(image))
            }
        }

        return null
    }

    /**
     * GroupInfo储存群组信息
     *
     * @param id 群号
     * @param task 定时发阿宋消息的任务
     * @param randomDelay 防冷群延迟
     * @param randomType 防冷群类型
     */
    class GroupInfo(
        val id: Long,
        var task: Job?,
        var randomDelay: Long,
        var randomType: Int,
    )
}