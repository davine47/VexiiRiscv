package vexiiriscv.prediction

import spinal.core._
import spinal.lib._
import spinal.lib.misc.pipeline._
import spinal.lib.misc.plugin.FiberPlugin
import vexiiriscv.execute.BranchPlugin

import scala.collection.mutable

class LearnPlugin extends FiberPlugin with LearnService {
  override def getLearnPort(): Flow[LearnCmd] = logic.learn

  val logic = during build new Area {
    Prediction.BRANCH_HISTORY_WIDTH.set((0 +: host.list[HistoryUser].map(_.historyWidthUsed)).max)
    learnLock.await()

    val ups = host.list[BranchPlugin].map(_.logic.jumpLogic.learn)
    val learn = Flow(LearnCmd(learnCtxElements.toSeq))

    learn << StreamArbiterFactory().noLock.roundRobin.on(ups).toFlow
  }
}