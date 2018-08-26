package allawala.demo.user.model

import java.time.Instant

case class UserRefreshToken(selector: String, tokenHash: String, expiresIn: Instant)
case class UserToken(jwtToken: String, refreshToken: Option[UserRefreshToken])
