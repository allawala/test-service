package allawala.demo.auth.realm

import allawala.chassis.auth.model.PrincipalType
import allawala.chassis.auth.shiro.model.{JWTAuthenticationToken, Principal}
import allawala.chassis.auth.shiro.realm.JWTRealm
import allawala.demo.user.repository.UserTokenRepository
import javax.inject.Inject
import org.apache.shiro.authc.{AuthenticationException, AuthenticationInfo, AuthenticationToken, SimpleAccount}
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.subject.PrincipalCollection

class JWTAuthRealm @Inject() (userTokenRepository: UserTokenRepository) extends JWTRealm {
  /*
   Token signature and expiration is already checked by this point.
   If refresh token semantics are allowed, the request that causes the token to be reissued allows is allowed to authenticate with the expired token with a valid signature.
   The reason for that is that the we dont reissue the token until we validate that the old token passes all non expiration related authentication checks
  */
  override def doGetAuthenticationInfo(authenticationToken: AuthenticationToken): AuthenticationInfo = {
    val token = authenticationToken.asInstanceOf[JWTAuthenticationToken]
    val principal = token.getPrincipal().asInstanceOf[Principal]

    val principalType = principal.principalType

    /*
     Only demonstrating creating/updating user tokens, so the principalType service is ignored.
     Depending on how you generate service tokens, you may want to cater for this similar to how user tokens are handled
    */
    if (principalType == PrincipalType.Service) {
      new SimpleAccount(token.getPrincipal, token.getCredentials, getName)
    } else {
      val userTokens = userTokenRepository.get(principal.principal)
      if (userTokens.isEmpty) {
        /*
         Since we are in Shiro realm outside our futures and eithers, we throw the Shiro AuthenticationException here as normal which will be converted to the Domain specific
         AuthenticationException and converted to the standard error response payload
        */
        throw new AuthenticationException("user has no active tokens")
      } else {
        // You may also want to check if the user is active or not
        val passedInToken = token.getCredentials().asInstanceOf[String]
        userTokens.find(_.jwtToken == passedInToken) match {
          case Some(_) => new SimpleAccount(token.getPrincipal, token.getCredentials, getName)
          case None => throw new AuthenticationException("user token not valid")
        }
      }
    }
  }

  override def doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo = {
    super.doGetAuthorizationInfo(principals)
  }
}
