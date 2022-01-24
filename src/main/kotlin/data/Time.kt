package mirai.guyuemochen.chatbot.data

object Time {

    fun changeStringToTime(time: String): Long{

        try{
            if (time.endsWith("s")){
                return time.substring(0, time.length - 1).toLong() * 1000
            }
            else if (time.endsWith("m")){
                return time.substring(0, time.length - 1).toLong() * 1000 * 60
            }
            else if (time.endsWith("h")){
                return time.substring(0, time.length - 1).toLong() * 1000 * 60 * 60
            }
            else{
                return time.substring(0, time.length - 1).toLong()
            }
        }
        catch (e: Exception){
            return -1
        }

    }

}