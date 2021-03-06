package controllers.admin

import akka.util.Timeout
import controllers.BaseController
import models.sandbox.SandboxTask
import util.FutureUtils.defaultContext
import models.sandbox.SandboxTask.RunScheduledTask
import services.scheduled.ScheduledTask
import util.Application

import scala.concurrent.Future
import scala.concurrent.duration._

@javax.inject.Singleton
class SandboxController @javax.inject.Inject() (override val app: Application, scheduledTask: ScheduledTask) extends BaseController {
  RunScheduledTask.scheduledTask = Some(scheduledTask)

  implicit val timeout = Timeout(10.seconds)

  def list = withAdminSession("sandbox.list") { implicit request =>
    getAdminUser(request).map { u =>
      Ok(views.html.admin.sandbox.list(u))
    }
  }

  def sandbox(key: String) = withAdminSession("sandbox." + key) { implicit request =>
    val sandbox = SandboxTask.withValue(key)
    getAdminUser(request).flatMap { u =>
      sandbox.run(app).map { result =>
        Ok(views.html.admin.sandbox.run(u, sandbox, result))
      }
    }
  }
}
