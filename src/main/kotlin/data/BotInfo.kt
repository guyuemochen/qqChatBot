package mirai.guyuemochen.chatbot.data

import kotlinx.coroutines.*

import mirai.guyuemochen.chatbot.classes.RWData
import mirai.guyuemochen.chatbot.classes.Constants
import mirai.guyuemochen.chatbot.classes.Messages

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group

import java.io.FileInputStream
import java.nio.file.Path
import kotlin.io.path.Path

import kotlin.io.path.exists

/** 储存bot的详细信息
 *
 * @author 古月莫辰
 *
 * @param botPath 储存bot信息的json文件路径
 * @param bot net.mamoe.mirai.Bot类型
 * @param pluginPath 插件信息
 */
class BotInfo(
    bot: Bot,
    private val pluginPath: Path,
) {
    private var botId: Long = 0
    private var owner: Long = 0
    private val groupInfoList: MutableList<GroupInfo> = mutableListOf()

    // 初始化：载入数据
    init {
        load(bot)
    }

    /**
     * 初始化函数，减少mem空间
     *
     * @param bot mirai机器人
     */
    private fun load(bot: Bot){
        val botPath = Path("${pluginPath}/${bot.id}")
        if(!botPath.exists()){
            // 若不存在则新建文件
        }
        else{
            // 若存在则从现有文件中读取
            readJson(botPath, bot)
        }
    }

    /**
     * 获取当前json文件并转化为class形式
     *
     * @param botPath 当前bot所在文件地址
     * @param bot 当前的bot
     */
    private fun readJson(botPath: Path, bot: Bot){
        val botInfo = BotJson().getInfo(botPath)
        // 保存bot的qq号
        this.botId = botInfo.id
        // 保存bot的主人
        this.owner = botInfo.owner
        // 循环填充group信息
        for (groupInfo in botInfo.groups){
            try{
                // 获取group
                val group = bot.getGroupOrFail(groupInfo.groupId)
                // 填充group信息
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
                group.sendMessage(Messages.RandomPic.sendPictureOnly(image))
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