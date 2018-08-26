package allawala.demo.user.service

import allawala.ResponseFE
import allawala.chassis.auth.model.{PrincipalType, RefreshToken}
import allawala.chassis.auth.service.TokenStorageService
import allawala.chassis.core.exception.ServerException
import allawala.demo.user.model.{UserRefreshToken, UserToken}
import allawala.demo.user.repository.UserTokenRepository
import javax.inject.{Inject, Named}

import scala.concurrent.{ExecutionContext, Future}

/*
  Only demonstrating creating/updating user tokens, so the principalType is ignored.
  Depending on how you generate service tokens, you may want to cater for this similar to how user tokens are handled
 */
class UserTokenServiceImpl @Inject() (userTokenRepository: UserTokenRepository)
                                     (@Named("blocking-fixed-pool-dispatcher") implicit val ec: ExecutionContext) extends TokenStorageService {
  override def storeTokens(principalType: PrincipalType, principal: String, jwtToken: String, refreshToken: Option[RefreshToken]): ResponseFE[Unit] = {
    Future {
      Right {
        userTokenRepository.create(principal, UserToken(jwtToken, refreshToken.map(transformToUserRefreshToken)))
      }
    }
  }

  override def lookupTokens(selector: String): ResponseFE[(String, RefreshToken)] = {
    Future {
      userTokenRepository.getBySelector(selector) match {
        case Some(ut) =>
          ut.refreshToken match {
            case Some(rt) => Right((ut.jwtToken, transformFromUserRefreshToken(rt)))
            case None => Left(ServerException("refresh.token.not.found"))
          }
        case None => Left(ServerException("refresh.token.not.found"))
      }
    }
  }

  override def rotateTokens(
                             principalType: PrincipalType, principal: String, oldJwtToken: String, jwtToken: String, oldRefreshToken: RefreshToken, refreshToken: RefreshToken
                           ): ResponseFE[Unit] = {
    Future {
      Right {
        userTokenRepository.update(
          principal,
          UserToken(oldJwtToken, Some(transformToUserRefreshToken(oldRefreshToken))),
          UserToken(jwtToken, Some(transformToUserRefreshToken(refreshToken)))
        )
      }
    }
  }

  override def removeTokens(principalType: PrincipalType, principal: String, jwtToken: String, refreshTokenSelector: Option[String]): ResponseFE[Unit] = {
    Future {
      Right {
        userTokenRepository.deleteToken(principal, jwtToken, refreshTokenSelector)
      }
    }
  }

  override def removeAllTokens(principalType: PrincipalType, principal: String): ResponseFE[Unit] = {
    Future {
      Right {
        userTokenRepository.deleteAllUserTokens(principal)
      }
    }
  }

  private def transformToUserRefreshToken(refreshToken: RefreshToken): UserRefreshToken = {
    UserRefreshToken(
      selector = refreshToken.selector,
      tokenHash = refreshToken.tokenHash,
      expiresIn = refreshToken.expires
    )
  }

  private def transformFromUserRefreshToken(userRefreshToken: UserRefreshToken): RefreshToken = {
    RefreshToken(
      selector = userRefreshToken.selector,
      tokenHash = userRefreshToken.tokenHash,
      expires = userRefreshToken.expiresIn,
      encodedSelectorAndToken = None
    )

  }
}
