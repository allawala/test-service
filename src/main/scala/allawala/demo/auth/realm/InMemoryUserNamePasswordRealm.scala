package allawala.demo.auth.realm

import allawala.chassis.auth.shiro.realm.UsernamePasswordRealm
import allawala.demo.auth.service.TestCredentialsMatcher
import allawala.demo.user.repository.UserRepository
import javax.inject.Inject
import org.apache.shiro.authc._
import org.apache.shiro.util.ByteSource

class InMemoryUserNamePasswordRealm @Inject()(matcher: TestCredentialsMatcher, userRepository: UserRepository) extends UsernamePasswordRealm {
  setCredentialsMatcher(matcher)

  override def doGetAuthenticationInfo(authenticationToken: AuthenticationToken): AuthenticationInfo = {
    val token = authenticationToken.asInstanceOf[UsernamePasswordToken]

    userRepository.getByEmailOpt(token.getUsername) match {
      case Some(userEntity) =>
        val info = new SimpleAuthenticationInfo(userEntity.email, userEntity.encryptedPassword.toCharArray, getName)
        info.setCredentialsSalt(ByteSource.Util.bytes(userEntity.salt))
        info
      case None => throw new AuthenticationException("user not found")
    }
  }
}
