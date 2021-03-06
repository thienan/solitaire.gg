package controllers.admin

import controllers.BaseController
import models.rules.{GameRules, GameRulesSet}
import play.api.http.FileMimeTypes
import services.history.GameStatisticsService
import util.Application
import util.FutureUtils.defaultContext

import scala.concurrent.Future

@javax.inject.Singleton
class RulesController @javax.inject.Inject() (override val app: Application, implicit val fmt: FileMimeTypes) extends BaseController {
  def rulesList(q: String, sortBy: String) = withAdminSession("list") { implicit request =>
    val statuses = GameRulesSet.all.map(getStatus)
    val filtered = if (q.nonEmpty) {
      statuses.filter(s => s._1.id.toLowerCase.contains(q) || s._1.title.toLowerCase.contains(q))
    } else {
      statuses
    }
    val sorted = sort(sortBy, filtered)
    Future.successful(Ok(views.html.admin.rules.rulesList(q, sortBy, sorted.size, 0, sorted)))
  }

  def rulesData = withAdminSession("data") { implicit request =>
    Future.successful(Ok(views.html.admin.rules.rulesDataList(GameRulesSet.all)))
  }

  def rulesDetail(id: String) = withAdminSession("detail") { implicit request =>
    val rules = GameRulesSet.allByIdWithAliases(id)
    GameStatisticsService.getStatistics(id).map { stats =>
      Ok(views.html.admin.rules.rulesDetail(rules, stats))
    }
  }

  def rulesScreenshot(id: String) = withAdminSession("screenshot") { implicit request =>
    val filename = s"./offline/build/screenshots/$id-hd.png"
    Future.successful(Ok.sendFile(new java.io.File(filename)))
  }

  private[this] def sort(order: String, rs: Seq[(GameRules, Boolean)]) = order match {
    case "title" => rs.sortBy(_._1.title)
    case "status" => rs.sortBy(x => (!x._2, x._1.title))
  }

  private[this] def getStatus(r: GameRules) = {
    val completed = GameRulesSet.completed.exists(_._2 == r)
    (r, completed)
  }
}
