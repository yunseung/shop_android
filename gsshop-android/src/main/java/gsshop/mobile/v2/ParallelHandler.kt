package gsshop.mobile.v2

import android.os.Handler
import android.os.Message
import com.gsshop.mocha.pattern.chain.CommandExecutor
import java.util.*

class ParallelHandler(executor: CommandExecutor?) : Handler() {
    private val threadPool: CommandJobThreadPool?
    private var commandExecutor: CommandExecutor? = null

    var state:String? = POOL_EMPTY //INPUT, FULL

    //End가 아닌상태면 1초 기다리도록
    private var timerTask: TimerTask? = null

    // 기존 자바의 getPoolSize
    fun getPoolSize() : Int? = threadPool?.getSize()

    init {
        threadPool = CommandJobThreadPool(ArrayList())
        state = POOL_EMPTY
        commandExecutor = executor
        executeFlag = true
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val job = msg.obj as CommandJob
        if (CommandJob.JOB_START == job.jobState) {
            //JOB_START 내부에서 잡 증가,
            threadPool?.addJob(job)
        } else if (CommandJob.JOB_END == job.jobState) {
            //JOB_END 내부에서 잡 감소
            threadPool?.removeJob(job.jobName)

            //잡이 0개 이고, 이미 할일이 다 채워진 상태라면 커맨드를 종료 해도 된다. 사용하지 않음
            if (threadPool?.getSize() == 0 && POOL_FULL == state) {

                //넥스체인이 실행된적이 없다.
                if (executeFlag) {
                    //막아,
                    executeFlag = false
                    commandExecutor?.execute()
                }
            }
        }
    }

    /**
     * 큐잉
     * @param jobName
     * @param jobState
     */
    fun sendMessage(jobName: String?, jobState: String?) {
        val msg = Message()
        val job = CommandJob(jobName, jobState)
        msg.obj = job
        handleMessage(msg)
    }

    /**
     * 딜레이 만큼 다음 커맨드 실행,
     * @param delay
     */
    fun executeTask(delay: Int) {
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
        timerTask = object : TimerTask() {
            override fun run() {
                if (executeFlag) {
                    executeFlag = false
                    commandExecutor?.execute()
                }
            }
        }
        Timer().schedule(timerTask, delay.toLong())
    }

    companion object {
        @JvmField
        var POOL_EMPTY = "EMPTY"
        @JvmField
        var POOL_INPUT = "INPUT"
        @JvmField
        var POOL_FULL = "FULL"

        //END 커맨드와 , 패러럴에 들어 있는 잡이 끝난 시점에서 누가 next chain 을 수행했는지 확인하는 플래그
        @JvmField
        var executeFlag = true
    }
}