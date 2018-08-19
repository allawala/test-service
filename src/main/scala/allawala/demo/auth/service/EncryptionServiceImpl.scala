package allawala.demo.auth.service

import java.nio.charset.StandardCharsets

import com.google.common.hash.Hashing

class EncryptionServiceImpl extends EncryptionService {
  // Again a very rudimentary encryption. For production look at something like bcrypt, scrypt etc
  override def encrypt(password: String, salt: String): String = {
    val sb = new StringBuilder
    sb.append(salt)
    sb.append(password)

    Hashing.sha256().hashString(sb.toString(), StandardCharsets.UTF_8).toString
  }
}
