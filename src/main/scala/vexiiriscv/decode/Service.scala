package vexiiriscv.decode

import spinal.core._
import spinal.core.fiber.{Lockable, Retainer}
import spinal.lib._
import spinal.lib.logic.Masked
import spinal.lib.misc.pipeline._
import vexiiriscv.riscv.{MicroOp, RegfileSpec, RfRead}

import scala.collection.mutable

trait DecoderService {
  val elaborationLock = Retainer()
  def covers() : Seq[Masked] //List of all instruction implemented

  def addMicroOpDecoding[T <: BaseType](microOp: MicroOp, key : Payload[T], value: T) : Unit = addMicroOpDecoding(microOp, DecodeList(key -> value))
  def addMicroOpDecoding(microOp: MicroOp, decoding: DecodeListType)
  def addMicroOpDecodingDefault(key : Payload[_ <: BaseType], value : BaseType) : Unit
}


trait AlignerService{
  val lastSliceData, firstSliceData = mutable.LinkedHashSet[NamedType[_ <: Data]]()
  val elaborationLock = Retainer()
}