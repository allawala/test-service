package allawala.demo.auth.realm

import allawala.chassis.auth.model.PrincipalType
import allawala.chassis.auth.shiro.model.{JWTAuthenticationToken, Principal}
import allawala.chassis.auth.shiro.realm.JWTRealm
import allawala.demo.user.repository.{UserRepository, UserTokenRepository}
import javax.inject.Inject
import org.apache.shiro.authc.{AuthenticationException, AuthenticationInfo, AuthenticationToken, SimpleAccount}
import org.apache.shiro.authz.{AuthorizationInfo, SimpleAuthorizationInfo}
import org.apache.shiro.subject.PrincipalCollection
import scala.collection.JavaConverters._

class JWTAuthRealm @Inject() (userTokenRepository: UserTokenRepository, userRepository: UserRepository) extends JWTRealm {
  private val AllPermissions = "*:*:*"
  /*
   NOTE!
   Token signature and expiration is already checked by this point.

   - If the refresh token is disabled and the token is expired, this method will not be called as the request will be rejected before this point

   - If the refresh token is enabled and the JWT is expired, then only when the tokens are meant to be reissued, this method will be called with that expired token as the credentials.

     The reason that is passes the expired token for that request is that the new tokens are only reissued only if the old token passes all the non expiration related authentication.
     Once the tokens are rotated, then this will be called with the non expired tokens as per normal
  */
  override def doGetAuthenticationInfo(authenticationToken: AuthenticationToken): AuthenticationInfo = {
    val token = authenticationToken.asInstanceOf[JWTAuthenticationToken]
    val principal = token.getPrincipal().asInstanceOf[Principal]

    val principalType = principal.principalType

    /*
      Here we are choosing to forego any further authentication on a service token, which has already been checked for expiration by this point.
      This is where you would add additional checks if you wanted
    */
    if (principalType == PrincipalType.Service) {
      new SimpleAccount(token.getPrincipal, token.getCredentials, getName)
    } else {
      val userTokens = userTokenRepository.get(principal.principal)
      if (userTokens.isEmpty) {
        /*
         Since we are in Shiro realm outside our futures and eithers, we throw the Shiro AuthenticationException here as normal
         This will automatically be converted to the Domain specific AuthenticationException, logged and generate the standard error response
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
    val principal = principals.getPrimaryPrincipal.asInstanceOf[Principal]

    // In this example we allow a service to perform any action, you may want to limit as needed
    val (roleNames, permissions) = if (principal.principalType == PrincipalType.Service) {
      (Set.empty[String], Set(AllPermissions))
    } else {
      userRepository.getByEmailOpt(principal.principal) match {
        case Some(user) => (user.roles, user.permissions.map(_.permissionString))
        case None => throw new AuthenticationException("user not found")
      }
    }
    val info = new SimpleAuthorizationInfo(roleNames.asJava)
    info.setStringPermissions(permissions.asJava)
    info
  }
}
