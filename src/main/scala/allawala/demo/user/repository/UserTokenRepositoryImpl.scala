package allawala.demo.user.repository

import allawala.demo.user.model.UserToken

import scala.collection.mutable.{Map => MutMap}

class UserTokenRepositoryImpl extends UserTokenRepository {
  private val tokens: MutMap[String, Set[UserToken]] = MutMap.empty // Map of userUuid -> Set[UserTokens]
  // Normally we would be running an sql query but in our case we have this helper map to speed up indirect lookup
  private val selectorMap: MutMap[String, UserToken] = MutMap.empty // Map of selector -> userToken

  def create(userUuid: String, userToken: UserToken): Unit = {
    tokens.get(userUuid) match {
      case Some(t) =>
        addToHelperMaps(userToken)
        tokens += (userUuid -> (t + userToken))
      case None =>
        addToHelperMaps(userToken)
        tokens += (userUuid -> Set(userToken))
    }
  }

  override def update(userUuid: String, oldUserToken: UserToken, newUserToken: UserToken): Unit = {
    deleteToken(userUuid, oldUserToken.jwtToken, newUserToken.refreshToken.map(_.selector))
    create(userUuid, newUserToken)
  }

  override def deleteToken(userUuid: String, jwtToken: String, selector: Option[String]): Unit = {
    // remove from the selector map if refresh token was present
    selector.foreach(s => selectorMap -= s)
    tokens.remove(userUuid) match {
      case Some(t) =>
        // remove the token
        val updatedTokens = t.filterNot(_.jwtToken == jwtToken)
        // add the rest back
        tokens += (userUuid -> updatedTokens)
      case None => // Do nothing, might have already been removed
    }
  }

  override def deleteAllUserTokens(userUuid: String): Unit = {
    tokens.remove(userUuid).foreach(_.foreach(_.refreshToken.foreach(rt => selectorMap -= rt.selector)))
  }

  override def getBySelector(selector: String): Option[UserToken] = selectorMap.get(selector)


  override def get(userUuid: String): Set[UserToken] = tokens.getOrElse(userUuid, Set.empty)

  private def addToHelperMaps(userToken: UserToken) = {
    // if there is a refresh token, add to the selector map
    userToken.refreshToken.foreach(_ => selectorMap += (userToken.jwtToken -> userToken))
  }
}
