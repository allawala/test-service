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
        // You may want to check if the account is active etc
        val info = new SimpleAuthenticationInfo(userEntity.email, userEntity.encryptedPassword.toCharArray, getName)
        info.setCredentialsSalt(ByteSource.Util.bytes(userEntity.salt))
        info
      /*
       Since we are in Shiro realm outside our futures and eithers, we throw the Shiro AuthenticationException here as normal which will be converted to the Domain specific
       AuthenticationException and converted to the standard error response payload
      */
      case None => throw new AuthenticationException("user not found")
    }
  }
}
