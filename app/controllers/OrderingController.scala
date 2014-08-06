package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick._
import models._

object OrderingController extends Controller {

  /**
   * 受注情報入力フォームの定義
   */
  val orderingForm = Form(
    mapping(
      "ID" -> longNumber,
      "createdAt" -> longNumber,
      "customerID" ->  longNumber,
      "itemID" -> longNumber,
      "itemCount" -> longNumber,
      "comment" -> text(maxLength = 140)
    )(Ordering.apply)(Ordering.unapply)
  )

  /**
   * 受注登録フォーム表示アクションメソッドの定義
   */
  def showCreateForm() = Action { request =>
    Ok(views.html.orderingCreateForm(orderingForm))
  }

  /**
   * 受注登録アクションメソッドの定義
   */
  def create() = DBAction { implicit rs =>
    orderingForm.bindFromRequest.fold(
      errors => BadRequest(views.html.orderingCreateForm(errors)),
      ordering => {
        OrderingDAO.create(ordering)
        Redirect(routes.OrderingController.search())
      }
    )
  }

  /**
   * 受注情報検索アクションメソッドの定義
   */
  def search(word: String) = DBAction { implicit rs =>
    Ok(views.html.orderingSearch(word, OrderingDAO.search(word)))
  }

  /**
   * 受注情報削除アクションメソッドの定義
   */
  def remove(ID: Long) = DBAction { implicit rs =>
    OrderingDAO.remove(OrderingDAO.searchByID(ID))
    Redirect(routes.OrderingController.search())
  }
}