package fun.api

trait Api[F[_]] {
  def getState: F[Int]
  def sendCommand(command: Command): F[Unit]
}
