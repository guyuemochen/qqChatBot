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
import java.util.*
import kotlin.NoSuchElementException
import kotlin.io.path.Path

import kotlin.io.path.exists

/** 储存bot的详细信息
 *
 * @author 古月莫辰
 *
 * @param bot net.mamoe.mirai.Bot类型
 * @param pluginPath 插件信息
 */
class BotInfo(
    val bot: Bot,
    private val pluginPath: Path,
) {
    var botId: Long = 0
    var owner: Long = 0
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
            createInfo(bot)
        }
        else{
            // 若存在则从现有文件中读取
            readJson(botPath, bot)
        }
    }

    /**
     * 初始化所有group信息
     *
     * @param bot 当前的bot
     */
    private fun createInfo(bot: Bot){
        // 获取当前bot信息
        this.botId = bot.id
        // 将当前bot主人设置为空
        this.owner = 0
        for (group in bot.groups){
            addNewGroup(group)
        }
    }

    /**
     * 获取当前json文件并转化为class形式
     *
     * @param botPath 当前bot所在文件地址
     * @param bot 当前的bot
     */
    private fun readJson(botPath: Path, bot: Bot){
        val botInfo = BotJson.getInfo(botPath, Constants.FileNames.botInfo)
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
                val task = newTask(group)
                this.groupInfoList.add(
                    GroupInfo(
                        groupInfo.groupId,
                        task,
                        groupInfo.randomDelay,
                        groupInfo.randomType,
                        groupInfo.status,
                        groupInfo.welcomeText,
                    )
                )
                if (groupInfo.randomType == Constants.RandomType.answerAfterDelay){
                    Timer().schedule(task, groupInfo.randomDelay)
                }
                else if(groupInfo.randomType == Constants.RandomType.repeatConstantDelay){
                    Timer().schedule(task, Date(), groupInfo.randomDelay)
                }
            }
            catch (e: NoSuchElementException){
                // 当出现群组不存在时跳过
                continue
            }
        }

        checkGroups()
    }

    /**
     * 检查群组是否都进行储存
     */
    private fun checkGroups(){

        for (group in bot.groups){

            if (findGroupIndexById(group.id, groupInfoList) == -1){
                addNewGroup(group)
            }

        }

    }

    /**
     * 添加新的群
     *
     * @param group 新的群信息
     */
    fun addNewGroup(group: Group){
        val task = newTask(group)
        this.groupInfoList.add(
            GroupInfo(
                group.id,
                task,
                Constants.GroupDataDefault.randomDelay,
                Constants.GroupDataDefault.randomType,
                Constants.BotStatus.on,
                Constants.GroupDataDefault.welcomeText,
            )
        )
        Timer().schedule(task, Constants.GroupDataDefault.randomDelay)
    }

    /**
     * 移除群组
     *
     * @param group 群组信息
     */
    fun removeGroup(group: Group){

        val groupInfo = this.groupInfoList[findGroupIndexById(group.id, groupInfoList)]
        groupInfo.task?.cancel()
        groupInfoList.remove(groupInfo)

    }

    /**
     * 保存当前所有bot信息
     */
    fun saveData(){
        val group: MutableList<BotJson.GroupInfo> = mutableListOf()
        // 保存所有群组信息
        for (groupInfo in this.groupInfoList){
            group.add(
                BotJson.GroupInfo(
                    groupId = groupInfo.id,
                    randomType = groupInfo.randomType,
                    randomDelay = groupInfo.randomDelay,
                    status = groupInfo.status,
                    welcomeText = groupInfo.welcomeText,
                )
            )
        }
        // 保存所有信息
        val botjson = BotJson.BaseInfo(
            id = this.botId,
            owner = this.owner,
            groups = group,
        )

        // 保存信息
        BotJson.inputInfo(Path("${pluginPath}/${this.botId}"), botjson, Constants.FileNames.botInfo)
    }

    /**
     * 重置对应group的task
     *
     * @param group 当前所在群组
     *
     * @return 返回bot在该群的randomType
     */
    fun refreshTask(group: Group){

        val groupInfoIndex = findGroupIndexById(group.id, this.groupInfoList)
        val groupInfo = this.groupInfoList[groupInfoIndex]
        if (groupInfo.status == Constants.BotStatus.off){
            groupInfo.task?.cancel()
        }
        else if (groupInfo.randomType == Constants.RandomType.answerAfterDelay){
            // 取消当前任务
            groupInfo.task?.cancel()
            // 添加新的任务
            groupInfo.task = newTask(group)
            Timer().schedule(groupInfo.task, groupInfo.randomDelay)
        }

    }

    /**
     * 判断是否在群
     *
     * @param id 群id
     *
     * @return true为在群中，false为不在当前群
     */
    fun containGroup(id: Long): Boolean{
        return findGroupIndexById(id, this.groupInfoList) != -1
    }

    /**
     * 更改bot在当前群中的状态
     *
     * @param id 当前的群id
     * @param status 更改后的状态
     *
     * @return -1 当前群不存在；-2 status重复；-3 status错误；1 更改成开启状态；2 更改成关闭状态
     */
    fun changeBotStatus(id: Long, status: String): Int{

        if (!containGroup(id)){
            return -1
        }
        val groupInfoIndex = findGroupIndexById(id, groupInfoList)
        val groupInfo = groupInfoList[groupInfoIndex]

        if (status == "on" && groupInfo.status == Constants.BotStatus.on){
            return -2
        }
        else if(status == "off" && groupInfo.status == Constants.BotStatus.off){
            return -2
        }
        else if(status == "on"){
            groupInfo.status = Constants.BotStatus.on
            return 1
        }
        else if(status == "off"){
            groupInfo.status = Constants.BotStatus.off
            return 2
        }
        else{
            return -3
        }

    }

    /**
     * 更改随机发图间隔时间
     *
     * @param groupId 群id
     * @param time 更改后时间
     */
    fun changeGroupRandomTime(groupId: Long, time: Long){
        val groupInfo = this.groupInfoList[findGroupIndexById(groupId, this.groupInfoList)]

        groupInfo.randomDelay = time
        if (groupInfo.randomType == Constants.RandomType.repeatConstantDelay){
            groupInfo.task?.cancel()
            groupInfo.task= bot.getGroup(groupId)?.let { newTask(it) }
            Timer().schedule(groupInfo.task, Date(), groupInfo.randomDelay)
        }
    }

    /**
     * 更改当前group的randomType并重置task
     *
     * @param groupId 群号
     * @param newType 新的类型
     *
     * @return 运行状态
     * 返回1：成功
     * 返回-1：无需调整
     * 返回-2：不存在该类型
     */
    fun changeRandomType(groupId: Long, newType: Int): Int{
        val groupInfo = this.groupInfoList[findGroupIndexById(groupId, groupInfoList)]
        if (groupInfo.randomType != newType){
            groupInfo.randomType = newType
            if(newType == Constants.RandomType.answerAfterDelay){
                // 因为之后会直接cancel再重置所以不用可以填任何内容进来
                return 1
            }
            else if(newType == Constants.RandomType.repeatConstantDelay){
                groupInfo.task?.cancel()
                groupInfo.task = bot.getGroup(groupId)?.let { newTask(it) }
                Timer().schedule(groupInfo.task, Date(), groupInfo.randomDelay)
                return 1
            }

            return -2
        }

        return -1
    }

    /**
     * 从所有群信息中找到对应groupInfo的Index
     *
     * @param groupId 群号
     * @param groupInfoList 所有群信息
     *
     * @return 返回对应群信息。若找不到则返回-1
     */
    private fun findGroupIndexById(groupId: Long, groupInfoList: MutableList<GroupInfo>): Int{

        if (groupInfoList.size == 1){
            if (groupInfoList[0].id == groupId) {
                return 0
            }
        }
        else if(groupInfoList.size > 1){
            val leftIndex = findGroupIndexById(groupId, groupInfoList.subList(0, groupInfoList.size/2))
            if (leftIndex != -1){
                return leftIndex
            }

            val rightIndex = findGroupIndexById(groupId, groupInfoList.subList(groupInfoList.size/2, groupInfoList.size))
            if (rightIndex != -1){
                return rightIndex + groupInfoList.size/2
            }
        }
        return -1

    }

    /**
     * 创建新的task
     *
     * @param group 当前bot所在组
     *
     * @return 设定好的task
     */
    private fun newTask(group: Group): SendPic {
        return SendPic(group, pluginPath)
    }

    /**
     * GroupInfo储存群组信息
     *
     * @param id 群号
     * @param task 定时发阿宋消息的任务
     * @param randomDelay 防冷群延迟
     * @param randomType 防冷群类型
     * @param status bot状态
     */
    class GroupInfo(
        val id: Long,
        var task: SendPic?,
        var randomDelay: Long,
        var randomType: Int,
        var status: Int,
        var welcomeText: String,
    )

    /**
     * 基础task，定时发送图片
     *
     * @oaram group 选定群组
     * @param pluginPath 当前插件文件储存地址
     */
    class SendPic(private val group: Group, private val pluginPath: Path): TimerTask(){

        @OptIn(DelicateCoroutinesApi::class)
        override fun run(){

            GlobalScope.launch{
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

    }
}