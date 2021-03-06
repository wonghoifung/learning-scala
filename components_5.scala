import scala.concurrent._
import java.util.concurrent.atomic._
import scala.collection._
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}
import scala.annotation.tailrec

object collectionTest extends App {

	class AtomicBuffer[T] {
		private val buffer = new AtomicReference[List[T]](Nil)
	    /*@tailrec */def +=(x:T):Unit = {
			val xs = buffer.get
			val nxs = x :: xs
			if(!buffer.compareAndSet(xs, nxs)){
				this += x
			}
		}
		override def toString():String = {
			buffer.toString()
		}
	}

	def execute(body: =>Unit) = ExecutionContext.global.execute(
		new Runnable{
			def run() = body
		})

	val buffer = mutable.ArrayBuffer[Int]()
	// val buffer = new AtomicBuffer[Int]()

	def asyncAdd(numbers:Seq[Int]) = execute {
		buffer.synchronized {
			buffer ++= numbers
			println(s"buffer = $buffer")
		}

		// for( i <- numbers) {
		// 	buffer += i
		// }
		// println(s"buffer = $buffer")
	}

	asyncAdd(0 until 10)
	asyncAdd(10 until 20)

	Thread.sleep(500)

	//////////////////////

	val queue = new LinkedBlockingQueue[String]
	for (i <- 1 to 5500) queue.offer(i.toString)
	execute {
		val it = queue.iterator
		while (it.hasNext) println(it.next())
	}
	for (i <- 1 to 5500) queue.poll()
	Thread.sleep(1000)

}