import akka.Done
import io.reactivex.{BackpressureStrategy, Completable, Single}
import monix.eval.Task
import monix.reactive.Observable

import scala.concurrent.duration.FiniteDuration

package object app {

  object Implicits {

    implicit class SingleAsTask[T](s: io.reactivex.Single[T]) {
      def asTask: Task[T] = Observable.fromReactivePublisher(s.toFlowable).firstL
    }

    implicit class CompletableAsTask[T](s: io.reactivex.Completable) {
      def asTask: Task[Done] = SingleAsTask(s.toSingleDefault(Done)).asTask
    }

    implicit class ObservableAsMonix[T](s: io.reactivex.Observable[T]) {
      def asObservable: Observable[T] = Observable.fromReactivePublisher(s.toFlowable(BackpressureStrategy.BUFFER))
    }

    def retryBackoff[A](source: Task[A],
                        maxRetries: Int, firstDelay: FiniteDuration): Task[A] = {
      source.onErrorHandleWith {
        case ex: Exception =>
          if (maxRetries > 0)
          // Recursive call, it's OK as Monix is stack-safe
            retryBackoff(source, maxRetries - 1, firstDelay * 2)
              .delayExecution(firstDelay)
          else
            Task.raiseError(ex)
      }
    }
  }

}


