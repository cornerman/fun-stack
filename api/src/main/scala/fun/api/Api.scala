package fun.api

trait Api[F[_]] {
  def get: F[Int]
  def increment: F[Unit]
}
