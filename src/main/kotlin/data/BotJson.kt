package mirai.guyuemochen.chatbot.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import mirai.guyuemochen.chatbot.classes.RWData
import java.nio.file.Path

/**
 * 读写存储bot信息的json文件
 *
 * @author 古月漠尘
 */
object BotJson {

    object GroupListSerializer : JsonTransformingSerializer<List<GroupInfo>>(ListSerializer(GroupInfo.serializer())) {
        // If response is not an array, then it is a single object that should be wrapped into the array
        override fun transformDeserialize(element: JsonElement) =
            if (element !is JsonArray) JsonArray(listOf(element)) else element
    }

    // 基础信息
    @Serializable
    data class BaseInfo(
        val id: Long,
        var owner: Long,
        @Serializable(with = GroupListSerializer::class)
        var groups: List<GroupInfo>
    )

    /**
     * 储存group信息
     *
     * @param groupId 群号
     * @param randomType 随机发图类型
     * @param randomDelay 随机发图延迟
     * @param status bot状态
     * @param welcomeText 欢迎词
     */
    @Serializable
    data class GroupInfo(
        val groupId: Long,
        var randomType: Int,
        var randomDelay: Long,
        var status: Int,
        var welcomeText: String,
    )

    /**
     * 提取json文件中的信息并放回
     *
     * @param path 文件路径
     * @param filename 文件名称
     *
     * @return 以json格式返回bot信息
     */
    fun getInfo(path: Path, filename: String): BaseInfo{
        return Json.decodeFromString(RWData.readTxtFile(path, filename))
    }

    /**
     * 输入到json文件
     *
     * @param path 文件路径
     * @param botInfo json格式的文件
     * @param filename 文件名称
     */
    fun inputInfo(path: Path, botInfo: BaseInfo, filename: String){
        RWData.writeTxtFile(Json.encodeToString(botInfo), path, filename, false)
    }

}