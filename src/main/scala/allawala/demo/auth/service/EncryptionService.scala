package allawala.demo.auth.service

trait EncryptionService {
  def encrypt(password: String, salt: String): String
}
