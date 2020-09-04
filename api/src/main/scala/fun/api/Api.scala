package fun.api

trait Api[F[_]] {
  def getState: F[Int]
  def incrementValue: F[Unit]
}
