package mirai.guyuemochen.chatbot.data

class CmdDescription(
    val cmd: String,
    val description: String,
    val detailedDescription: String,
    val nextArgs: List<CmdDescription> = listOf(),
){
    private val commandNotExist = "当前指令不存在"

    fun getDepth(): Int{
        if (nextArgs.isEmpty()){
            return 1
        }

        val depths : MutableList<Int> = mutableListOf()
        for (arg in nextArgs){
            depths.add(arg.getDepth())
        }

        return getLargest(depths)
    }

    fun getDescription(depth: Int, msgList: List<String>): String{
        if (depth == 1){
            return detailedDescription
        }
        for (arg in nextArgs){
            if (arg.cmd == msgList[msgList.size - depth]){
                return arg.getDescription(depth + 1, msgList)
            }
        }
        return commandNotExist
    }
    
    private fun getLargest(nums: MutableList<Int>): Int {
        var largest = 0
        for (num in nums){
            if (num > largest){
                largest = num
            }
        }

        return largest
    }
}