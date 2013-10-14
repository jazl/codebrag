package com.softwaremill.codebrag.service.notification

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import com.typesafe.scalalogging.slf4j.Logging
import com.softwaremill.codebrag.dao.{UserDAO, HeartbeatStore}
import org.joda.time.DateTime
import org.bson.types.ObjectId
import com.softwaremill.codebrag.dao.reporting.NotificationCountFinder
import com.softwaremill.codebrag.dao.reporting.views.NotificationCountersView
import com.softwaremill.codebrag.domain.{LastUserNotificationDispatch, User}
import com.softwaremill.codebrag.common.Clock
import com.softwaremill.codebrag.service.config.CodebragConfig
import scala.concurrent.duration.FiniteDuration

class UserNotificationSenderActor(actorSystem: ActorSystem,
                                  heartbeatStore: HeartbeatStore,
                                  val notificationCounts: NotificationCountFinder,
                                  val userDAO: UserDAO,
                                  val clock: Clock,
                                  val notificationService: NotificationService,
                                  val config: CodebragConfig)
  extends Actor with Logging with UserNotificationsSender {

  import UserNotificationSenderActor._

  def receive = {
    case SendUserNotifications => {
      logger.debug("Preparing notifications to send out")
      sendUserNotifications(heartbeatStore.loadAll())
      scheduleNextNotificationsSendOut(actorSystem, self, config.notificationsCheckInterval)
    }

    case SendDailySummary => {
      logger.debug("Preparing daily summaries to send out")
      sendDailySummary(userDAO.findAll())
      scheduleNextDailySendOut(actorSystem, self, clock)
    }
  }

}

object UserNotificationSenderActor extends Logging {

  def initialize(actorSystem: ActorSystem,
                 heartbeatStore: HeartbeatStore,
                 notificationCountFinder: NotificationCountFinder,
                 userDAO: UserDAO,
                 clock: Clock,
                 notificationsService: NotificationService,
                 config: CodebragConfig) = {
    logger.debug("Initializing user notification system")
    val actor = actorSystem.actorOf(
      Props(new UserNotificationSenderActor(actorSystem, heartbeatStore, notificationCountFinder, userDAO, clock, notificationsService, config)),
      "notification-scheduler")

    scheduleNextNotificationsSendOut(actorSystem, actor, config.notificationsCheckInterval)
    scheduleNextDailySendOut(actorSystem, actor, clock)
  }

  private def scheduleNextDailySendOut(actorSystem: ActorSystem, receiver: ActorRef, clock: Clock) {
    import actorSystem.dispatcher
    import scala.concurrent.duration._

    val nextSendOutDate = clock.currentDateTime.withTimeAtStartOfDay().plusHours(9)
    val millisToNextSendOut = nextSendOutDate.getMillis - clock.currentTimeMillis

    actorSystem.scheduler.scheduleOnce(millisToNextSendOut.millis, receiver, SendDailySummary)
  }

  private def scheduleNextNotificationsSendOut(actorSystem: ActorSystem, receiver: ActorRef, interval: FiniteDuration) {
    import actorSystem.dispatcher
    logger.debug(s"Scheduling next preparation in $interval")
    actorSystem.scheduler.scheduleOnce(interval, receiver, SendUserNotifications)
  }


}

case object SendUserNotifications

case object SendDailySummary

trait UserNotificationsSender extends Logging {
  def notificationCounts: NotificationCountFinder

  def userDAO: UserDAO

  def clock: Clock

  def notificationService: NotificationService

  def config: CodebragConfig

  def sendUserNotifications(heartbeats: List[(ObjectId, DateTime)]) {
    def userIsOffline(heartbeat: DateTime) = heartbeat.isBefore(clock.currentDateTimeUTC.minus(config.userOfflinePeriod))

    var emailsScheduled = 0

    heartbeats.foreach {
      case (userId, lastHeartbeat) =>
        if (userIsOffline(lastHeartbeat)) {
          val counters = notificationCounts.getCountersSince(lastHeartbeat, userId)
          userDAO.findById(userId).foreach(user => {
            if (userShouldBeNotified(lastHeartbeat, user, counters)) {
              sendNotifications(user, counters)
              updateLastNotificationsDispatch(user, counters)
              emailsScheduled += 1
            }
          })
        }
    }

    logger.debug(s"Scheduled $emailsScheduled notification emails")
  }

  private def userShouldBeNotified(heartbeat: DateTime, user: User, counters: NotificationCountersView) = {
    val needsCommitNotification = counters.pendingCommitCount > 0 && (user.notifications match {
      case None => true
      case Some(LastUserNotificationDispatch(None, _)) => true
      case Some(LastUserNotificationDispatch(Some(date), _)) => date.isBefore(heartbeat)
    })

    val needsFollowupNotification = counters.followupCount > 0 && (user.notifications match {
      case None => true
      case Some(LastUserNotificationDispatch(_, None)) => true
      case Some(LastUserNotificationDispatch(_, Some(date))) => date.isBefore(heartbeat)
    })

    needsCommitNotification || needsFollowupNotification
  }

  private def sendNotifications(user: User, counter: NotificationCountersView) = {
    notificationService.sendCommitsOrFollowupNotification(user, counter.pendingCommitCount, counter.followupCount)
  }

  private def updateLastNotificationsDispatch(user: User, counters: NotificationCountersView) {
    val commitDate = if (counters.pendingCommitCount > 0) Some(clock.currentDateTimeUTC) else None
    val followupDate = if (counters.followupCount > 0) Some(clock.currentDateTimeUTC) else None
    if (commitDate.isDefined || followupDate.isDefined) {
      userDAO.rememberNotifications(user.id, LastUserNotificationDispatch(commitDate, followupDate))
    }
  }

  def sendDailySummary(users: List[User]) {
    users.foreach {
      user => {
        val counters = notificationCounts.getCounters(user.id)
        notificationService.sendCommitsOrFollowupNotification(user, counters.pendingCommitCount, counters.followupCount)
      }
    }
  }

}