package allawala.demo.auth.service

import javax.inject.Inject
import org.apache.shiro.authc.{AuthenticationInfo, AuthenticationToken, SaltedAuthenticationInfo}
import org.apache.shiro.authc.credential.CredentialsMatcher
import org.apache.shiro.codec.CodecSupport

class TestCredentialsMatcher @Inject()(val encryptionService: EncryptionService) extends CredentialsMatcher {
  override def doCredentialsMatch(token: AuthenticationToken, info: AuthenticationInfo): Boolean = {
    val bytes = info.asInstanceOf[SaltedAuthenticationInfo].getCredentialsSalt.getBytes
    val salt = new String(bytes, CodecSupport.PREFERRED_ENCODING)
    val unencrypted = new String(token.getCredentials.asInstanceOf[Array[Char]])
    val encrypted = encryptionService.encrypt(unencrypted, salt)

    val expected = new String(info.getCredentials.asInstanceOf[Array[Char]])
    encrypted == expected

  }
}
