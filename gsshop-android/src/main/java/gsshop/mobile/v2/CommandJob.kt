package gsshop.mobile.v2

class CommandJob     //TOMTV, IMG, IMGMS
//start, end, idle
constructor(@JvmField var jobName: String?, @JvmField var jobState: String?) {
    companion object {
        const val JOB_START = "START"
        const val JOB_IDLE = "IDLE"
        const val JOB_END = "END"
    }
}