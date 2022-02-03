package gsshop.mobile.v2

import java.util.*

class CommandJobThreadPool(private val arrJob: ArrayList<CommandJob>) {
    /**
     * 스레드풀의 Job을 넣는행위
     * @param job
     * @return -1(maxSize를 초과한경우), 사이즈를 보통 리턴함
     */
    fun addJob(job: CommandJob): Boolean {
        return arrJob.add(job)
    }

    fun removeJob(jobName: String?): Boolean {
        for (i in arrJob.indices) {
            if (jobName != null && arrJob[i].jobName != null && jobName == arrJob[i].jobName) {
                arrJob.removeAt(i)
                return true
            }
        }
        return false
    }

    fun getSize(): Int = arrJob.size
}