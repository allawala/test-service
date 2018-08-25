package allawala.demo.user.service

import allawala.ResponseFE
import allawala.chassis.auth.model.{PrincipalType, RefreshToken}
import allawala.chassis.auth.service.TokenStorageService

import scala.concurrent.Future

class UserTokenServiceImpl extends TokenStorageService {
  override def storeTokens(principalType: PrincipalType, principal: String, jwtToken: String, refreshToken: Option[RefreshToken]): ResponseFE[Unit] = {
    Future.successful(Right(()))
  }

  override def lookupTokens(selector: String): ResponseFE[(String, RefreshToken)] = ???

  override def rotateTokens(
                             principalType: PrincipalType, principal: String, oldJwtToken: String, jwtToken: String, oldRefreshToken: RefreshToken, refreshToken: RefreshToken
                           ): ResponseFE[Unit] = {
    Future.successful(Right(()))
  }

  override def removeTokens(principalType: PrincipalType, principal: String, jwtToken: String, refreshTokenSelector: Option[String]): ResponseFE[Unit] = {
    Future.successful(Right(()))
  }

  override def removeAllTokens(principalType: PrincipalType, principal: String): ResponseFE[Unit] = {
    Future.successful(Right(()))
  }
}
