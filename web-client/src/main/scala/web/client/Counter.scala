import outwatch._
import outwatch.dsl._

import colibri._
import cats.effect.IO

object Helpers {
  import outwatch.helpers._

  trait EmitterBuilderDispatcher[-T] {
    def dispatch[R](emitter: EmitterBuilder[T, R]): R
  }
  object EmitterBuilderDispatcher {
    def ofModelUpdate[M, T](subject: Subject[M], update: (T, M) => M) = new EmitterBuilderDispatcher[T] {
      def dispatch[R](emitter: EmitterBuilder[T, R]): R = emitter.withLatest(subject).map(update.tupled) --> subject
    }
  }

  @inline implicit final class AccessEnvironmentDispatchOperations[O, R[-_]](val builder: EmitterBuilder[O, R[Any]])(implicit acc: AccessEnvironment[R]) {
    @inline def dispatch: R[EmitterBuilderDispatcher[O]] = AccessEnvironment[R].access[EmitterBuilderDispatcher[O]](_.dispatch(builder))
  }
}

object Counter {
  import Helpers._

  def main(): Unit = {
    val subject = Subject.behavior(init)
    val dispatcher = EmitterBuilderDispatcher.ofModelUpdate(subject, update _)
    Outwatch.renderInto[IO]("#app", view(subject).provide(dispatcher)).unsafeRunSync()
  }

  type Model = Int

  def init: Model = 0

  sealed trait Msg
  case object Increment extends Msg
  case object Decrement extends Msg

  def update(msg: Msg, model: Model): Model =
    msg match {
      case Increment => model + 1
      case Decrement => model - 1
    }

  def view(model: Observable[Model]) =
    div(
      button("-", onClick.use(Decrement).dispatch),
      div(model.map(_.toString)),
      button("+", onClick.use(Increment).dispatch),
    )
}
