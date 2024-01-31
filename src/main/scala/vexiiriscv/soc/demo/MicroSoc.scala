package vexiiriscv.soc.demo

import spinal.core._
import spinal.core.fiber._
import spinal.lib.StreamPipe
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.bus.tilelink
import spinal.lib.bus.tilelink._
import spinal.lib.bus.tilelink.coherent.{CacheFiber, HubFiber}
import spinal.lib.bus.tilelink.fabric.Node
import spinal.lib.com.uart.TilelinkUartFiber
import spinal.lib.misc.TilelinkClintFiber
import spinal.lib.misc.plic.TilelinkPlicFiber
import spinal.lib.system.tag.PMA
import vexiiriscv.soc.TilelinkVexiiRiscvFiber

// SocDemo is a little SoC made only for simulation purposes.
class MicroSoc() extends Component {
  val mainBus = tilelink.fabric.Node()

  // Create a few NaxRiscv cpu
  val cpu = new TilelinkVexiiRiscvFiber()
  mainBus << List(cpu.iBus, cpu.dBus)

  // Create a tilelink memory bus which will get out of the SoC to connect the main memory
  val ram = new tilelink.fabric.RamFiber()
  ram.up at SizeMapping(0x80000000l, 0x10000l) of mainBus

  // Handle all the IO / Peripheral things
  val peripheral = new Area {
    val slowBus = Node()
    slowBus at (0x10000000l, 0x10000000l)  of (mainBus)

    val clint = new TilelinkClintFiber()
    clint.node at 0x10000 of slowBus

    val plic = new TilelinkPlicFiber()
    plic.node at 0xC00000l of slowBus

    val uart = new TilelinkUartFiber()
    uart.node at 0x1000 of slowBus

    val plicEnd = plic.createInterruptSlave(1)
    plicEnd << uart.interrupt

    cpu.bind(clint)
    cpu.bind(plic)
  }
}

object MicroSoc extends App{
  val sc = SpinalConfig(defaultClockDomainFrequency = FixedFrequency(100 MHz))
  sc.generateVerilog(new MicroSoc())
}