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

class BotJson {

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

    @Serializable
    data class GroupInfo(
        // 群id
        val groupId: Long,
        // 随机发图类型
        var randomType: Int,
        // 随机发图延迟
        var randomDelay: Long,
        // bot状态
        var status: String,
        // 欢迎词
        var welcomeText: String
    )

    fun getInfo(botPath: Path): BaseInfo{
        return Json.decodeFromString(RWData().readTxtFile(botPath, "botInfo.json"))
    }

    fun inputInfo(botPath: Path, botInfo: BaseInfo){
        RWData().writeTxtFile(Json.encodeToString(botInfo), botPath, "botInfo.json", false)
    }

}