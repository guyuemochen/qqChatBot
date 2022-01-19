package mirai.guyuemochen.chatbot.data

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        }
        else{
            readJson(botPath)
        }
    }

    private fun readJson(botPath: Path){
        val botInfo = BotJson().getInfo(botPath)
        this.botId = botInfo.id
        this.owner = botInfo.owner
        for (groupInfo in botInfo.groups){
            try{
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
                continue
            }
        }
    }

    private fun newTask(delayTime: Long, group: Group, type: Int): Job? {
        // 检测设定类型
        if (type == Constants.randomType.answerAfterDelay){
            return GlobalScope.launch{
                delay(delayTime)
                val pictures = RWData.getFiles("$pluginPath/image/randoms")
                // 根据指定路径寻找随机图片
                val image = group.uploadImage(
                    FileInputStream(
                        "$pluginPath/image/randoms/" + pictures.shuffled()[0]
                    )
                )
                group.sendMessage(RandomPic.randomPicture(image))
            }
        }

        return null
    }

    class GroupInfo(
        val id: Long,
        var task: Job?,
        var randomDelay: Long,
        var randomType: Int,
    )
}