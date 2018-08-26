package allawala.demo.user.repository

import allawala.demo.user.model.UserToken

trait UserTokenRepository {
  def create(userUuid: String, userToken: UserToken): Unit
  def update(userUuid: String, oldUserToken: UserToken, newUserToken: UserToken): Unit
  def deleteToken(userUuid: String, jwtToken: String, selector: Option[String]): Unit
  def deleteAllUserTokens(userUuid: String): Unit
  def getBySelector(selector: String): Option[UserToken]
  def get(userUuid: String): Set[UserToken]
}
