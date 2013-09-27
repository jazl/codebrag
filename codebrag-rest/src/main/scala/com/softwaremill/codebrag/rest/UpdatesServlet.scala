package com.softwaremill.codebrag.rest

import com.softwaremill.codebrag.service.user.Authenticator
import org.joda.time.DateTime
import com.softwaremill.codebrag.dao.reporting.NotificationCountFinder
import org.bson.types.ObjectId

class UpdatesServlet(val authenticator: Authenticator, finder: NotificationCountFinder) extends JsonServletWithAuthentication {
  before() {
    haltIfNotAuthenticated()
  }

  get("/") {
    val counters = finder.getCounters(new ObjectId(user.id))
    UpdateNotification(new DateTime().getMillis, counters.pendingCommitCount, counters.followupCount)
  }

  get("/", sinceLastTime) {
    val counters = finder.getCountersSince(new DateTime(params.get("since").get.toLong), new ObjectId(user.id))
    UpdateNotification(new DateTime().getMillis, counters.pendingCommitCount, counters.followupCount)
  }

  private def sinceLastTime = params.get("since").isDefined
}

object UpdatesServlet {
  val Mapping = "updates"
}

case class UpdateNotification(lastUpdate: Long, commits: Long, followups: Long)